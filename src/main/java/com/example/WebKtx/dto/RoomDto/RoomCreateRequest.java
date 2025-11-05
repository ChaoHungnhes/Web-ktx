package com.example.WebKtx.dto.RoomDto;

import com.example.WebKtx.common.Enum.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomCreateRequest {
    @NotBlank String id;
    @NotBlank String dormId;
    @NotBlank String name;
    @NotNull RoomType type;
    @NotNull  Integer maxOccupants;
    @NotNull BigDecimal price;
    @NotNull  Integer floor;
}
