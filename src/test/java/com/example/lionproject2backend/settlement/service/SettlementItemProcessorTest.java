package com.example.lionproject2backend.settlement.service;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.mentor.repository.MentorRepository;
import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.domain.SettlementTarget;
import com.example.lionproject2backend.settlement.dto.SettlementAggregationRow;
import com.example.lionproject2backend.settlement.exception.SettlementTargetSkippableException;
import com.example.lionproject2backend.settlement.repository.SettlementDetailRepository;
import com.example.lionproject2backend.settlement.repository.SettlementRepository;
import com.example.lionproject2backend.settlement.repository.SettlementTargetRepository;
import com.example.lionproject2backend.user.domain.User;
import com.example.lionproject2backend.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettlementItemProcessorTest {

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private SettlementRepository settlementRepository;

    @Mock
    private SettlementDetailRepository settlementDetailRepository;

    @Mock
    private SettlementTargetRepository settlementTargetRepository;

    @InjectMocks
    private SettlementItemProcessor settlementItemProcessor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(settlementItemProcessor, "settlementPeriodStr", "2026-07");
    }

    @Test
    void READY_target은_PROCESSING으로_변경한_뒤_정산_후보를_반환한다() {
        SettlementTarget target = SettlementTarget.create(1L, "2026-07", 10L);
        Mentor mentor = createMentor();
        SettlementAggregationRow row = new SettlementAggregationRow(1L, 20000L, 0L, 2000L, 18000L);

        when(settlementDetailRepository.findSettlementAggregationByMentorAndPeriod(
                eq(1L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(Optional.of(row));
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(settlementRepository.findByMentorAndSettlementPeriod(mentor, YearMonth.of(2026, 6)))
                .thenReturn(Optional.empty());

        Settlement settlement = settlementItemProcessor.process(target);

        assertThat(target.getStatus()).isEqualTo(SettlementTarget.SettlementTargetStatus.PROCESSING);
        assertThat(settlement).isNotNull();
        assertThat(settlement.getSettlementAmount()).isEqualTo(18000);
    }

    @Test
    void 집계_결과가_없으면_target을_SKIPPED로_변경하고_null을_반환한다() {
        SettlementTarget target = SettlementTarget.create(1L, "2026-07", 10L);

        when(settlementDetailRepository.findSettlementAggregationByMentorAndPeriod(
                eq(1L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(Optional.empty());

        Settlement settlement = settlementItemProcessor.process(target);

        assertThat(target.getStatus()).isEqualTo(SettlementTarget.SettlementTargetStatus.SKIPPED);
        assertThat(settlement).isNull();
        verify(settlementTargetRepository).save(target);
    }

    @Test
    void 처리_가능하지_않은_target은_null을_반환한다() {
        SettlementTarget target = SettlementTarget.create(1L, "2026-07", 10L);
        target.markAsDone();

        Settlement settlement = settlementItemProcessor.process(target);

        assertThat(settlement).isNull();
        verify(settlementDetailRepository, never()).findSettlementAggregationByMentorAndPeriod(
                any(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
    }

    @Test
    void 집계는_있지만_멘토가_없으면_target_local_skip_예외를_던진다() {
        SettlementTarget target = SettlementTarget.create(1L, "2026-07", 10L);
        SettlementAggregationRow row = new SettlementAggregationRow(1L, 20000L, 0L, 2000L, 18000L);

        when(settlementDetailRepository.findSettlementAggregationByMentorAndPeriod(
                eq(1L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(Optional.of(row));
        when(mentorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> settlementItemProcessor.process(target))
                .isInstanceOf(SettlementTargetSkippableException.class)
                .hasMessageContaining("정산 대상 멘토를 찾을 수 없습니다");
    }

    @Test
    void 집계_필수값이_null이면_target_local_skip_예외를_던지고_멘토를_조회하지_않는다() {
        SettlementTarget target = SettlementTarget.create(1L, "2026-07", 10L);
        SettlementAggregationRow row = new SettlementAggregationRow(1L, 20000L, 0L, 2000L, null);

        when(settlementDetailRepository.findSettlementAggregationByMentorAndPeriod(
                eq(1L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(Optional.of(row));

        assertThatThrownBy(() -> settlementItemProcessor.process(target))
                .isInstanceOf(SettlementTargetSkippableException.class)
                .hasMessageContaining("정산 집계 결과가 유효하지 않습니다");
        verify(mentorRepository, never()).findById(any());
    }

    @Test
    void 집계_금액이_int_범위를_넘으면_target_local_skip_예외를_던지고_멘토를_조회하지_않는다() {
        SettlementTarget target = SettlementTarget.create(1L, "2026-07", 10L);
        SettlementAggregationRow row = new SettlementAggregationRow(
                1L,
                (long) Integer.MAX_VALUE + 1,
                0L,
                2000L,
                18000L
        );

        when(settlementDetailRepository.findSettlementAggregationByMentorAndPeriod(
                eq(1L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(Optional.of(row));

        assertThatThrownBy(() -> settlementItemProcessor.process(target))
                .isInstanceOf(SettlementTargetSkippableException.class)
                .hasMessageContaining("정산 집계 결과가 유효하지 않습니다");
        verify(mentorRepository, never()).findById(any());
    }

    private Mentor createMentor() {
        User user = User.create("mentor@example.com", "password", "mentor", UserRole.MENTOR);
        return new Mentor(user, "career");
    }
}
