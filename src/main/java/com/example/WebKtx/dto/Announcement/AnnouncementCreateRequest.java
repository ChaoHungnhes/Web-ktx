package com.example.WebKtx.dto.Announcement;

import com.example.WebKtx.common.Enum.Channel;
import com.example.WebKtx.common.Enum.Target;
import lombok.*;

// Create
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementCreateRequest {
    String title;
    String summary;
    String content;
    Target target;
    Channel channel;
    String createdById;     // optional (nếu muốn set người tạo)
}
