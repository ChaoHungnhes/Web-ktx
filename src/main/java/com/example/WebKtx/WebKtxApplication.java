package com.example.WebKtx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class WebKtxApplication {
	public static void main(String[] args) {
		SpringApplication.run(WebKtxApplication.class, args);
	}

}
