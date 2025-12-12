package com.example.WebKtx.controller;

import com.example.WebKtx.anotation.ApiMessage;
import com.example.WebKtx.common.Enum.SupportStatus;
import com.example.WebKtx.dto.ResultPaginationDTO;
import com.example.WebKtx.dto.SupportRequestDto.SupportRequestCreateRequest;
import com.example.WebKtx.dto.SupportRequestDto.SupportRequestUpdateRequest;
import com.example.WebKtx.service.SupportRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/webktx/support-requests")
@RequiredArgsConstructor
public class SupportRequestController {

    private final SupportRequestService service;

    @PostMapping
    @ApiMessage("Create support request success")
    public ResponseEntity<?> create(@Valid @RequestBody SupportRequestCreateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update support request success")
    public ResponseEntity<?> update(
            @PathVariable String id,
            @Valid @RequestBody SupportRequestUpdateRequest req
    ) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete support request success")
    public ResponseEntity<?> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok("Delete support request success");
    }

    @GetMapping("/{id}")
    @ApiMessage("Get support request success")
    public ResponseEntity<?> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @ApiMessage("Get support requests (paged) success")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.getAll(PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    // ====== Search by created date ======
    @GetMapping("/by-created-date")
    @ApiMessage("Search support requests by created date success")
    public ResponseEntity<?> searchByCreatedDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.searchByCreatedDate(date, PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    // ====== Search by resolved date ======
    @GetMapping("/by-resolved-date")
    @ApiMessage("Search support requests by resolved date success")
    public ResponseEntity<?> searchByResolvedDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.searchByResolvedDate(date, PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    // ====== Search by status ======
    @GetMapping("/by-status")
    @ApiMessage("Search support requests by status success")
    public ResponseEntity<?> searchByStatus(
            @RequestParam SupportStatus status,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.searchByStatus(status, PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    // ====== Search by roomName ======
    @GetMapping("/by-room")
    @ApiMessage("Search support requests by room name success")
    public ResponseEntity<?> searchByRoom(
            @RequestParam String roomName,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.searchByRoomName(roomName, PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    // ====== Search by studentId ======
    @GetMapping("/by-student")
    @ApiMessage("Search support requests by student success")
    public ResponseEntity<?> searchByStudent(
            @RequestParam String studentId,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.searchByStudent(studentId, PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }


}

