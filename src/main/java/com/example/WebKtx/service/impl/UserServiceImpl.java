package com.example.WebKtx.service.impl;

import com.example.WebKtx.common.Enum.RoleEnum;
import com.example.WebKtx.common.ErrorCode;
import com.example.WebKtx.dto.userDto.UserCreateRequest;
import com.example.WebKtx.dto.userDto.UserCreateResponse;
import com.example.WebKtx.dto.userDto.UserResponse;
import com.example.WebKtx.dto.userDto.UserUpdateRequest;
import com.example.WebKtx.entity.Role;
import com.example.WebKtx.entity.User;
import com.example.WebKtx.exception.AppException;
import com.example.WebKtx.mapper.UserMapper;
import com.example.WebKtx.repository.RoleRepository;
import com.example.WebKtx.repository.StudentRepository;
import com.example.WebKtx.repository.UserRepository;
import com.example.WebKtx.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.WebKtx.common.ErrorCode.STUDENT_NOT_EXISTED;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired private final UserMapper userMapper;
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
    private final StudentRepository studentRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    @PostAuthorize("hasRole('ADMIN')")
    public UserCreateResponse create(UserCreateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Current user: {}", auth.getName());
        log.info("Authorities: {}", auth.getAuthorities());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role userRole = roleRepository.findById(RoleEnum.USER.name())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND)); // tự định nghĩa ErrorCode

        user.setRoles(Set.of(userRole));
        user = userRepository.save(user);
        UserCreateResponse newUser = userMapper.toUserResponse(user);
        newUser.setRoles(user.getRoles());
        return newUser;
    }


    // Trong UserServiceImpl
    @Override
    @Transactional
// @PreAuthorize("hasRole('ADMIN')") // Nên chặn ở Controller hoặc Service
    public UserCreateResponse update(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 1. Cập nhật thông tin cơ bản
        if (request.getName() != null) user.setName(request.getName());

        // 2. Cập nhật trạng thái (Active/Close)
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        // 3. Cập nhật Password nếu có (Encode)
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // 4. Cập nhật Quyền (Roles) - Logic quan trọng cho Admin
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> newRoles = new HashSet<>();
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findById(roleName)
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
                newRoles.add(role);
            }
            user.setRoles(newRoles);
        }

        user = userRepository.save(user);

        // Map response thủ công hoặc dùng mapper
        UserCreateResponse res = userMapper.toUserResponse(user);
        res.setRoles(user.getRoles());
        return res;
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!userRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserCreateResponse findById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getMyInfo() {
        var context =
                SecurityContextHolder
                        .getContext(); // khi 1 request xác thực thành công thì tt user đăng nhập đc lưu trữ trong
        // SecurityContextHolder
        String name = context.getAuthentication().getName();
        log.info(name);
        User user = userRepository.findByEmail(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String studentId = String.valueOf(studentRepository.findStudentIdByUserId(user.getId()).orElseThrow(() -> new AppException(STUDENT_NOT_EXISTED)));
        log.info(studentId);
        UserResponse userResponse = userMapper.toMyUserResponse(user);
        userResponse.setStudentId(studentId);
        return userResponse;
    }

}
