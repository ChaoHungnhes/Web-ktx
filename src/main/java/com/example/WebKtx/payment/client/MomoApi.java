package com.example.WebKtx.payment.client;

import com.example.WebKtx.payment.model.CreateMomoRequest;
import com.example.WebKtx.payment.model.CreateMomoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "momo", url = "${momo.end-point}")
public interface MomoApi {
    @PostMapping("/create")
    CreateMomoResponse createMomoQR(@RequestBody CreateMomoRequest createMomoRequest);
}
