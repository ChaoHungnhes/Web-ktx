package com.example.WebKtx.payment.Momo.controller;

import com.example.WebKtx.payment.Momo.constant.MomoParameter;
import com.example.WebKtx.payment.Momo.model.CreateMomoResponse;
import com.example.WebKtx.payment.Momo.service.MomoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/momo")
public class MomoController {
    private final MomoService momoService;
    @PostMapping("create")
    public CreateMomoResponse createQR(@RequestParam String invoiceId){
        return momoService.createForInvoice(invoiceId);
    }
    @GetMapping("ipn-handler")
    public String ipnHandler(@RequestParam Map<String ,String> request){
        Integer resultCode = Integer.valueOf(request.get(MomoParameter.RESULT_CODE));
        return resultCode == 0 ? "Giao dich thanh cong" : "Giao dich that bai";
    }
}
