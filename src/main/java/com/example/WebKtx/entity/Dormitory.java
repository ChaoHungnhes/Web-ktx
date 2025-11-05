package com.example.WebKtx.entity;

import com.example.WebKtx.common.Enum.PlanType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity @Table(name = "dormitories",
        uniqueConstraints = @UniqueConstraint(name="uq_dorm_name", columnNames = "dorm_name"))
public class Dormitory {

    @Id
    @Column(name = "dorm_id", length = 32) // có thể tự set "DORMA" hoặc dùng UUID
    String id;

    @Column(name = "dorm_name", nullable = false, length = 50)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name="plan_type", nullable = false, length = 16)
    PlanType type;

    @OneToMany(mappedBy = "dormitory", fetch = FetchType.LAZY)
    Set<Room> rooms;
}
