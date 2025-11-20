package com.example.WebKtx.dto.RoomServiceDetailDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ServicePriceDto {
    private String name;
    private BigDecimal price;
}
