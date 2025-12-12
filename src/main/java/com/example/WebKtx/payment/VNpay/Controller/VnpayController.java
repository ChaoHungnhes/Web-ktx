package com.example.WebKtx.payment.VNpay.Controller;

import com.example.WebKtx.anotation.ApiMessage;
import com.example.WebKtx.payment.VNpay.VNpayDto.VnpayCreatePaymentRequest;
import com.example.WebKtx.payment.VNpay.VNpayDto.VnpayCreatePaymentResponse;
import com.example.WebKtx.payment.VNpay.Service.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/webktx/vnpay")
@RequiredArgsConstructor
public class VnpayController {

    private final VnpayService vnpayService;

    // tạo URL thanh toán cho 1 invoice
    @ApiMessage("Create VNPAY payment url success")
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(HttpServletRequest request,
                                           @RequestBody VnpayCreatePaymentRequest body) {
        String clientIp = request.getRemoteAddr();
        VnpayCreatePaymentResponse res = vnpayService.createPaymentUrl(body, clientIp);
        return ResponseEntity.ok(res);
    }

    // IPN handler (VNPAY gọi)
    @GetMapping(
            value = "/ipn",
            produces = "text/plain;charset=UTF-8"
    )
    public String ipnVnpay(@RequestParam Map<String, String> params) {
        return vnpayService.handleIpn(params); // trả về "OK" / "INVALID" / ...
    }
}

