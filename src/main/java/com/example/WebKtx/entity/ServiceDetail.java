package com.example.WebKtx.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity @Table(name="service_details")
public class ServiceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="detail_id", length = 36)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="invoice_id", nullable = false)
    Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="service_id", nullable = false)
    RoomService service;

    // Số lượng (kWh, m3, lần, …)
    @Column(name="quantity", nullable = false)
    BigDecimal quantity; // dùng BigDecimal để nhân chính xác

    // Đơn giá (copy từ RoomService.price tại thời điểm lập)
    @Column(name="unit_price", nullable = false, precision = 12, scale = 2)
    BigDecimal unitPrice;

    // Đơn vị đo (kWh/m3/lần/…)
    @Column(name="unit", length = 16)
    String unit;

    // Mô tả chi phí phát sinh (nếu có)
    @Column(name="description", length = 255)
    String description;

    // Thành tiền dòng (quantity * unitPrice), có thể tính trước để “đóng băng”
    @Column(name="total_amount", nullable = false, precision = 12, scale = 2)
    BigDecimal totalAmount;

    @PrePersist
    void prePersist() {
        if (quantity == null) quantity = BigDecimal.ZERO;
        if (unitPrice == null) unitPrice = BigDecimal.ZERO;
        if (totalAmount == null) totalAmount = unitPrice.multiply(quantity);
    }
}
