package com.example.WebKtx.payment.VNpay.VNpayDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VnpayCreatePaymentRequest {
    private String invoiceId;      // id hóa đơn cần thanh toán
    private String payerStudentId; // sinh viên trả (có thể null nếu không cần)
}
