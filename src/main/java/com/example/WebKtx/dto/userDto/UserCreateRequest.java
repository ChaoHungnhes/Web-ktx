package com.example.WebKtx.dto.userDto;

import com.example.WebKtx.common.Enum.ActiveEnum;
import com.example.WebKtx.common.Enum.GenderEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {
    @NotBlank
    String name;

    @NotBlank
    @Email
    String email;

    @NotBlank
    String password;

    @Enumerated(EnumType.STRING)
    ActiveEnum active;

    Set<String> roles; // chỉ gửi tên role hoặc id role
}
