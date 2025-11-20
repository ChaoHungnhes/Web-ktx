package com.example.WebKtx.service.impl;

import com.example.WebKtx.common.Enum.Channel;
import com.example.WebKtx.common.Enum.Target;
import com.example.WebKtx.dto.Announcement.AnnouncementCreateRequest;
import com.example.WebKtx.dto.Announcement.AnnouncementResponse;
import com.example.WebKtx.dto.Announcement.AnnouncementUpdateRequest;
import com.example.WebKtx.dto.ResultPaginationDTO;
import com.example.WebKtx.entity.Announcement;
import com.example.WebKtx.mapper.AnnouncementMapper;
import com.example.WebKtx.repository.AnnouncementRepository;
import com.example.WebKtx.repository.UserRepository;
import com.example.WebKtx.service.AnnouncementService;
import com.example.WebKtx.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository repo;
    private final UserRepository userRepo; // nếu cần set createdBy
    private final AnnouncementMapper mapper;
    private final FileStorageService fileStorageService; // sẽ tạo bên dưới

    @Override
    public AnnouncementResponse create(AnnouncementCreateRequest req) {
        Announcement a = mapper.toEntity(req);
        if (req.getCreatedById() != null) {
            a.setCreatedBy(userRepo.findById(req.getCreatedById()).orElse(null));
        }
        Announcement saved = repo.save(a);
        return mapper.toResponse(saved);
    }

    @Override
    public AnnouncementResponse update(String id, AnnouncementUpdateRequest req) {
        Announcement a = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapper.update(a, req);
        Announcement saved = repo.save(a);
        return mapper.toResponse(saved);
    }

    @Override
    public void delete(String id) {
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementResponse findById(String id) {
        Announcement a = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapper.toResponse(a);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getAll(Pageable pageable) {
        Page<Announcement> page = repo.findAll(pageable);
        List<AnnouncementResponse> items = page.getContent().stream().map(mapper::toResponse).toList();
        return wrap(pageable, page, items);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO search(String keyword, Target target, Channel channel, Pageable pageable) {
        Page<Announcement> page = repo.search(blankToNull(keyword), target, channel, pageable);
        List<AnnouncementResponse> items = page.getContent().stream().map(mapper::toResponse).toList();
        return wrap(pageable, page, items);
    }

    @Override
    public AnnouncementResponse uploadImage(String id, MultipartFile file) {
        Announcement a = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String url = fileStorageService.saveAnnouncementImage(file); // trả về URL public
        a.setImageUrl(url);
        repo.save(a);
        return mapper.toResponse(a);
    }

    private String blankToNull(String s) { return (s == null || s.isBlank()) ? null : s; }

    private ResultPaginationDTO wrap(Pageable pageable, Page<?> page, List<?> items) {
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        ResultPaginationDTO dto = new ResultPaginationDTO();
        dto.setMeta(meta);
        dto.setResult(items);
        return dto;
    }
}

