package com.example.WebKtx.service.impl;

import com.example.WebKtx.dto.reportDto.DebtRoomDto;
import com.example.WebKtx.dto.reportDto.RoomOccupancyDto;
import com.example.WebKtx.dto.reportDto.SummaryDto;
import com.example.WebKtx.repository.InvoiceRepository;
import com.example.WebKtx.repository.RoomRepository;
import com.example.WebKtx.repository.StudentRepository;
import com.example.WebKtx.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final RoomRepository roomRepo;
    private final InvoiceRepository invoiceRepo;
    private final StudentRepository studentRepo;

    @Override
    public List<DebtRoomDto> getDebtRooms(String monthStr) {
        YearMonth ym = YearMonth.parse(monthStr); // expects "YYYY-MM"
        LocalDate month = ym.atDay(1);
        return roomRepo.findDebtRoomsByMonth(month);
    }

    @Override
    public List<RoomOccupancyDto> getOccupancy(String dormId) {
        if (dormId == null || dormId.isBlank()) {
            return roomRepo.findAllRoomOccupancy();
        } else {
            return roomRepo.findRoomOccupancyByDorm(dormId);
        }
    }

    @Override
    public SummaryDto getSummary(String monthStr) {
        YearMonth ym = YearMonth.parse(monthStr);
        LocalDate month = ym.atDay(1);

        long totalRooms = roomRepo.count();
        // rooms with invoice that month
        long roomsWithInvoice = invoiceRepo.countByMonth(month);

        // unpaid invoices for that month
        long unpaidRooms = invoiceRepo.countByMonthAndStatus(month, com.example.WebKtx.common.Enum.InvoiceStatus.UNPAID);

        BigDecimal totalInvoiceAmount = invoiceRepo.sumTotalAmountByMonth(month);
        if (totalInvoiceAmount == null) totalInvoiceAmount = BigDecimal.ZERO;

        BigDecimal totalPaid = invoiceRepo.sumPaidAmountByMonth(month);
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;

        BigDecimal totalRemain = totalInvoiceAmount.subtract(totalPaid);

        long totalStudents = studentRepo.countByRoomIsNotNull(); // implement in studentRepo

        return new SummaryDto(
                monthStr,
                totalRooms,
                roomsWithInvoice,
                unpaidRooms,
                totalInvoiceAmount,
                totalPaid,
                totalRemain,
                totalStudents
        );
    }
}

