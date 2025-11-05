package com.example.WebKtx.service.impl;

import com.example.WebKtx.common.Enum.RegistrationStatus;
import com.example.WebKtx.dto.ResultPaginationDTO;
import com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationCreateRequest;
import com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationResponse;
import com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationUpdateRequest;
import com.example.WebKtx.entity.Room;
import com.example.WebKtx.entity.RoomRegistration;
import com.example.WebKtx.entity.Student;
import javax.annotation.processing.Generated;

import com.example.WebKtx.mapper.RoomRegistrationMapper;
import com.example.WebKtx.repository.RoomRegistrationRepository;
import com.example.WebKtx.repository.RoomRepository;
import com.example.WebKtx.repository.StudentRepository;
import com.example.WebKtx.service.RoomRegistrationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.example.WebKtx.common.Util.PaginationUtils.wrap;

@Service
@RequiredArgsConstructor
public class RoomRegistrationServiceImpl implements RoomRegistrationService {

    private final RoomRegistrationRepository repo;
    private final StudentRepository studentRepo;
    private final RoomRepository roomRepo;
    private final RoomRegistrationMapper mapper;

    @Override
    @Transactional
    public RoomRegistrationResponse create(RoomRegistrationCreateRequest req) {
        Student student = studentRepo.findById(req.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "student not found"));
        Room room = roomRepo.findById(req.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "room not found"));

        RoomRegistration entity = mapper.toEntity(req);
        entity.setStudent(student);
        entity.setRoom(room);

        RoomRegistration saved = repo.save(entity);
        // Trả về projection đầy đủ field
        return repo.findByIdAsDto(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "cannot load registration"));
    }

    @Override
    @Transactional
    public RoomRegistrationResponse update(String id, RoomRegistrationUpdateRequest req) {
        RoomRegistration entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "registration not found"));

        mapper.update(entity, req);

        if (req.getRoomId() != null && !req.getRoomId().isBlank()
                && (entity.getRoom() == null || !req.getRoomId().equals(entity.getRoom().getId()))) {
            Room room = roomRepo.findById(req.getRoomId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "room not found"));
            entity.setRoom(room);
        }

        repo.save(entity);
        return repo.findByIdAsDto(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "cannot load registration"));
    }

    @Override
    public void delete(String id) {
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "registration not found");
        repo.deleteById(id);
    }

    @Override
    public RoomRegistrationResponse findById(String id) {
        return repo.findByIdAsDto(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "registration not found"));
    }

    @Override
    public ResultPaginationDTO getAllPaged(Pageable pageable) {
        Page<RoomRegistrationResponse> page = repo.findAllAsDto(pageable);
        return wrap(page); // như trước
    }

    @Override
    public List<RoomRegistrationResponse> findByStudent(String studentId) {
        if (!studentRepo.existsById(studentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found");
        }
        return repo.findAllByStudentAsDto(studentId);
    }

    @Override
    public ResultPaginationDTO getAllByApproved(Pageable pageable) {
        Page<RoomRegistrationResponse> page = repo.findAllByApproved(pageable);
        return wrap(page);
    }

    @Override
    public ResultPaginationDTO getAllByPending(Pageable pageable) {
        Page<RoomRegistrationResponse> page = repo.findAllByPending(pageable);
        return wrap(page);
    }

    @Override
    public ResultPaginationDTO getAllByRejected(Pageable pageable) {
        Page<RoomRegistrationResponse> page = repo.findAllByRejected(pageable);
        return wrap(page);
    }

    @Override
    @Transactional
    public RoomRegistrationResponse approve(String id) {
        RoomRegistration rr = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "registration not found"));

        Room room = rr.getRoom();
        if (room == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "room not set for registration");
        }

        // Phòng còn chỗ? (đếm thực tế từ Student)
        int actual = (int) roomRepo.countStudentsInRoom(room.getId());
        if (actual >= room.getMaxOccupants()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "room is full");
        }

        // Cập nhật trạng thái
        rr.setStatus(RegistrationStatus.APPROVED);

        // (Tuỳ chọn) gán phòng cho student khi duyệt
        Student stu = rr.getStudent();
        if (stu != null) {
            stu.setRoom(room);
            studentRepo.save(stu);
        }

        // (Tuỳ chọn) đồng bộ currentOccupants về DB
        room.setCurrentOccupants(actual + 1);
        roomRepo.save(room);

        repo.save(rr);

        return repo.findByIdAsDto(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "cannot load registration dto"));
    }

    @Override
    @Transactional
    public RoomRegistrationResponse reject(String id) {
        RoomRegistration rr = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "registration not found"));

        rr.setStatus(RegistrationStatus.REJECTED);
        repo.save(rr);

        return repo.findByIdAsDto(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "cannot load registration dto"));
    }
}
