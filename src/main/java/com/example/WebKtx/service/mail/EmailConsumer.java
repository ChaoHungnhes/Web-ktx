package com.example.WebKtx.service.mail;

import com.example.WebKtx.config.RabbitMQConfig;
import com.example.WebKtx.dto.Email.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailConsumer {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmailMessage(EmailMessage emailMessage) {
        log.info("Nhận message từ RabbitMQ để gửi mail: {}", emailMessage);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailMessage.getTo());
            message.setSubject(emailMessage.getSubject());
            message.setText(emailMessage.getBody());

            mailSender.send(message);
            log.info("Gửi email tới {} thành công", emailMessage.getTo());
        } catch (Exception e) {
            log.error("Gửi email thất bại", e);
            // ở đây có thể gửi vào DLQ hoặc retry
        }
    }
}
