package com.example.WebKtx.entity;

import com.example.WebKtx.common.Enum.PaymentMethod;
import com.example.WebKtx.common.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity @Table(name="payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="payment_id", length = 36)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="invoice_id", nullable = false)
    Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="student_id")
    Student payer;

    @Column(name="payment_date", nullable = false)
    LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name="payment_method", nullable = false, length = 16)
    PaymentMethod method;

    @Column(name="amount", nullable = false, precision = 12, scale = 2)
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false, length = 16)
    PaymentStatus status;
}
