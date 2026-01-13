package com.example.lionproject2backend.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentVerifyRequest {

    @NotBlank(message = "결제 ID를 입력해주세요")
    private String impUid;
}
