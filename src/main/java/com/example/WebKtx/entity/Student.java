package com.example.WebKtx.entity;

import com.example.WebKtx.common.Enum.GenderEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity @Table(name="students",
        uniqueConstraints = @UniqueConstraint(name="uq_student_identity", columnNames = "identity_number"))
public class Student {

    @Id
    @Column(name="student_id", length = 32) // có thể giữ mã "ST01" hoặc dùng UUID
    String id;

    // 🔁 Đổi từ String dormCode → FK thật
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="dorm_id", nullable = false,
            foreignKey = @ForeignKey(name="fk_students_dorm"))
    Dormitory dormitory;

    @Column(name="first_name", nullable = false, length = 50)
    String firstName;

    @Column(name="last_name", nullable = false, length = 20)
    String lastName;

    @Column(name="identity_number", nullable = false, length = 20)
    String identityNumber;

    @Enumerated(EnumType.STRING)
    @Column(name="gender", nullable = false, length = 8)
    GenderEnum gender;

    @Column(name="birth_date", nullable = false)
    LocalDate birthDate;

    @Column(name="phone", nullable = false, length = 20)
    String phone;

    @Column(name="hometown", nullable = false, length = 100)
    String hometown;

    @Column(name="contract_date", nullable = false)
    LocalDate contractDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_id")
    Room room; // có thể null nếu chưa xếp phòng

    @Column(name="avatar")
    String avatar;

    @Column(name="guardian_name", nullable = false, length = 50)
    String guardianName;

    @Column(name="guardian_phone", nullable = false, length = 20)
    String guardianPhone;

    @Column(name="relationship", nullable = false, length = 20)
    String relationship;

    @Column(name="occupation", nullable = false, length = 100)
    String occupation;

    @Column(name="priority", nullable = false)
    Integer priority;

    @Column(name = "academic_year", length = 16)
    String academicYear;

    @Column(name = "class_name", length = 64)
    String className;

    @OneToOne
    @JoinColumn(name="user_id", unique = true)
    User user;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    Set<RoomRegistration> registrations;

//    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
//    Set<Invoice> invoices;
}
