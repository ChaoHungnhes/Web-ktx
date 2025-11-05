package com.example.WebKtx.service.impl;

import com.example.WebKtx.dto.DormitoryDto.DormitoryCreateRequest;
import com.example.WebKtx.dto.DormitoryDto.DormitoryResponse;
import com.example.WebKtx.dto.DormitoryDto.DormitoryUpdateRequest;
import com.example.WebKtx.entity.Dormitory;
import com.example.WebKtx.mapper.DormitoryMapper;
import com.example.WebKtx.repository.DormitoryRepository;
import com.example.WebKtx.service.DormitoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DormitoryServiceImpl implements DormitoryService {
    private final DormitoryRepository repo;
    private final DormitoryMapper mapper;
    @Override
    public DormitoryResponse create(DormitoryCreateRequest req) {
        if (repo.existsById(req.getId())) throw new ResponseStatusException(HttpStatus.CONFLICT, "dorm_id existed");
        if (repo.existsByNameIgnoreCase(req.getName())) throw new ResponseStatusException(HttpStatus.CONFLICT, "name existed");
        Dormitory entity = mapper.toEntity(req);
        return mapper.toResponse(repo.save(entity));
    }
    @Override
    public DormitoryResponse update(String id, DormitoryUpdateRequest req) {
        Dormitory entity = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapper.update(entity, req);
        return mapper.toResponse(repo.save(entity));
    }

    @Override
    public void delete(String id) {
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        repo.deleteById(id);
    }

    @Override
    public DormitoryResponse findById(String id) {
        return repo.findById(id).map(mapper::toResponse).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<DormitoryResponse> getAll() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllNames() {
        return repo.findAllNames();
    }
}

