package com.example.WebKtx.entity;

import com.example.WebKtx.common.Enum.Channel;
import com.example.WebKtx.common.Enum.Target;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity @Table(name="announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="announcement_id", length = 36)
    String id;

    @Column(name="title", nullable = false, length = 100)
    String title;

    @Column(name="summary", length = 255)       // ✅ tóm tắt ngắn
    String summary;

    @Lob
    @Column(name="content", nullable = false)
    String content;

    @Enumerated(EnumType.STRING)
    @Column(name="target", nullable = false, length = 16)
    Target target;              // ALL | STUDENT | MANAGER

    @Column(name="created_at", nullable = false)
    LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name="channel", nullable = false, length = 16)
    Channel channel;            // WEB | EMAIL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="created_by")
    User createdBy;

    @Column(name="image_url")   // ✅ link ảnh đã upload
    String imageUrl;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
