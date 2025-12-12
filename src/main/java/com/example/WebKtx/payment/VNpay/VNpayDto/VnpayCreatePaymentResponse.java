package com.example.WebKtx.payment.VNpay.VNpayDto;
import lombok.*;

//@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class VnpayCreatePaymentResponse {
    private String paymentUrl;   // FE mở cái này
    private String orderInfo;    // nội dung hóa đơn
    private String invoiceId;
    private String paymentId;    // id Payment record đã tạo ở DB
}
