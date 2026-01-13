package com.example.lionproject2backend.payment.repository;

import com.example.lionproject2backend.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
