package com.example.WebKtx.config;

import com.example.WebKtx.common.Enum.RoleEnum;
import com.example.WebKtx.common.ErrorCode;
import com.example.WebKtx.entity.Role;
import com.example.WebKtx.entity.User;
import com.example.WebKtx.exception.AppException;
import com.example.WebKtx.repository.RoleRepository;
import com.example.WebKtx.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(
        level = AccessLevel.PRIVATE,
        makeFinal =
                true)
@Configuration
@Slf4j
public class ApplicationInitConfig {
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                Role roles = roleRepository.findById(RoleEnum.ADMIN.name())
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
                User user = User.builder()
                        .email("admin@gmail.com")
                        .name("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(new HashSet<>(Set.of(roles)))
                        .build();
                userRepository.save(user);
                log.warn("admin user has been created with default password : admin, please change it");
            }
        };
    }
}
