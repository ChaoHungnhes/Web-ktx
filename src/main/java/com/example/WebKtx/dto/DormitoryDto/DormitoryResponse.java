package com.example.WebKtx.dto.DormitoryDto;

import com.example.WebKtx.common.Enum.PlanType;
import lombok.Data;

@Data
public class DormitoryResponse {
    String id; String name; PlanType type;
}
