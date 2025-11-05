package com.example.WebKtx.dto.StudentDto;

import com.example.WebKtx.common.Enum.GenderEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class StudentInRoomResponse {
    String id;
    String fullName;        // firstName + " " + lastName
    GenderEnum gender;
    String academicYear;
    String className;
    String phone;
}
