package com.example.WebKtx.controller;

import com.example.WebKtx.anotation.ApiMessage;
import com.example.WebKtx.dto.StudentDto.StudentCreateRequest;
import com.example.WebKtx.dto.StudentDto.StudentUpdateRequest;
import com.example.WebKtx.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webktx/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody StudentCreateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody StudentUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        service.delete(id); return ResponseEntity.ok("Delete complete");
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) { return ResponseEntity.ok(service.findById(id)); }
    @GetMapping
    public ResponseEntity<?> getAll() { return ResponseEntity.ok(service.getAll()); }

    @GetMapping("/{roomId}/students")
    @ApiMessage("Get students by room success")
    public ResponseEntity<?> getStudentsByRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(service.getStudentsInRoom(roomId));
    }
    @PutMapping("/{id}/remove-room")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')") // Bảo mật
    public ResponseEntity<?> removeStudentFromRoom(@PathVariable String id) {
        service.removeStudentFromRoom(id);
        return ResponseEntity.ok("Removed student from room successfully");
    }
    @GetMapping("/with-room")
    public ResponseEntity<?> getStudentsWithRoom() {
        return ResponseEntity.ok(service.getStudentsWithRoom());
    }
}

