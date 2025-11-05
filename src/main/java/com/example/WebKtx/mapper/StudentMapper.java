package com.example.WebKtx.mapper;

import com.example.WebKtx.dto.StudentDto.StudentCreateRequest;
import com.example.WebKtx.dto.StudentDto.StudentResponse;
import com.example.WebKtx.dto.StudentDto.StudentUpdateRequest;
import com.example.WebKtx.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mapping(target="dormitory", ignore = true)
    @Mapping(target="room", ignore = true)
    @Mapping(target="user", ignore = true)
    Student toEntity(StudentCreateRequest req);

    @Mapping(target="dormitory", ignore = true)
    @Mapping(target="room", ignore = true)
    @Mapping(target="user", ignore = true)
    void update(@MappingTarget Student entity, StudentUpdateRequest req);

    @Mapping(target="dormId",   source="dormitory.id")
    @Mapping(target="dormName", source="dormitory.name")
    @Mapping(target="roomId",   source="room.id")
    @Mapping(target="roomName", source="room.name")
    @Mapping(target="userId",   source="user.id")
    @Mapping(target="userEmail",source="user.email")
    StudentResponse toResponse(Student entity);
}
