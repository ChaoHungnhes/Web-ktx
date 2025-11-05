package com.example.WebKtx.service;

import com.example.WebKtx.dto.DormitoryDto.DormitoryCreateRequest;
import com.example.WebKtx.dto.DormitoryDto.DormitoryResponse;
import com.example.WebKtx.dto.DormitoryDto.DormitoryUpdateRequest;

import java.util.List;

public interface DormitoryService {
    DormitoryResponse create(DormitoryCreateRequest req);
    DormitoryResponse update(String id, DormitoryUpdateRequest req);
    void delete(String id);
    DormitoryResponse findById(String id);
    public List<DormitoryResponse> getAll();
    List<String> getAllNames();
}
