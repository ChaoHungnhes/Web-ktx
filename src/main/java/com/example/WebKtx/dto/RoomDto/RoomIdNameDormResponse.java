package com.example.WebKtx.dto.RoomDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomIdNameDormResponse {
    private String roomId;
    private String roomName;
    private String dormId;
    private String dormName;
}
