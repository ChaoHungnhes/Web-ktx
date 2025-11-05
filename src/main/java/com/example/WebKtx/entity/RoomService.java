package com.example.WebKtx.entity;

import com.example.WebKtx.dto.DormitoryDto.DormitoryCreateRequest;
import com.example.WebKtx.dto.DormitoryDto.DormitoryResponse;
import com.example.WebKtx.dto.DormitoryDto.DormitoryUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity @Table(name="services",
        uniqueConstraints = @UniqueConstraint(name="uq_service_name", columnNames = "service_name"))
public class RoomService {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="service_id", length = 36)
    String id;

    @Column(name="service_name", nullable = false, length = 50)
    String name;

    @Column(name="service_price", nullable = false, precision = 12, scale = 2)
    BigDecimal price;

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    Set<ServiceDetail> details;
}
