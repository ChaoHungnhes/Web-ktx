package com.example.WebKtx.entity;

import com.example.WebKtx.common.Enum.RegistrationStatus;
import com.example.WebKtx.common.Enum.RequestType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity @Table(name="room_registrations")
public class RoomRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="registration_id", length = 36)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="student_id", nullable = false)
    Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_id", nullable = false)
    Room room;

    @Column(name="registration_date", nullable = false)
    LocalDate registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false, length = 16)
    RegistrationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name="request_type", nullable = false, length = 16)
    RequestType requestType;

    @PrePersist
    void prePersist() {
        if (status == null) status = RegistrationStatus.PENDING;
        if (requestType == null) requestType = RequestType.REGISTER;
        if (registrationDate == null) registrationDate = LocalDate.now();
    }
}
