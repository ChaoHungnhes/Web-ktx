package com.example.WebKtx.dto.Announcement;

import com.example.WebKtx.common.Enum.Channel;
import com.example.WebKtx.common.Enum.Target;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AnnouncementUpdateRequest {
    String title;
    String summary;
    String content;
    Target target;
    Channel channel;
    String imageUrl;        // optional: cho phép cập nhật URL ảnh nếu muốn
}
