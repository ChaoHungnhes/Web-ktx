package com.example.WebKtx.dto.InvoiceDto;
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
public class InvoiceCreateRequest {
    @NotBlank String roomId;
    @NotNull LocalDate month;         // quy ước ngày 1
    @NotBlank String studentId;        // cách A còn cột student_id
    BigDecimal roomFee;                // null => copy room.price
    List<ServiceDetailItem> details;   // điện/nước/phát sinh
}
