package com.example.WebKtx.mapper;

import com.example.WebKtx.dto.DormitoryDto.DormitoryCreateRequest;
import com.example.WebKtx.dto.DormitoryDto.DormitoryResponse;
import com.example.WebKtx.dto.DormitoryDto.DormitoryUpdateRequest;
import com.example.WebKtx.entity.Dormitory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DormitoryMapper {
    Dormitory toEntity(DormitoryCreateRequest req);
    @Mapping(target="id", ignore=true)
    void update(@MappingTarget Dormitory entity, DormitoryUpdateRequest req);
    DormitoryResponse toResponse(Dormitory entity);
}
