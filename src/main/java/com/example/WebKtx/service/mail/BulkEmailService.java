package com.example.WebKtx.service.mail;

import com.example.WebKtx.common.Enum.ActiveEnum;
import com.example.WebKtx.dto.Email.EmailMessage;
import com.example.WebKtx.entity.User;
import com.example.WebKtx.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BulkEmailService {

    private final UserRepository userRepository;
    private final EmailProducer emailProducer;

    public int sendEmailToAllActiveUsers(String subject, String body) {
        List<User> users = userRepository.findByActive(ActiveEnum.OPEN);

        for (User user : users) {
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                continue;
            }

            EmailMessage msg = new EmailMessage(
                    user.getEmail(),
                    subject,
                    body
            );
            emailProducer.sendEmailToQueue(msg);
        }

        return users.size();
    }

}

