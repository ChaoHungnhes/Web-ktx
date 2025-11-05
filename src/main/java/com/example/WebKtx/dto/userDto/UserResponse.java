package com.example.WebKtx.dto.userDto;

import com.example.WebKtx.common.Enum.ActiveEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    String id;
    String name;
    String email;
    String password;
    ActiveEnum active;
    String studentId;
    Set<String> roles;
}
