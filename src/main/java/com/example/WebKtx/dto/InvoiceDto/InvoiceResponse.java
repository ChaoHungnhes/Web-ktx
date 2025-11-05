package com.example.WebKtx.dto.InvoiceDto;
import com.example.WebKtx.common.Enum.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class InvoiceResponse {
    String id;
    String roomId; String roomName;
    LocalDate month; LocalDate createdAt;

    BigDecimal roomFee;
    BigDecimal electricityFee; // cộng các dòng "Điện"
    BigDecimal waterFee;       // cộng các dòng "Nước"
    BigDecimal extraFee;       // cộng các dòng khác
    BigDecimal totalServiceFee;
    BigDecimal totalAmount;

    InvoiceStatus status;

    List<InvoiceResponseDetail> details;

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class InvoiceResponseDetail {
        String detailId;
        String serviceId;
        String serviceName;
        BigDecimal quantity;
        String unit;
        BigDecimal unitPrice;
        BigDecimal totalAmount;
        String description;
    }
}

