package com.example.WebKtx.dto.reportDto;

import com.example.WebKtx.common.Enum.InvoiceStatus;
import java.math.BigDecimal;

public record DebtRoomDto(
        String roomId,          // 1. r.id
        String roomName,        // 2. r.name
        String dormId,          // 3. d.id
        String dormName,        // 4. d.name
        Integer floor,          // 5. r.floor
        Long studentCount,      // 6. count(s2) -> BẮT BUỘC PHẢI LÀ LONG
        BigDecimal totalAmount, // 7. i.totalAmount
        BigDecimal paidAmount,  // 8. sum(paid)
        BigDecimal debtAmount,  // 9. calculated remain
        String invoiceId,       // 10. i.id
        InvoiceStatus status    // 11. i.status
) {}