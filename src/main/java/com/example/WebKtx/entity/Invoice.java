package com.example.WebKtx.entity;

import com.example.WebKtx.common.Enum.InvoiceStatus;
import jakarta.persistence.*;
        import lombok.*;
        import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity @Table(
        name="invoices",
        uniqueConstraints = @UniqueConstraint(
                name="uq_invoice_room_month",
                columnNames = {"room_id","month"} // ✅ đổi sang room+month
        )
)
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="invoice_id", length = 36)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_id", nullable = false)
    Room room;

    @Column(name="month", nullable = false)
    LocalDate month; // quy ước ngày 1 của tháng

    @Column(name="created_at", nullable = false)
    LocalDate createdAt;

    @Column(name="room_fee", nullable = false, precision = 12, scale = 2)
    BigDecimal roomFee;

    @Column(name="total_service_fee", nullable = false, precision = 12, scale = 2)
    BigDecimal totalServiceFee;

    @Column(name="total_amount", nullable = false, precision = 12, scale = 2)
    BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false, length = 16)
    InvoiceStatus status;

    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Set<ServiceDetail> serviceDetails;

    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Set<Payment> payments;

    @PrePersist
    void prePersist() {
        if (status == null) status = InvoiceStatus.UNPAID;
        if (totalServiceFee == null) totalServiceFee = BigDecimal.ZERO;
        if (roomFee == null) roomFee = BigDecimal.ZERO;
        if (totalAmount == null) totalAmount = roomFee.add(totalServiceFee);
        if (createdAt == null) createdAt = LocalDate.now();
    }
}

