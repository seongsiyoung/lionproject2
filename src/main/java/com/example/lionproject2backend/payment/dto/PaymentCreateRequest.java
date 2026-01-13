package com.example.lionproject2backend.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCreateRequest {

    @NotNull(message = "횟수를 입력해주세요")
    @Min(value = 1, message = "최소 1회 이상 선택해주세요")
    private Integer count;
}
