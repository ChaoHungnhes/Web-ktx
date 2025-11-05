package com.example.WebKtx.dto.DormitoryDto;

import com.example.WebKtx.common.Enum.PlanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DormitoryCreateRequest {
    @NotBlank String id;
    @NotBlank String name;
    @NotNull PlanType type;
}
