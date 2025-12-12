package com.example.WebKtx.dto.reportDto;
import java.math.BigDecimal;

public record SummaryDto(
        String month,         // "2025-11"
        long totalRooms,
        long roomsWithInvoice,
        long unpaidRooms,
        BigDecimal totalInvoiceAmount,
        BigDecimal totalPaidAmount,
        BigDecimal totalRemainAmount,
        long totalStudents
) {}
