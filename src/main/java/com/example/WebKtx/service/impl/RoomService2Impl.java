package com.example.WebKtx.service.impl;

import com.example.WebKtx.dto.ServicePriceDto;
import com.example.WebKtx.entity.RoomService;
import com.example.WebKtx.repository.RoomServiceRepository;
import com.example.WebKtx.service.RoomService2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService2Impl implements RoomService2 {
    private final RoomServiceRepository repository;
    @Override
    public List<ServicePriceDto> getDienNuoc() {
        var targets = java.util.Set.of("Dien", "Nuoc");

        List<ServicePriceDto> result = repository.findAll().stream()
                .filter(s -> s.getName() != null)
                .map(s -> new ServicePriceDto(
                        s.getName().trim(),
                        s.getPrice() == null ? java.math.BigDecimal.ZERO : s.getPrice()
                ))
                .filter(dto -> targets.contains(dto.getName()))
                .collect(java.util.stream.Collectors.toList());
        return result;
    }
}
