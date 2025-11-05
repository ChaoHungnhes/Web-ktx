package com.example.WebKtx.entity;

import com.example.WebKtx.common.Enum.ActiveEnum;
import com.example.WebKtx.common.Enum.GenderEnum;
import com.example.WebKtx.common.Enum.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UUID;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @NotBlank @Email
    String email;
    @NotBlank
    String password;
    @NotBlank
    String name;
    @Enumerated(EnumType.STRING)
    ActiveEnum active;
    @ManyToMany
    Set<Role> roles;
}
