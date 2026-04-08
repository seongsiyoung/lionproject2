package com.example.lionproject2backend.settlement.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SettlementTargetTest {

    @Test
    void READY_target은_PROCESSING으로_claim할_수_있다() {
        SettlementTarget target = SettlementTarget.create(1L, "2026-07", 10L);

        target.markAsProcessing();

        assertThat(target.getStatus()).isEqualTo(SettlementTarget.SettlementTargetStatus.PROCESSING);
    }

    @Test
    void PROCESSING_target은_SKIPPED로_변경할_수_있다() {
        SettlementTarget target = SettlementTarget.create(1L, "2026-07", 10L);
        target.markAsProcessing();

        target.markAsSkipped();

        assertThat(target.getStatus()).isEqualTo(SettlementTarget.SettlementTargetStatus.SKIPPED);
    }

    @Test
    void FAILED_target은_READY로_복구할_수_있다() {
        SettlementTarget target = SettlementTarget.create(1L, "2026-07", 10L);
        target.markAsFailed();

        target.resetToReady();

        assertThat(target.getStatus()).isEqualTo(SettlementTarget.SettlementTargetStatus.READY);
    }

    @Test
    void READY만_처리_가능한_target이다() {
        SettlementTarget ready = SettlementTarget.create(1L, "2026-07", 10L);
        SettlementTarget done = SettlementTarget.create(2L, "2026-07", 10L);
        done.markAsDone();

        assertThat(ready.isProcessable()).isTrue();
        assertThat(done.isProcessable()).isFalse();
    }
}
