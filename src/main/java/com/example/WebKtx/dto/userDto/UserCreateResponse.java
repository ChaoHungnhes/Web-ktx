package com.example.WebKtx.dto.userDto;

import com.example.WebKtx.common.Enum.ActiveEnum;
import com.example.WebKtx.common.Enum.GenderEnum;
import com.example.WebKtx.entity.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateResponse {
    String id;
    String name;
    String email;
    @Enumerated(EnumType.STRING)
    ActiveEnum active;

    Set<Role> roles; // có thể trả tên role hoặc code role
}
