package com.example.WebKtx.controller;

import com.example.WebKtx.anotation.ApiMessage;
import com.example.WebKtx.common.Enum.InvoiceStatus;
import com.example.WebKtx.dto.InvoiceDto.InvoiceCreateRequest;
import com.example.WebKtx.dto.InvoiceDto.InvoiceResponse;
import com.example.WebKtx.dto.InvoiceDto.InvoiceUpdateRequest;
import com.example.WebKtx.dto.ResultPaginationDTO;
import com.example.WebKtx.dto.RoomServiceDetailDto.ServiceDetailItem;
import com.example.WebKtx.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/webktx/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService service;

    @PostMapping("/create")
    @ApiMessage("Create invoice success")
    public ResponseEntity<?> create(@Valid @RequestBody InvoiceCreateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update invoice success")
    public ResponseEntity<?> update(@PathVariable String id,
                                    @Valid @RequestBody InvoiceUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete invoice success")
    public ResponseEntity<?> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok("Delete complete");
    }

    @GetMapping("/{id}")
    @ApiMessage("Get invoice success")
    public ResponseEntity<?> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @ApiMessage("Get invoices (paged) success")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int pageIndex,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(service.getAll(PageRequest.of(pageIndex, pageSize)));
    }

    // detail lines
    @PostMapping("/{id}/details")
    @ApiMessage("Add invoice line success")
    public ResponseEntity<?> addDetail(@PathVariable String id, @RequestBody ServiceDetailItem item) {
        return ResponseEntity.ok(service.addDetail(id, item));
    }

    @DeleteMapping("/{id}/details/{detailId}")
    @ApiMessage("Remove invoice line success")
    public ResponseEntity<?> removeDetail(@PathVariable String id, @PathVariable String detailId) {
        return ResponseEntity.ok(service.removeDetail(id, detailId));
    }

    // recompute totals
    @PutMapping("/{id}/recompute")
    @ApiMessage("Recompute invoice total success")
    public ResponseEntity<?> recompute(@PathVariable String id) {
        return ResponseEntity.ok(service.recompute(id));
    }

    // mark paid if enough payments
    @PutMapping("/{id}/mark-paid-if-enough")
    @ApiMessage("Mark invoice paid if enough success")
    public ResponseEntity<?> markPaidIfEnough(@PathVariable String id) {
        return ResponseEntity.ok(service.markPaidIfEnough(id));
    }

    // ===== Search theo tháng =====
    // Chấp nhận: month=yyyy-MM hoặc month=yyyy-MM-dd (BE normalize về ngày 01)
    @GetMapping("/search-by-month")
    @ApiMessage("Search invoices by month success")
    public ResponseEntity<?> searchByMonth(
            @RequestParam String month,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        LocalDate m = parseMonthFirstDay(month);
        ResultPaginationDTO dto = service.searchByMonth(m, PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    // ===== Search theo phòng =====
    @GetMapping("/by-room")
    @ApiMessage("Search invoices by room success")
    public ResponseEntity<?> searchByRoom(
            @RequestParam String roomId,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.searchByRoom(roomId, PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    // ===== Search theo trạng thái =====
    @GetMapping("/by-status")
    @ApiMessage("Search invoices by status success")
    public ResponseEntity<?> searchByStatus(
            @RequestParam InvoiceStatus status,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.searchByStatus(status, PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/by-room/latest-unpaid")
    @ApiMessage("Get latest unpaid invoice by room success")
    public ResponseEntity<?> getLatestUnpaidByRoom(@RequestParam String roomId) {
        InvoiceResponse res = service.findLatestUnpaidByRoom(roomId);
        return ResponseEntity.ok(res);
    }
    private LocalDate parseMonthFirstDay(String input) {
        // "yyyy-MM" -> yyyy-MM-01
        if (input != null && input.matches("\\d{4}-\\d{2}")) {
            return LocalDate.parse(input + "-01");
        }
        // "yyyy-MM-dd" -> giữ nguyên (bạn đang lưu day=01)
        return LocalDate.parse(input);
    }
}

