package com.example.WebKtx.service;

import com.example.WebKtx.dto.reportDto.DebtRoomDto;
import com.example.WebKtx.dto.reportDto.RoomOccupancyDto;
import com.example.WebKtx.dto.reportDto.SummaryDto;

import java.util.List;

public interface ReportService {
    List<DebtRoomDto> getDebtRooms(String month); // month "yyyy-MM"
    List<RoomOccupancyDto> getOccupancy(String dormId); // dormId optional null -> all
    SummaryDto getSummary(String month);
}
