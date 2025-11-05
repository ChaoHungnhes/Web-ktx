package com.example.WebKtx.mapper;

import com.example.WebKtx.dto.RoomDto.RoomCreateRequest;
import com.example.WebKtx.dto.RoomDto.RoomResponse;
import com.example.WebKtx.dto.RoomDto.RoomUpdateRequest;
import com.example.WebKtx.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(target="dormitory", ignore = true)
    @Mapping(target="currentOccupants", constant = "0")
    Room toEntity(RoomCreateRequest req);

    @Mapping(target="dormitory", ignore = true)
    void update(@MappingTarget Room entity, RoomUpdateRequest req);

    @Mapping(target="dormId", source="dormitory.id")
    @Mapping(target="dormName", source="dormitory.name")
    RoomResponse toResponse(Room entity);
}
