package com.example.WebKtx.dto.InvoiceDto;
import com.example.WebKtx.common.Enum.InvoiceStatus;
import com.example.WebKtx.dto.RoomServiceDetailDto.ServiceDetailItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class InvoiceUpdateRequest {
    BigDecimal roomFee;
    List<ServiceDetailItem> details;   // thay thế toàn bộ chi tiết (nếu truyền)
    InvoiceStatus status;              // UNPAID/PAID/DISCOUNTED
}
