package com.example.WebKtx.service;

import com.example.WebKtx.dto.ResultPaginationDTO;
import com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationCreateRequest;
import com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationResponse;
import com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationUpdateRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomRegistrationService {
    RoomRegistrationResponse create(RoomRegistrationCreateRequest req);
    RoomRegistrationResponse update(String id, RoomRegistrationUpdateRequest req);
    void delete(String id);
    RoomRegistrationResponse findById(String id);

    ResultPaginationDTO getAllPaged(Pageable pageable);
    List<RoomRegistrationResponse> findByStudent(String studentId);

    ResultPaginationDTO getAllByApproved(Pageable pageable);
    ResultPaginationDTO getAllByPending(Pageable pageable);
    ResultPaginationDTO getAllByRejected(Pageable pageable);
    RoomRegistrationResponse approve(String id);
    RoomRegistrationResponse reject(String id);
}
