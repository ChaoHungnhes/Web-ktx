package com.example.WebKtx.controller;

import com.example.WebKtx.anotation.ApiMessage;
import com.example.WebKtx.dto.DormitoryDto.DormitoryCreateRequest;
import com.example.WebKtx.dto.DormitoryDto.DormitoryUpdateRequest;
import com.example.WebKtx.service.DormitoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webktx/dormitories")
@RequiredArgsConstructor
public class DormitoryController {
    private final DormitoryService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody DormitoryCreateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody DormitoryUpdateRequest req) {
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
    @GetMapping("/names")
    @ApiMessage("Get dorm names success")
    public ResponseEntity<?> getAllNames() {
        return ResponseEntity.ok(service.getAllNames());
    }
}

