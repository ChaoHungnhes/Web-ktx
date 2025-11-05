package com.example.WebKtx.service.impl;

import com.example.WebKtx.dto.StudentDto.StudentCreateRequest;
import com.example.WebKtx.dto.StudentDto.StudentInRoomResponse;
import com.example.WebKtx.dto.StudentDto.StudentResponse;
import com.example.WebKtx.dto.StudentDto.StudentUpdateRequest;
import com.example.WebKtx.entity.Room;
import com.example.WebKtx.entity.Student;
import com.example.WebKtx.entity.User;
import com.example.WebKtx.mapper.StudentMapper;
import com.example.WebKtx.repository.DormitoryRepository;
import com.example.WebKtx.repository.RoomRepository;
import com.example.WebKtx.repository.StudentRepository;
import com.example.WebKtx.repository.UserRepository;
import com.example.WebKtx.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository repo;
    private final DormitoryRepository dormRepo;
    private final RoomRepository roomRepo;
    private final UserRepository userRepo;
    private final StudentMapper mapper;

    @Transactional
    @Override
    public StudentResponse create(StudentCreateRequest req) {
        if (repo.existsById(req.getId())) throw new ResponseStatusException(HttpStatus.CONFLICT, "student_id existed");
        if (repo.existsByIdentityNumber(req.getIdentityNumber())) throw new ResponseStatusException(HttpStatus.CONFLICT, "identity existed");

        Student e = mapper.toEntity(req);
        e.setDormitory(dormRepo.findById(req.getDormId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "dorm not found")));

        if (req.getRoomId() != null) {
            Room room = roomRepo.findById(req.getRoomId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "room not found"));
            // check capacity
            long now = repo.countByRoomId(room.getId());
            if (now >= room.getMaxOccupants()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "room is full");
            e.setRoom(room);
        }

        if (req.getUserId() != null) {
            User u = userRepo.findById(req.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not found"));
            e.setUser(u);
        }

        Student saved = repo.save(e);
        // sync current occupants
        if (saved.getRoom() != null) {
            Room r = saved.getRoom();
            r.setCurrentOccupants((int) repo.countByRoomId(r.getId()));
            roomRepo.save(r);
        }
        return mapper.toResponse(saved);
    }

    @Transactional
    @Override
    public StudentResponse update(String id, StudentUpdateRequest req) {
        Student e = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String oldRoomId = e.getRoom() != null ? e.getRoom().getId() : null;

        mapper.update(e, req);

        e.setDormitory(dormRepo.findById(req.getDormId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "dorm not found")));

        if (req.getRoomId() != null && !req.getRoomId().isBlank()) {
            Room room = roomRepo.findById(req.getRoomId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "room not found"));
            long now = repo.countByRoomId(room.getId());
            // nếu chuyển phòng, loại trừ chính student hiện tại
            if (e.getRoom() == null || !room.getId().equals(oldRoomId)) {
                if (now >= room.getMaxOccupants()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "room is full");
            }
            e.setRoom(room);
        } else {
            e.setRoom(null);
        }

        if (req.getUserId() != null && !req.getUserId().isBlank()) {
            User u = userRepo.findById(req.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not found"));
            e.setUser(u);
        } else {
            e.setUser(null);
        }

        Student saved = repo.save(e);

        // sync rooms
        if (oldRoomId != null) {
            Room old = roomRepo.findById(oldRoomId).orElse(null);
            if (old != null) { old.setCurrentOccupants((int) repo.countByRoomId(old.getId())); roomRepo.save(old); }
        }
        if (saved.getRoom() != null) {
            Room nr = saved.getRoom();
            nr.setCurrentOccupants((int) repo.countByRoomId(nr.getId()));
            roomRepo.save(nr);
        }

        return mapper.toResponse(saved);
    }

    @Transactional
    @Override
    public void delete(String id) {
        Student e = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String roomId = e.getRoom() != null ? e.getRoom().getId() : null;
        repo.delete(e);
        if (roomId != null) {
            Room r = roomRepo.findById(roomId).orElse(null);
            if (r != null) { r.setCurrentOccupants((int) repo.countByRoomId(r.getId())); roomRepo.save(r); }
        }
    }

    @Override
    public StudentResponse findById(String id) {
        return repo.findById(id).map(mapper::toResponse).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<StudentResponse> getAll() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public List<StudentInRoomResponse> getStudentsInRoom(String roomId) {
        if (!roomRepo.existsById(roomId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "room not found");
        }
        return repo.findByRoomIdAsDto(roomId);
    }
}

