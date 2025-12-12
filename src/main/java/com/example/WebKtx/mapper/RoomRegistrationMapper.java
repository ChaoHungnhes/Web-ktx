package com.example.WebKtx.mapper;

import com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationCreateRequest;
import com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationResponse;
import com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationUpdateRequest;
import com.example.WebKtx.entity.RoomRegistration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomRegistrationMapper {

    // === CREATE: chỉ map các field cơ bản ===
    @Mapping(target="student", ignore = true)
    @Mapping(target="room", ignore = true)
    @Mapping(target="status", ignore = true)        // để @PrePersist lo
    @Mapping(target="requestType", ignore = true)   // để @PrePersist lo
    RoomRegistration toEntity(RoomRegistrationCreateRequest req);

    // === UPDATE: chỉ update field cơ bản, không đè student/room ===
    @Mapping(target="student", ignore = true)
    @Mapping(target="room", ignore = true)
    @Mapping(target="status", ignore = true)
    @Mapping(target="requestType", ignore = true)
    void update(@MappingTarget RoomRegistration entity, RoomRegistrationUpdateRequest req);

    // === toResponse: CHỈ dùng nếu bạn convert từ entity, không phải projection ===
    @Mapping(target="studentId", source="student.id")
    @Mapping(target="studentName", expression="java(entity.getStudent().getFirstName() + \" \" + entity.getStudent().getLastName())")
    @Mapping(target="gender", source="student.gender")
    @Mapping(target="academicYear", source="student.academicYear")
    @Mapping(target="className", source="student.className")

    @Mapping(target="roomId", source="room.id")
    @Mapping(target="roomName", source="room.name")
    @Mapping(target="dormId", source="room.dormitory.id")
    @Mapping(target="dormName", source="room.dormitory.name")

    RoomRegistrationResponse toResponse(RoomRegistration entity);
}
