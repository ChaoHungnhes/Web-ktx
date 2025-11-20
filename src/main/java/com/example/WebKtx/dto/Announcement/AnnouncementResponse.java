package com.example.WebKtx.dto.Announcement;
import com.example.WebKtx.common.Enum.Channel;
import com.example.WebKtx.common.Enum.Target;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AnnouncementResponse {
    String id;
    String title;
    String summary;
    String content;
    Target target;
    Channel channel;
    LocalDateTime createdAt;
    String createdById;
    String createdByName;
    String imageUrl;
}
