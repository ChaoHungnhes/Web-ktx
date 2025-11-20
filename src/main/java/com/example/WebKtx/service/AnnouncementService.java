package com.example.WebKtx.service;

import com.example.WebKtx.common.Enum.Channel;
import com.example.WebKtx.common.Enum.Target;
import com.example.WebKtx.dto.Announcement.AnnouncementCreateRequest;
import com.example.WebKtx.dto.Announcement.AnnouncementResponse;
import com.example.WebKtx.dto.Announcement.AnnouncementUpdateRequest;
import com.example.WebKtx.dto.ResultPaginationDTO;
import com.example.WebKtx.entity.Announcement;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface AnnouncementService {
    AnnouncementResponse create(AnnouncementCreateRequest req);
    AnnouncementResponse update(String id, AnnouncementUpdateRequest req);
    void delete(String id);
    AnnouncementResponse findById(String id);
    ResultPaginationDTO getAll(Pageable pageable);

    ResultPaginationDTO search(String keyword, Target target, Channel channel, Pageable pageable);

    // upload ảnh cho 1 announcement
    AnnouncementResponse uploadImage(String id, MultipartFile file);
}
