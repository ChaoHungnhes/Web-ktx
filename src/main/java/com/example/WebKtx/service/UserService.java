package com.example.WebKtx.service;

import com.example.WebKtx.dto.userDto.UserCreateRequest;
import com.example.WebKtx.dto.userDto.UserCreateResponse;
import com.example.WebKtx.dto.userDto.UserResponse;
import com.example.WebKtx.dto.userDto.UserUpdateRequest;
import com.example.WebKtx.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    List<User> getAll();
    UserCreateResponse create(UserCreateRequest request);

    UserCreateResponse update(String id, UserUpdateRequest request);
    void delete(String id);
    UserCreateResponse findById(String id);
    UserResponse getMyInfo();

}
