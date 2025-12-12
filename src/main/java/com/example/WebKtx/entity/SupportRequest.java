package com.example.WebKtx.entity;

import com.example.WebKtx.common.Enum.SupportStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity @Table(name="support_requests")
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="request_id", length = 36)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="student_id", nullable = false)
    Student student;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false, length = 16)
    SupportStatus status;

    @Column(name="created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name="resolved_at")
    LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="handled_by")
    User handledBy;

    @Lob
    @Column(name="content", nullable = false)
    String content;

    @Column(name="room_name")
    String roomName;
    @Column(name="request_content", nullable = false)
    String requestContent;
    @Column(name="note_extend", nullable = false)
    String noteExtend;


    @PrePersist
    void prePersist() {
        if (status == null) status = SupportStatus.PROCESSING;
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
