package com.example.WebKtx.entity;

import com.example.WebKtx.common.Enum.RoomType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity @Table(name="rooms")
public class Room {

    @Id
    @Column(name="room_id", length = 32) // có thể tự set "RMA1" hoặc dùng UUID
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="dorm_id", nullable = false)
    Dormitory dormitory;

    @Column(name="room_name", nullable = false, length = 50)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name="room_type", nullable = false, length = 16)
    RoomType type;

    @Column(name="current_occupants", nullable = false)
    Integer currentOccupants;

    @Column(name="max_occupants", nullable = false)
    Integer maxOccupants;

    @Column(name="room_price", nullable = false, precision = 12, scale = 2)
    BigDecimal price;

    @Column(name="floor", nullable = false)
    int floor;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    Set<Student> students;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    Set<Invoice> invoices;
}
