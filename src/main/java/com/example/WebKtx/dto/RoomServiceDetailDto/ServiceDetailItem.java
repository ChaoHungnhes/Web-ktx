package com.example.WebKtx.dto.RoomServiceDetailDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDetailItem {
    String serviceId;          // có thể null nếu truyền theo tên
    String serviceName;        // "Dien", "Nuoc", "Phat sinh"
    BigDecimal quantity;       // kWh / m3 / 1
    BigDecimal unitPrice;      // nếu null => copy từ RoomService.price
    String unit;               // "kWh", "m3", "lần"
    String description;        // mô tả phát sinh
}
