package com.example.WebKtx.controller;

import com.example.WebKtx.anotation.ApiMessage;
import com.example.WebKtx.dto.ResultPaginationDTO;
import com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationCreateRequest;
import com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationUpdateRequest;
import com.example.WebKtx.service.RoomRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webktx/room-registrations")
@RequiredArgsConstructor
public class RoomRegistrationController {

    private final RoomRegistrationService service;

    @PostMapping("/create")
    @ApiMessage("Create room registration success")
    public ResponseEntity<?> create(@Valid @RequestBody RoomRegistrationCreateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update room registration success")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody RoomRegistrationUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete room registration success")
    public ResponseEntity<?> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(new Object(){ public final String message = "Delete complete"; });
    }

    @GetMapping("/{id}")
    @ApiMessage("Get room registration by id success")
    public ResponseEntity<?> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @ApiMessage("Get room registrations (paged) success")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.getAllPaged(PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }
    // ✅ LIST theo studentId
    @GetMapping("/student/{studentId}")
    @ApiMessage("Get room registrations by student success")
    public ResponseEntity<?> findByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(service.findByStudent(studentId));
    }

    @GetMapping("/approved")
    @ApiMessage("Get room registrations (paged) success")
    public ResponseEntity<?> getAllByApproved(
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.getAllByApproved(PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/pending")
    @ApiMessage("Get room registrations (paged) success")
    public ResponseEntity<?> getAllByPending(
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.getAllByPending(PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/rejected")
    @ApiMessage("Get room registrations (paged) success")
    public ResponseEntity<?> getAllByRejected(
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.getAllByRejected(PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/approve/{id}")
    @ApiMessage("Approve room registration success")
    public ResponseEntity<?> approve(@PathVariable String id) {
        return ResponseEntity.ok(service.approve(id));
    }

    @PutMapping("/reject/{id}")
    @ApiMessage("Reject room registration success")
    public ResponseEntity<?> reject(@PathVariable String id) {
        return ResponseEntity.ok(service.reject(id));
    }
}

