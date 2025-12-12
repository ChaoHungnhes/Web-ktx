package com.example.WebKtx.service.impl;

import com.example.WebKtx.common.Enum.RegistrationStatus;
import com.example.WebKtx.common.Enum.RequestType;
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

import java.time.LocalDate;
import java.util.List;

import static com.example.WebKtx.common.Enum.RequestType.*;
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

        RequestType type = req.getRequestType() != null
                ? req.getRequestType()
                : RequestType.REGISTER;  // giống @PrePersist

        // 👉 Option B: validate nhẹ
        switch (type) {
            case REGISTER -> {
                if (student.getRoom() != null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Student already has a room, use TRANSFER instead");
                }
            }
            case TRANSFER -> {
                if (student.getRoom() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Student currently has no room, cannot TRANSFER");
                }
                if (student.getRoom().getId().equals(room.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "New room must be different from current room");
                }
            }
            case CHECKOUT -> {
                if (student.getRoom() == null
                        || !student.getRoom().getId().equals(room.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Student is not currently in this room, cannot CHECKOUT");
                }
            }
        }

        RoomRegistration entity = mapper.toEntity(req);
        entity.setStudent(student);
        entity.setRoom(room);
        entity.setRequestType(type); // để chắc ăn, không cần chờ PrePersist

        RoomRegistration saved = repo.save(entity);

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

        Student stu = rr.getStudent();
        Room targetRoom = rr.getRoom();
        if (targetRoom == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "room not set for registration");
        }

        // đảm bảo lấy room mới nhất từ DB
        targetRoom = roomRepo.findById(targetRoom.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "room not found"));

        // Đếm số người hiện tại trong phòng mới
        int actualNew = (int) roomRepo.countStudentsInRoom(targetRoom.getId());
        int max = targetRoom.getMaxOccupants() != null ? targetRoom.getMaxOccupants() : 0;

        RequestType type = rr.getRequestType();

        switch (type) {
            case REGISTER -> {
                // student chưa có phòng hoặc đang null
                if (stu.getRoom() != null && !stu.getRoom().getId().equals(targetRoom.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Student already has a room, use TRANSFER instead");
                }

                if (actualNew >= max) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "room is full");
                }

                stu.setRoom(targetRoom);
                targetRoom.setCurrentOccupants(actualNew + 1);
                studentRepo.save(stu);
                roomRepo.save(targetRoom);
            }

            case TRANSFER -> {
                Room oldRoom = stu.getRoom();
                if (oldRoom == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student currently has no room");
                }
                if (oldRoom.getId().equals(targetRoom.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Old room and new room are the same");
                }

                // Đếm lại phòng mới
                if (actualNew >= max) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "new room is full");
                }

                // Giảm số người ở phòng cũ
                oldRoom = roomRepo.findById(oldRoom.getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "old room not found"));
                int actualOld = (int) roomRepo.countStudentsInRoom(oldRoom.getId());
                if (actualOld > 0) oldRoom.setCurrentOccupants(actualOld - 1);

                // Tăng số người phòng mới
                targetRoom.setCurrentOccupants(actualNew + 1);

                // Gán phòng mới cho student
                stu.setRoom(targetRoom);

                roomRepo.save(oldRoom);
                roomRepo.save(targetRoom);
                studentRepo.save(stu);
            }

            case CHECKOUT -> {
                Room currentRoom = stu.getRoom();
                if (currentRoom == null || !currentRoom.getId().equals(targetRoom.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Student is not currently in this room");
                }

                // Giảm số người ở phòng này
                int actual = (int) roomRepo.countStudentsInRoom(targetRoom.getId());
                if (actual > 0) targetRoom.setCurrentOccupants(actual - 1);

                // Bỏ gán phòng cho student
                stu.setRoom(null);

                roomRepo.save(targetRoom);
                studentRepo.save(stu);
            }
        }

        // Cuối cùng: set APPROVED cho đơn
        rr.setStatus(RegistrationStatus.APPROVED);
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

    @Override
    public ResultPaginationDTO searchByDate(LocalDate date, Pageable pageable) {
        Page<RoomRegistrationResponse> page = repo.findAllByRegistrationDate(date, pageable);
        return wrap(page);
    }

    @Override
    public ResultPaginationDTO findTransferByStudent(String studentId, Pageable pageable) {
        if (!studentRepo.existsById(studentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found");
        }
        Page<RoomRegistrationResponse> page = repo.findAllTransferByStudent(studentId, pageable);
        return wrap(page);
    }

    @Override
    public ResultPaginationDTO findCheckoutByStudent(String studentId, Pageable pageable) {
        if (!studentRepo.existsById(studentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found");
        }
        Page<RoomRegistrationResponse> page = repo.findAllCheckoutByStudent(studentId, pageable);
        return wrap(page);
    }

}
