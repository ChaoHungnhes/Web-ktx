package com.example.WebKtx.dto.RoomDto;

import com.example.WebKtx.common.Enum.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor // thêm dòng này
@NoArgsConstructor  // optional nếu bạn cần default
public class RoomResponse {
    String id;
    String name;
    RoomType type;
    Integer currentOccupants;
    Integer maxOccupants;
    BigDecimal price;
    Integer floor;
    String dormId;
    String dormName;
}
