package com.example.WebKtx.dto.SupportRequestDto;

import com.example.WebKtx.common.Enum.SupportStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupportRequestResponse {

    String id;

    String studentId;
    String studentName;
    String studentPhone;

    String roomName;

    SupportStatus status;
    LocalDateTime createdAt;
    LocalDateTime resolvedAt;

    String handledById;
    String handledByName;

    String requestContent;
    String content;
    String noteExtend;
}

