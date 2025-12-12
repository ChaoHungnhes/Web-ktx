package com.example.WebKtx.service;

import com.example.WebKtx.common.Enum.SupportStatus;
import com.example.WebKtx.dto.ResultPaginationDTO;
import com.example.WebKtx.dto.SupportRequestDto.SupportRequestCreateRequest;
import com.example.WebKtx.dto.SupportRequestDto.SupportRequestResponse;
import com.example.WebKtx.dto.SupportRequestDto.SupportRequestUpdateRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface SupportRequestService {

    SupportRequestResponse create(SupportRequestCreateRequest req);
    SupportRequestResponse update(String id, SupportRequestUpdateRequest req);
    void delete(String id);
    SupportRequestResponse findById(String id);

    ResultPaginationDTO getAll(Pageable pageable);

    // search
    ResultPaginationDTO searchByCreatedDate(LocalDate date, Pageable pageable);
    ResultPaginationDTO searchByResolvedDate(LocalDate date, Pageable pageable);
    ResultPaginationDTO searchByStatus(SupportStatus status, Pageable pageable);
    ResultPaginationDTO searchByRoomName(String roomName, Pageable pageable);
    ResultPaginationDTO searchByStudent(String studentId, Pageable pageable);

}

