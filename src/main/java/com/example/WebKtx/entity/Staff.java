package com.example.WebKtx.entity;

import com.example.WebKtx.common.Enum.StaffPosition;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity @Table(name="staff",
        uniqueConstraints = @UniqueConstraint(name="uq_staff_email", columnNames = "email"))
public class Staff {

    @Id
    @Column(name="staff_id", length = 32) // có thể tự set "STF01" hoặc dùng UUID
    String id;

    @Column(name="full_name", nullable = false, length = 50)
    String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name="position", nullable = false, length = 16)
    StaffPosition position;

    @Column(name="phone", nullable = false, length = 20)
    String phone;

    @Column(name="email", nullable = false, length = 100)
    String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    User user;
}
