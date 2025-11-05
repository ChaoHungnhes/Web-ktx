package com.example.WebKtx.dto.RoomRegistrationDto;

import com.example.WebKtx.common.Enum.RequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRegistrationCreateRequest {
    @NotBlank
    private String studentId;

    @NotBlank
    private String roomId;

    // optional – nếu null sẽ default ở @PrePersist = REGISTER
    private RequestType requestType;

    // optional – nếu null sẽ default ở @PrePersist = now()
    private LocalDate registrationDate;
}
