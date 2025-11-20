package com.example.WebKtx.repository;

import com.example.WebKtx.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    @Query("""
      select coalesce(sum(p.amount),0) from Payment p
      where p.invoice.id = :invoiceId and p.status = com.example.WebKtx.common.Enum.PaymentStatus.SUCCESS
    """)
    BigDecimal sumSuccessAmount(@Param("invoiceId") String invoiceId);
}