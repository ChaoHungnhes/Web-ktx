package com.example.WebKtx.dto.SupportRequestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupportRequestCreateRequest {

    @NotBlank
    String studentId;

    @NotBlank
    String roomName;

    @NotBlank
    String requestContent;   // tiêu đề / nội dung chính

    @NotBlank
    String noteExtend;       // ghi chú thêm

    @NotBlank
    String content;          // nội dung chi tiết (Lob)
}

