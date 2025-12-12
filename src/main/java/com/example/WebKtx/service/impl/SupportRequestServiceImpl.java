package com.example.WebKtx.service.impl;

import com.example.WebKtx.common.Enum.SupportStatus;
import com.example.WebKtx.dto.ResultPaginationDTO;
import com.example.WebKtx.dto.SupportRequestDto.SupportRequestCreateRequest;
import com.example.WebKtx.dto.SupportRequestDto.SupportRequestResponse;
import com.example.WebKtx.dto.SupportRequestDto.SupportRequestUpdateRequest;
import com.example.WebKtx.entity.Student;
import com.example.WebKtx.entity.SupportRequest;
import com.example.WebKtx.entity.User;
import com.example.WebKtx.repository.StudentRepository;
import com.example.WebKtx.repository.SupportRequestRepository;
import com.example.WebKtx.repository.UserRepository;
import com.example.WebKtx.service.SupportRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.List;

import static com.example.WebKtx.common.Util.PaginationUtils.wrap;

@Service
@RequiredArgsConstructor
@Transactional
public class SupportRequestServiceImpl implements SupportRequestService {

    private final SupportRequestRepository repo;
    private final StudentRepository studentRepo;
    private final UserRepository userRepo;

    @Override
    public SupportRequestResponse create(SupportRequestCreateRequest req) {
        Student stu = studentRepo.findById(req.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "student not found"));

        SupportRequest entity = SupportRequest.builder()
                .student(stu)
                .roomName(req.getRoomName())
                .requestContent(req.getRequestContent())
                .noteExtend(req.getNoteExtend())
                .content(req.getContent())
                .status(SupportStatus.PROCESSING)
                .build();

        SupportRequest saved = repo.save(entity);
        return repo.findByIdAsDto(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    public SupportRequestResponse update(String id, SupportRequestUpdateRequest req) {
        SupportRequest sr = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (req.getStatus() != null) {
            sr.setStatus(req.getStatus());
            if (req.getStatus() == SupportStatus.RESOLVED || req.getStatus() == SupportStatus.REJECTED) {
                // nếu chuyển sang RESOLVED/REJECTED mà chưa có resolvedAt thì set now
                if (sr.getResolvedAt() == null) {
                    sr.setResolvedAt(LocalDateTime.now());
                }
            }
        }

        if (req.getHandledById() != null) {
            User handler = userRepo.findById(req.getHandledById())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "handler not found"));
            sr.setHandledBy(handler);
        }

        if (req.getNoteExtend() != null) sr.setNoteExtend(req.getNoteExtend());
        if (req.getContent() != null) sr.setContent(req.getContent());

        repo.save(sr);
        return repo.findByIdAsDto(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    public void delete(String id) {
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public SupportRequestResponse findById(String id) {
        return repo.findByIdAsDto(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getAll(Pageable pageable) {
        Page<SupportRequestResponse> page = repo.findAllAsDto(pageable);
        return wrap(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO searchByCreatedDate(LocalDate date, Pageable pageable) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX); // 23:59:59.999999999
        Page<SupportRequestResponse> page = repo.findByCreatedAtBetween(start, end, pageable);
        return wrap(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO searchByResolvedDate(LocalDate date, Pageable pageable) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        Page<SupportRequestResponse> page = repo.findByResolvedAtBetween(start, end, pageable);
        return wrap(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO searchByStatus(SupportStatus status, Pageable pageable) {
        Page<SupportRequestResponse> page = repo.findByStatusAsDto(status, pageable);
        return wrap(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO searchByRoomName(String roomName, Pageable pageable) {
        String keyword = (roomName == null || roomName.isBlank()) ? null : roomName;
        Page<SupportRequestResponse> page = repo.findByRoomNameLike(keyword, pageable);
        return wrap(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO searchByStudent(String studentId, Pageable pageable) {
        Page<SupportRequestResponse> page = repo.findByStudentId(studentId, pageable);
        return wrap(page);
    }

}

