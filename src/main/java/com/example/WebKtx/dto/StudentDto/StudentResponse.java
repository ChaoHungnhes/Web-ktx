package com.example.WebKtx.dto.StudentDto;

import com.example.WebKtx.common.Enum.GenderEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class StudentResponse {
    String id;
    String firstName; String lastName;
    String identityNumber; GenderEnum gender;
    LocalDate birthDate; String phone; String hometown;
    LocalDate contractDate; Integer priority;
    String avatar; String relationship; String occupation;
    // refs
    String dormId; String dormName;
    String roomId; String roomName;
    String userId; String userEmail;
    String academicYear;String className;
}
