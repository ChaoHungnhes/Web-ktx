package com.example.WebKtx.controller;

import com.example.WebKtx.anotation.ApiMessage;
import com.example.WebKtx.common.Enum.Channel;
import com.example.WebKtx.common.Enum.Target;
import com.example.WebKtx.dto.Announcement.AnnouncementCreateRequest;
import com.example.WebKtx.dto.Announcement.AnnouncementUpdateRequest;
import com.example.WebKtx.service.AnnouncementService;
import com.example.WebKtx.service.mail.BulkEmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/webktx/announcements")
@RequiredArgsConstructor
@Slf4j
public class AnnouncementController {

    private final AnnouncementService service;
    private final BulkEmailService bulkEmailService;

    @PostMapping("/create")
    @ApiMessage("Create announcement success")
    public ResponseEntity<?> create(@Valid @RequestBody AnnouncementCreateRequest req) {
        if(req.getChannel().equals(Channel.EMAIL)){
            int count;
            count = bulkEmailService.sendEmailToAllActiveUsers(
                    req.getTitle(),
                    req.getContent());
        }
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update announcement success")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody AnnouncementUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete announcement success")
    public ResponseEntity<?> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok("Delete complete");
    }

    @GetMapping("/{id}")
    @ApiMessage("Get announcement success")
    public ResponseEntity<?> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @ApiMessage("Get announcements (paged) success")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(service.getAll(PageRequest.of(pageIndex, pageSize, Sort.by("createdAt").descending())));
    }

    // Search: keyword + optional target/channel
    @GetMapping("/_search")
    @ApiMessage("Search announcements success")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Target target,
            @RequestParam(required = false) Channel channel,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(
                service.search(keyword, target, channel, PageRequest.of(pageIndex, pageSize, Sort.by("createdAt").descending()))
        );
    }

    // Upload ảnh (multipart)
    @PostMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage("Upload announcement image success")
    public ResponseEntity<?> uploadImage(@PathVariable String id, @RequestPart("file") MultipartFile file) {
        log.info("hiiiiiiiiiiiiiiiiiiiiiiiii");
        return ResponseEntity.ok(service.uploadImage(id, file));
    }
}

