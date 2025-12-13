package com.example.WebKtx.service;

import com.example.WebKtx.dto.StudentDto.StudentCreateRequest;
import com.example.WebKtx.dto.StudentDto.StudentInRoomResponse;
import com.example.WebKtx.dto.StudentDto.StudentResponse;
import com.example.WebKtx.dto.StudentDto.StudentUpdateRequest;
import jakarta.transaction.Transactional;

import java.util.List;

public interface StudentService {
    StudentResponse create(StudentCreateRequest req);
    StudentResponse update(String id, StudentUpdateRequest req);


    void delete(String id);
    StudentResponse findById(String id);

    List<StudentResponse> getAll();
    List<StudentInRoomResponse> getStudentsInRoom(String roomId);
    void removeStudentFromRoom(String studentId);
}
