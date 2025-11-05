package com.example.WebKtx.mapper;

import com.example.WebKtx.dto.userDto.UserCreateRequest;
import com.example.WebKtx.dto.userDto.UserCreateResponse;
import com.example.WebKtx.dto.userDto.UserResponse;
import com.example.WebKtx.dto.userDto.UserUpdateRequest;
import com.example.WebKtx.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreateRequest userCreateRequest);

    @Mapping(target = "roles", ignore = true)
    UserCreateResponse toUserResponse(User user); // map data user vao userResponse

    @Mapping(target = "roles", ignore = true)
    void updateUser(
            @MappingTarget User user, UserUpdateRequest request);
    @Mapping(target = "roles", ignore = true)
    UserResponse toMyUserResponse(User user);
}
