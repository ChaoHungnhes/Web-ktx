package com.example.WebKtx.controller;

import com.example.WebKtx.dto.Email.BulkEmailRequest;
import com.example.WebKtx.dto.Email.EmailMessage;
import com.example.WebKtx.service.mail.BulkEmailService;
import com.example.WebKtx.service.mail.EmailProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webktx/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailProducer emailProducer;
    private final BulkEmailService bulkEmailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody EmailMessage request) {
        emailProducer.sendEmailToQueue(request);
        return ResponseEntity.ok("Đã đẩy request gửi mail vào RabbitMQ");
    }
    // gửi cho tất cả user
    @PostMapping("/send-all")
    public ResponseEntity<?> sendEmailToAll(@RequestBody BulkEmailRequest request) {
        int count;
            count = bulkEmailService.sendEmailToAllActiveUsers(
                    request.getSubject(),
                    request.getBody());

        return ResponseEntity.ok("Đã đẩy yêu cầu gửi mail cho " + count + " user vào queue");
    }
}
