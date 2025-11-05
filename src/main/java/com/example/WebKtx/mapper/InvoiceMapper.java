package com.example.WebKtx.mapper;

import com.example.WebKtx.dto.InvoiceDto.InvoiceResponse;
import com.example.WebKtx.dto.InvoiceDto.InvoiceSummaryResponse;
import com.example.WebKtx.entity.Invoice;
import com.example.WebKtx.entity.ServiceDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class InvoiceMapper {

    public InvoiceSummaryResponse toSummary(Invoice i) {
        return InvoiceSummaryResponse.builder()
                .id(i.getId())
                .roomId(i.getRoom().getId())
                .roomName(i.getRoom().getName())
                .month(i.getMonth())
                .createdAt(i.getCreatedAt())
                .roomFee(i.getRoomFee())
                .totalServiceFee(i.getTotalServiceFee())
                .totalAmount(i.getTotalAmount())
                .status(i.getStatus())
                .build();
    }

    public InvoiceResponse toResponse(Invoice i, List<ServiceDetail> details) {
        BigDecimal elec = BigDecimal.ZERO, water = BigDecimal.ZERO, extra = BigDecimal.ZERO;
        List<InvoiceResponse.InvoiceResponseDetail> lines = new ArrayList<>();

        for (ServiceDetail sd : details) {
            String svc = sd.getService().getName();
            if (svc.equalsIgnoreCase("Dien"))  elec  = elec.add(sd.getTotalAmount());
            else if (svc.equalsIgnoreCase("Nuoc")) water = water.add(sd.getTotalAmount());
            else extra = extra.add(sd.getTotalAmount());

            lines.add(InvoiceResponse.InvoiceResponseDetail.builder()
                    .detailId(sd.getId())
                    .serviceId(sd.getService().getId())
                    .serviceName(sd.getService().getName())
                    .quantity(sd.getQuantity())
                    .unit(sd.getUnit())
                    .unitPrice(sd.getUnitPrice())
                    .totalAmount(sd.getTotalAmount())
                    .description(sd.getDescription())
                    .build());
        }

        return InvoiceResponse.builder()
                .id(i.getId())
                .roomId(i.getRoom().getId())
                .roomName(i.getRoom().getName())
                .month(i.getMonth())
                .createdAt(i.getCreatedAt())
                .roomFee(i.getRoomFee())
                .electricityFee(elec)
                .waterFee(water)
                .extraFee(extra)
                .totalServiceFee(i.getTotalServiceFee())
                .totalAmount(i.getTotalAmount())
                .status(i.getStatus())
                .details(lines)
                .build();
    }
}

