package com.example.WebKtx.dto.InvoiceDto;
import com.example.WebKtx.common.Enum.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class InvoiceSummaryResponse {
    String id;
    String roomId; String roomName;
    LocalDate month; LocalDate createdAt;
    BigDecimal roomFee;
    BigDecimal totalServiceFee;
    BigDecimal totalAmount;
    InvoiceStatus status;
}