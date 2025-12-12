package com.example.WebKtx.payment.Momo.service;

import com.example.WebKtx.common.Enum.InvoiceStatus;
import com.example.WebKtx.common.Enum.PaymentMethod;
import com.example.WebKtx.common.Enum.PaymentStatus;
import com.example.WebKtx.entity.Invoice;
import com.example.WebKtx.entity.Payment;
import com.example.WebKtx.payment.Momo.client.MomoApi;
import com.example.WebKtx.payment.Momo.model.CreateMomoRequest;
import com.example.WebKtx.payment.Momo.model.CreateMomoResponse;
import com.example.WebKtx.repository.InvoiceRepository;
import com.example.WebKtx.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomoService {

    @Value("${momo.partner-code}") private String PARTNER_CODE;
    @Value("${momo.access-key}")   private String ACCESS_KEY;
    @Value("${momo.secret-key}")   private String SECRET_KEY;
    @Value("${momo.return-url}")   private String REDIRECT_URL;
    @Value("${momo.ipn-url}")      private String IPN_URL;
    @Value("${momo.request-type}") private String REQUEST_TYPE;

    private final MomoApi momoApi;
    private final InvoiceRepository invoiceRepo;
    private final PaymentRepository paymentRepo;

    // Tạo phiên thanh toán cho 1 invoice
    @Transactional
    public CreateMomoResponse createForInvoice(String invoiceId) {
        Invoice inv = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        if (inv.getTotalAmount() == null || inv.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invoice totalAmount invalid");
        }
        if (inv.getStatus() == InvoiceStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invoice already PAID");
        }

        // MoMo yêu cầu số nguyên VND
        long amount = inv.getTotalAmount().setScale(0, RoundingMode.HALF_UP).longValueExact();

        String orderId = "INV-" + inv.getId() + "-" + System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().replace("-", "");
        String orderInfo = "Thanh toan hoa don phong " + inv.getRoom().getName();

        // extraData = base64(json)
        String extraJson = """
           {"invoiceId":"%s","roomId":"%s","month":"%s"}
        """.formatted(inv.getId(), inv.getRoom().getId(), inv.getMonth());
        String extraData = Base64.getEncoder().encodeToString(extraJson.getBytes(StandardCharsets.UTF_8));

        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                ACCESS_KEY, amount, extraData, IPN_URL, orderId, orderInfo, PARTNER_CODE, REDIRECT_URL, requestId, REQUEST_TYPE
        );

        String signature = hmacSHA256(rawSignature, SECRET_KEY);

        CreateMomoRequest req = CreateMomoRequest.builder()
                .partnerCode(PARTNER_CODE)
                .accessKey(ACCESS_KEY)
                .requestType(REQUEST_TYPE)
                .ipnUrl(IPN_URL)
                .redirectUrl(REDIRECT_URL)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .amount(amount)
                .extraData(extraData)
                .requestId(requestId)
                .lang("vi")
                .signature(signature)
                .build();

        CreateMomoResponse res = momoApi.createMomoQR(req);

        // Lưu 1 bản ghi Payment ở trạng thái PENDING (để track)
        Payment p = Payment.builder()
                .invoice(inv)
                .paymentDate(LocalDate.now())
                .method(PaymentMethod.QR)
                .amount(new BigDecimal(amount))
                .status(PaymentStatus.PENDING)
                .build();
        // lưu kèm orderId để đối soát (thêm field orderId trong Payment nếu muốn)
        // p.setOrderId(orderId);  // nếu bạn bổ sung cột order_id
        paymentRepo.save(p);

        return res;
    }

    // Xác minh IPN và cập nhật Invoice/Payment
    @Transactional
    public String handleIpn(Map<String, String> params) {
        // Lấy tham số IPN (tuỳ MoMo, một số key có thể null)
        String partnerCode = params.getOrDefault("partnerCode", "");
        String orderId     = params.getOrDefault("orderId", "");
        String requestId   = params.getOrDefault("requestId", "");
        String amount      = params.getOrDefault("amount", "0");
        String orderInfo   = params.getOrDefault("orderInfo", "");
        String orderType   = params.getOrDefault("orderType", "");
        String transId     = params.getOrDefault("transId", "");
        String resultCode  = params.getOrDefault("resultCode", "");
        String message     = params.getOrDefault("message", "");
        String payType     = params.getOrDefault("payType", "");
        String responseTime= params.getOrDefault("responseTime", "");
        String extraData   = params.getOrDefault("extraData", "");
        String signature   = params.getOrDefault("signature", "");

        // Raw theo spec IPN v2 (đối chiếu tài liệu MoMo của bạn)
        String raw = String.format(
                "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                ACCESS_KEY, amount, extraData, message, orderId, orderInfo, orderType, partnerCode, payType, requestId, responseTime, resultCode, transId
        );
        String calcSig = hmacSHA256(raw, SECRET_KEY);
        if (!calcSig.equals(signature)) {
            log.warn("MoMo IPN signature not match. orderId={}", orderId);
            return "INVALID_SIGNATURE";
        }

        // Giải extraData để lấy invoiceId
        String invoiceId;
        try {
            String json = new String(Base64.getDecoder().decode(extraData), StandardCharsets.UTF_8);
            // rất nhanh gọn, không cần lib JSON ở đây
            invoiceId = json.replaceAll(".*\"invoiceId\"\\s*:\\s*\"([^\"]+)\".*", "$1");
            if (invoiceId == null || invoiceId.isBlank() || invoiceId.equals(json)) {
                throw new IllegalArgumentException("invoiceId not found in extraData");
            }
        } catch (Exception e) {
            log.error("Cannot parse extraData", e);
            return "INVALID_EXTRA_DATA";
        }

        Invoice inv = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        boolean success = "0".equals(resultCode);
        BigDecimal paidAmount = new BigDecimal(amount);

        // Lưu payment (có thể tìm theo orderId nếu đã tạo PENDING trước đó)
        Payment payment = Payment.builder()
                .invoice(inv)
                .paymentDate(LocalDate.now())
                .method(PaymentMethod.QR)
                .amount(paidAmount)
                .status(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .build();
        paymentRepo.save(payment);

        // Nếu success → có thể auto-mark paid khi đủ
        if (success) {
            // tổng lại và mark
            BigDecimal totalSuccess = paymentRepo.sumSuccessAmount(invoiceId);
            if (totalSuccess.compareTo(inv.getTotalAmount()) >= 0) {
                inv.setStatus(InvoiceStatus.PAID);
                invoiceRepo.save(inv);
            }
        }

        return success ? "SUCCESS" : "FAILED";
    }

    private String hmacSHA256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC error", e);
        }
    }
}


