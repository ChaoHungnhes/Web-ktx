package com.example.WebKtx.payment.VNpay.Service;

import com.example.WebKtx.common.Enum.PaymentMethod;
import com.example.WebKtx.common.Enum.PaymentStatus;
import com.example.WebKtx.payment.VNpay.VNpayDto.VnpayCreatePaymentRequest;
import com.example.WebKtx.payment.VNpay.VNpayDto.VnpayCreatePaymentResponse;
import com.example.WebKtx.entity.Invoice;
import com.example.WebKtx.entity.Payment;
import com.example.WebKtx.entity.Student;
import com.example.WebKtx.payment.VNpay.Util.VnpayUtil;
import com.example.WebKtx.repository.InvoiceRepository;
import com.example.WebKtx.repository.PaymentRepository;
import com.example.WebKtx.repository.StudentRepository;
import com.example.WebKtx.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VnpayService {

    @Value("${vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnpHashSecret;

    @Value("${vnpay.pay-url}")
    private String vnpPayUrl;

    @Value("${vnpay.return-url}")
    private String vnpReturnUrl;

    @Value("${vnpay.ipn-url}")
    private String vnpIpnUrl;

    @Value("${vnpay.curr-code}")
    private String vnpCurrCode;

    @Value("${vnpay.locale}")
    private String vnpLocale;

    @Value("${vnpay.version}")
    private String vnpVersion;

    @Value("${vnpay.command}")
    private String vnpCommand;

    private final InvoiceRepository invoiceRepo;
    private final StudentRepository studentRepo;
    private final PaymentRepository paymentRepo;
    private final VnpayUtil vnpayUtil;
    private final InvoiceService invoiceService; // để gọi markPaidIfEnough

    /**
     * Tạo URL thanh toán cho 1 hóa đơn (full amount chưa trả)
     */
    @Transactional
    public VnpayCreatePaymentResponse createPaymentUrl(VnpayCreatePaymentRequest req, String clientIp) {
        Invoice invoice = invoiceRepo.findById(req.getInvoiceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "invoice not found"));

        // số tiền còn phải trả (totalAmount - sumSuccess)
        BigDecimal paid = paymentRepo.sumSuccessAmount(invoice.getId());
        BigDecimal remain = invoice.getTotalAmount().subtract(paid);
        if (remain.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invoice already fully paid");
        }

        Student payer = null;
        if (req.getPayerStudentId() != null) {
            payer = studentRepo.findById(req.getPayerStudentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "student not found"));
        }

        long amountVnd = remain.longValue();      // total (VND)
        long vnpAmount = amountVnd * 100L;        // VNPAY yêu cầu x100

        String orderId = UUID.randomUUID().toString().replace("-", "");
        String orderInfo = "Thanh toan hoa don phong " + invoice.getRoom().getName()
                + " thang " + invoice.getMonth();

        // 1. Tạo record Payment với status PENDING
        Payment payment = Payment.builder()
                .invoice(invoice)
                .payer(payer)
                .paymentDate(LocalDate.now())
                .method(PaymentMethod.EWALLET) // thêm enum VNPAY
                .amount(remain)
                .status(PaymentStatus.PENDING)
                .vnpOrderId(orderId)
                .build();
        payment = paymentRepo.save(payment);

        // 2. Build trường VNPAY
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnpVersion);
        vnpParams.put("vnp_Command", vnpCommand);
        vnpParams.put("vnp_TmnCode", vnpTmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(vnpAmount));
        vnpParams.put("vnp_CurrCode", vnpCurrCode);
        vnpParams.put("vnp_TxnRef", orderId);
        vnpParams.put("vnp_OrderInfo", orderInfo);
        vnpParams.put("vnp_OrderType", "billpayment");
        vnpParams.put("vnp_Locale", vnpLocale);
        vnpParams.put("vnp_IpAddr", clientIp != null ? clientIp : "127.0.0.1");
        vnpParams.put("vnp_ReturnUrl", vnpReturnUrl);
        vnpParams.put("vnp_CreateDate",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        vnpParams.put("vnp_ExpireDate",
                LocalDateTime.now().plusMinutes(15)
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

// ❌ KHÔNG gửi vnp_IpnUrl sang VNPAY
// vnpParams.put("vnp_IpnUrl", vnpIpnUrl);

        // build hashData để log
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        boolean first = true;
        for (String name : fieldNames) {
            String value = vnpParams.get(name);
            if (value == null || value.isEmpty()) continue;
            if (!first) hashData.append('&');
            first = false;
            hashData.append(name).append('=').append(value);
        }

        log.info("VNPAY CREATE - hashData = {}", hashData.toString());

// tính secureHash
        String secureHash = vnpayUtil.hmacSHA512(vnpHashSecret, hashData.toString());
        log.info("VNPAY CREATE - secureHash = {}", secureHash);

// build final URL
        String paymentUrl =
                vnpayUtil.buildQueryUrl(vnpPayUrl, vnpParams, vnpHashSecret);

        log.info("VNPAY CREATE - paymentUrl = {}", paymentUrl);



        return VnpayCreatePaymentResponse.builder()
                .paymentUrl(paymentUrl)
                .orderInfo(orderInfo)
                .invoiceId(invoice.getId())
                .paymentId(payment.getId())
                .build();
    }

    /**
     * Xử lý IPN VNPAY
     */
    @Transactional
    public String handleIpn(Map<String, String> vnpParams) {
        log.info("VNPAY IPN: {}", vnpParams);

        String vnpSecureHash = vnpParams.get("vnp_SecureHash");
        if (vnpSecureHash == null) {
            return "Invalid request";
        }

        // copy params trừ vnp_SecureHash & vnp_SecureHashType
        Map<String, String> fields = new HashMap<>();
        for (Map.Entry<String, String> e : vnpParams.entrySet()) {
            String k = e.getKey();
            if (!"vnp_SecureHash".equals(k) && !"vnp_SecureHashType".equals(k)) {
                fields.put(k, e.getValue());
            }
        }

        // build hashData giống bên gửi
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        // Trong hàm handleIpn
        StringBuilder hashData = new StringBuilder();
        boolean first = true;
        for (String name : fieldNames) {
            String value = fields.get(name);
            if (value == null || value.isEmpty()) continue;

            if (!first) hashData.append('&');
            first = false;

            // --- ĐOẠN QUAN TRỌNG: Cần Encode lại giống lúc gửi đi ---
            try {
                // Encode cả key và value để khớp với thuật toán lúc tạo URL
                String encodedName = URLEncoder.encode(name, StandardCharsets.US_ASCII.toString());
                String encodedValue = URLEncoder.encode(value, StandardCharsets.US_ASCII.toString());
                hashData.append(encodedName).append('=').append(encodedValue);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace(); // Xử lý lỗi
            }
        }

// Sau đó mới hash
        String recomputedHash = vnpayUtil.hmacSHA512(vnpHashSecret, hashData.toString());
        if (!vnpSecureHash.equalsIgnoreCase(recomputedHash)) {
            log.warn("VNPAY IPN invalid hash");
            return "Invalid checksum";
        }

        // --- xử lý nghiệp vụ như bạn đang làm ---
        String vnpTxnRef = vnpParams.get("vnp_TxnRef");
        String vnpTransactionNo = vnpParams.get("vnp_TransactionNo");
        String vnpTransactionStatus = vnpParams.get("vnp_TransactionStatus");

        Optional<Payment> optPayment = paymentRepo.findByVnpOrderId(vnpTxnRef);
        if (optPayment.isEmpty()) {
            return "Order not found";
        }
        Payment payment = optPayment.get();

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return "OK";
        }

        if ("00".equals(vnpTransactionStatus)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setVnpTransactionNo(vnpTransactionNo);
            paymentRepo.save(payment);
            invoiceService.markPaidIfEnough(payment.getInvoice().getId());
            return "OK";
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepo.save(payment);
            return "Failed";
        }
    }

}

