package com.example.lionproject2backend.settlement.domain;

import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.user.domain.User;
import com.example.lionproject2backend.user.domain.UserRole;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SettlementTest {

    @Test
    void PENDING_정산은_금액을_재계산할_수_있다() {
        Settlement settlement = createSettlement();

        settlement.recalculate(
                20000,
                2000,
                18000,
                3000,
                1000,
                17000,
                0
        );

        assertThat(settlement.getTotalPaymentAmount()).isEqualTo(20000);
        assertThat(settlement.getPlatformFee()).isEqualTo(2000);
        assertThat(settlement.getSettlementAmount()).isEqualTo(18000);
        assertThat(settlement.getRefundAmount()).isEqualTo(3000);
        assertThat(settlement.getPreviousCarryOverAmount()).isEqualTo(1000);
        assertThat(settlement.getPayableAmount()).isEqualTo(17000);
        assertThat(settlement.getCarryOverAmount()).isZero();
        assertThat(settlement.getStatus()).isEqualTo(SettlementStatus.PENDING);
    }

    @Test
    void COMPLETED_정산은_금액을_재계산할_수_없다() {
        Settlement settlement = createSettlement();
        settlement.complete();

        assertThatThrownBy(() -> settlement.recalculate(
                20000,
                2000,
                18000,
                3000,
                1000,
                17000,
                0
        )).isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("지급 완료된 정산");
    }

    private Settlement createSettlement() {
        User user = User.create("mentor@example.com", "password", "mentor", UserRole.MENTOR);
        Mentor mentor = new Mentor(user, "career");
        return Settlement.create(
                mentor,
                YearMonth.of(2026, 7),
                10000,
                1000,
                9000,
                0,
                0,
                9000,
                0
        );
    }
}
