package com.example.WebKtx.controller;

import com.example.WebKtx.anotation.ApiMessage;
import com.example.WebKtx.dto.reportDto.DebtRoomDto;
import com.example.WebKtx.dto.reportDto.RoomOccupancyDto;
import com.example.WebKtx.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/webktx/manager/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/debt-rooms")
    @ApiMessage("Get debt rooms by month success")
    public ResponseEntity<?> getDebtRooms(@RequestParam String month) {
        // month format: "2025-11"
        List<DebtRoomDto> data = reportService.getDebtRooms(month);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/occupancy")
    @ApiMessage("Get room occupancy success")
    public ResponseEntity<?> getOccupancy(@RequestParam(required = false) String dormId) {
        List<RoomOccupancyDto> data = reportService.getOccupancy(dormId);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/summary")
    @ApiMessage("Get reports summary success")
    public ResponseEntity<?> getSummary(@RequestParam String month) {
        var dto = reportService.getSummary(month);
        return ResponseEntity.ok(dto);
    }
}
