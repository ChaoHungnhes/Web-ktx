package com.example.WebKtx.payment.model;
import lombok.*;

@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class CreateMomoRequest {
    private String partnerCode;
    private String accessKey;    // <-- BỔ SUNG
    private String requestType;
    private String ipnUrl;
    private String orderId;
    private long amount;
    private String orderInfo;
    private String requestId;
    private String lang;
    private String redirectUrl;
    private String extraData;
    private String signature;
}

