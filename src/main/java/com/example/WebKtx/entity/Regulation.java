package com.example.WebKtx.entity;

import com.example.WebKtx.common.Enum.RegulationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity @Table(name="regulations")
public class Regulation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="regulation_id", length = 36)
    String id;

    @Column(name="title", nullable = false, length = 100)
    String title;

    @Lob
    @Column(name="content", nullable = false)
    String content;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false, length = 16)
    RegulationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="created_by")
    User createdBy;
}
