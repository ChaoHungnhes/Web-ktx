package com.example.WebKtx.dto.StudentDto;

import com.example.WebKtx.common.Enum.GenderEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentCreateRequest {
    @NotBlank String id;
    @NotBlank String dormId;
    @NotBlank String firstName;
    @NotBlank String lastName;
    @NotBlank String identityNumber;
    @NotNull GenderEnum gender;
    @NotNull  LocalDate birthDate;
    @NotBlank String phone;
    @NotBlank String hometown;
    @NotNull LocalDate contractDate;
    String roomId;          // optional
    String avatar;
    @NotBlank String guardianName;
    @NotBlank String guardianPhone;
    @NotBlank String relationship;
    @NotBlank String occupation;
    @NotNull  Integer priority;
    String userId;          // optional (OneToOne)
}
