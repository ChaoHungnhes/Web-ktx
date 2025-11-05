package com.example.WebKtx.dto.RoomDto;

import com.example.WebKtx.common.Enum.RoomType;
import lombok.Data;

@Data
public class RoomSearchRequest {
    String dormName;          // nullable -> bỏ qua
    RoomType type;            // nullable -> bỏ qua
    Integer maxOccupants;     // nullable -> bỏ qua
    Integer floor;            // nullable -> bỏ qua
    Integer page;             // optional
    Integer size;             // optional
    String sort;              // ví dụ: "price,asc" hoặc "name,desc"
}
