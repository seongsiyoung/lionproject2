package com.example.lionproject2backend.settlement.service;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.domain.SettlementTarget;
import com.example.lionproject2backend.settlement.dto.SettlementAggregationRow;
import com.example.lionproject2backend.settlement.repository.SettlementDetailRepository;
import com.example.lionproject2backend.settlement.repository.SettlementRepository;
import com.example.lionproject2backend.settlement.repository.SettlementTargetRepository;
import com.example.lionproject2backend.user.domain.User;
import com.example.lionproject2backend.user.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettlementItemWriterTest {

    @Mock
    private SettlementRepository settlementRepository;

    @Mock
    private SettlementDetailRepository settlementDetailRepository;

    @Mock
    private SettlementTargetRepository settlementTargetRepository;

    @InjectMocks
    private SettlementItemWriter settlementItemWriter;

    @Test
    void 기존_PENDING_정산이_있으면_새로_저장하지_않고_재사용한다() {
        Mentor mentor = createMentor(1L);
        Settlement existing = createSettlement(mentor, 10000, 1000, 9000);
        ReflectionTestUtils.setField(existing, "id", 100L);
        Settlement candidate = createSettlement(mentor, 20000, 2000, 18000);
        SettlementTarget target = SettlementTarget.create(1L, "2026-07", 10L);
        ReflectionTestUtils.setField(settlementItemWriter, "jobInstanceId", 10L);

        when(settlementRepository.findByMentorAndSettlementPeriod(mentor, YearMonth.of(2026, 7)))
                .thenReturn(Optional.of(existing));
        when(settlementDetailRepository.findSettlementAggregationForRecalculation(
                anyLong(),
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(Optional.of(new SettlementAggregationRow(1L, 30000L, 1000L, 3000L, 26000L)));
        when(settlementTargetRepository.findByJobInstanceIdAndMentorId(10L, 1L))
                .thenReturn(Optional.of(target));

        settlementItemWriter.write(Chunk.of(candidate));

        verify(settlementRepository, never()).saveAll(any());
        verify(settlementRepository, never()).save(any());
        verify(settlementDetailRepository).bulkUpdateSettlementId(
                anyLong(),
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );

        ArgumentCaptor<Long> settlementIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(settlementDetailRepository).bulkUpdateSettlementId(
                settlementIdCaptor.capture(),
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
        assertThat(settlementIdCaptor.getValue()).isEqualTo(100L);
        assertThat(existing.getTotalPaymentAmount()).isEqualTo(30000);
        assertThat(existing.getRefundAmount()).isEqualTo(1000);
        assertThat(existing.getPlatformFee()).isEqualTo(3000);
        assertThat(existing.getSettlementAmount()).isEqualTo(26000);
        assertThat(target.getStatus()).isEqualTo(SettlementTarget.SettlementTargetStatus.DONE);
    }

    @Test
    void 기존_COMPLETED_정산이_있으면_정산을_수정하지_않고_target을_FAILED로_남긴다() {
        Mentor mentor = createMentor(1L);
        Settlement existing = createSettlement(mentor, 10000, 1000, 9000);
        existing.complete();
        ReflectionTestUtils.setField(existing, "id", 100L);
        Settlement candidate = createSettlement(mentor, 20000, 2000, 18000);
        SettlementTarget target = SettlementTarget.create(1L, "2026-07", 10L);
        ReflectionTestUtils.setField(settlementItemWriter, "jobInstanceId", 10L);

        when(settlementRepository.findByMentorAndSettlementPeriod(mentor, YearMonth.of(2026, 7)))
                .thenReturn(Optional.of(existing));
        when(settlementTargetRepository.findByJobInstanceIdAndMentorId(10L, 1L))
                .thenReturn(Optional.of(target));

        settlementItemWriter.write(Chunk.of(candidate));

        verify(settlementRepository, never()).saveAll(any());
        verify(settlementRepository, never()).save(any());
        verify(settlementDetailRepository, never()).bulkUpdateSettlementId(
                anyLong(),
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
        assertThat(existing.getTotalPaymentAmount()).isEqualTo(10000);
        assertThat(target.getStatus()).isEqualTo(SettlementTarget.SettlementTargetStatus.FAILED);
    }

    private Mentor createMentor(Long id) {
        User user = User.create("mentor@example.com", "password", "mentor", UserRole.MENTOR);
        Mentor mentor = new Mentor(user, "career");
        ReflectionTestUtils.setField(mentor, "id", id);
        return mentor;
    }

    private Settlement createSettlement(Mentor mentor, int totalPaymentAmount, int platformFee, int settlementAmount) {
        return Settlement.create(
                mentor,
                YearMonth.of(2026, 7),
                totalPaymentAmount,
                platformFee,
                settlementAmount,
                0,
                0,
                settlementAmount,
                0
        );
    }
}
