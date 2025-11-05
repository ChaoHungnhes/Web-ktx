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

    // student, room sẽ set trong service
    @Mapping(target="student", ignore = true)
    @Mapping(target="room", ignore = true)
    RoomRegistration toEntity(RoomRegistrationCreateRequest req);

    // room có thể đổi trong service nếu req.roomId != null
    @Mapping(target="student", ignore = true)
    @Mapping(target="room", ignore = true)
    void update(@MappingTarget RoomRegistration entity, RoomRegistrationUpdateRequest req);

    @Mapping(target="studentId", source="student.id")
    @Mapping(target="roomId", source="room.id")
    @Mapping(target="dormId", source="room.dormitory.id")
    @Mapping(target="dormName", source="room.dormitory.name")
        // studentName sẽ tạo ở repo projection; nếu map từ entity có thể concat first+last ở service
    RoomRegistrationResponse toResponse(RoomRegistration entity);
}
