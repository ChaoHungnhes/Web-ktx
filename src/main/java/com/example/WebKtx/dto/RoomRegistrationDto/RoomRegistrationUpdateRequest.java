package com.example.WebKtx.dto.RoomRegistrationDto;

import com.example.WebKtx.common.Enum.RegistrationStatus;
import com.example.WebKtx.common.Enum.RequestType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRegistrationUpdateRequest {
    // Cho phép cập nhật trạng thái và/hoặc loại yêu cầu
    @NotNull
    private RegistrationStatus status;

    // optional – nếu muốn chuyển loại (REGISTER/CHANGEROOM)
    private RequestType requestType;

    // optional – nếu muốn chuyển phòng ở bước xử lý
    private String roomId;
}