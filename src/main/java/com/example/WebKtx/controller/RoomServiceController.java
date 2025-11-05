package com.example.WebKtx.controller;

import com.example.WebKtx.service.RoomService2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webktx/roomservices")
@RequiredArgsConstructor
public class RoomServiceController {
    private final RoomService2 service2;
    @GetMapping("/diennuoc")
    public ResponseEntity<?> getDienNuoc() {
        return ResponseEntity.ok(service2.getDienNuoc());
    }
}
