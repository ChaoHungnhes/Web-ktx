package com.example.WebKtx.dto.RoomRegistrationDto;

import com.example.WebKtx.common.Enum.GenderEnum;
import com.example.WebKtx.common.Enum.RegistrationStatus;
import com.example.WebKtx.common.Enum.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRegistrationDetailDto {
    private String id;

    private String studentId;
    private String studentName;     // firstName + " " + lastName
    private GenderEnum gender;      // NEW
    private String academicYear;    // NEW
    private String className;       // NEW

    private String roomId;
    private String roomName;        // NEW

    private String dormId;
    private String dormName;

    private LocalDate registrationDate;
    private RegistrationStatus status;
    private RequestType requestType;
}
