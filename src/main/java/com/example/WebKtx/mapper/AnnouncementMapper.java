package com.example.WebKtx.mapper;

import com.example.WebKtx.dto.Announcement.AnnouncementCreateRequest;
import com.example.WebKtx.dto.Announcement.AnnouncementResponse;
import com.example.WebKtx.dto.Announcement.AnnouncementUpdateRequest;
import com.example.WebKtx.entity.Announcement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AnnouncementMapper {
    @Mapping(target = "createdBy", ignore = true)
    Announcement toEntity(AnnouncementCreateRequest req);

    @Mapping(target = "createdBy", ignore = true)
    void update(@MappingTarget Announcement entity, AnnouncementUpdateRequest req);

    @Mapping(target = "createdById", source = "createdBy.id")
    AnnouncementResponse toResponse(Announcement entity);
}
