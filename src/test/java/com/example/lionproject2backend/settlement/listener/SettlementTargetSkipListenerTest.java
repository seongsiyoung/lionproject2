package com.example.lionproject2backend.settlement.listener;

import com.example.lionproject2backend.settlement.domain.SettlementTarget;
import com.example.lionproject2backend.settlement.exception.SettlementTargetSkippableException;
import com.example.lionproject2backend.settlement.repository.SettlementTargetRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SettlementTargetSkipListenerTest {

    @Test
    void processor에서_발생한_target_local_skip_예외는_target을_FAILED로_기록한다() {
        SettlementTargetRepository settlementTargetRepository = mock(SettlementTargetRepository.class);
        SettlementTargetSkipListener listener = new SettlementTargetSkipListener(settlementTargetRepository);
        SettlementTarget target = SettlementTarget.create(1L, "2026-07", 10L);
        SettlementTargetSkippableException exception =
                new SettlementTargetSkippableException(target, "멘토 정산 검증 실패");

        listener.onSkipInProcess(target, exception);

        assertThat(target.getStatus()).isEqualTo(SettlementTarget.SettlementTargetStatus.FAILED);
        verify(settlementTargetRepository).save(target);
    }
}
