package com.example.WebKtx.dto.SupportRequestDto;

import com.example.WebKtx.common.Enum.SupportStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupportRequestUpdateRequest {

    SupportStatus status;      // có thể đổi PROCESSING -> RESOLVED / REJECTED
    String handledById;        // id User xử lý (có thể null khi mới tạo)
    String noteExtend;         // có thể cập nhật
    String content;            // có thể cập nhật
}

