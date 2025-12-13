package com.example.WebKtx.controller;

import com.example.WebKtx.dto.userDto.UserCreateRequest;
import com.example.WebKtx.dto.userDto.UserUpdateRequest;
import com.example.WebKtx.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webktx/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;

    // Tạo user mới
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> create(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.create(request));
    }

    // Trong UserController
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> update(@PathVariable String id,
                                         @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    // API Delete đã có sẵn trong controller của bạn
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok("Delete complete");
    }

    // Lấy thông tin user theo id
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/myInfo")
    public ResponseEntity<Object> getMyInfo() {
        return ResponseEntity.ok(userService.getMyInfo());
    }

    // Lấy tất cả user
    @GetMapping
    public ResponseEntity<Object> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }
}
