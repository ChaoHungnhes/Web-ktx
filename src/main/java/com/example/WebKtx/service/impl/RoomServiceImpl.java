package com.example.WebKtx.service.impl;

import com.example.WebKtx.common.Enum.RoomType;
import com.example.WebKtx.dto.ResultPaginationDTO;
import com.example.WebKtx.dto.RoomDto.RoomCreateRequest;
import com.example.WebKtx.dto.RoomDto.RoomResponse;
import com.example.WebKtx.dto.RoomDto.RoomUpdateRequest;
import com.example.WebKtx.entity.Room;
import com.example.WebKtx.mapper.RoomMapper;
import com.example.WebKtx.repository.DormitoryRepository;
import com.example.WebKtx.repository.RoomRepository;
import com.example.WebKtx.repository.StudentRepository;
import com.example.WebKtx.service.RoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.example.WebKtx.common.Util.PaginationUtils.wrap;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepo;
    private final DormitoryRepository dormRepo;
    private final StudentRepository studentRepo;
    private final RoomMapper mapper;
    @Override
    @Transactional
    public RoomResponse create(RoomCreateRequest req) {
        if (roomRepo.existsById(req.getId())) throw new ResponseStatusException(HttpStatus.CONFLICT, "room_id existed");
        Room entity = mapper.toEntity(req);
        entity.setDormitory(dormRepo.findById(req.getDormId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "dorm not found")));
        Room saved = roomRepo.save(entity);
        // ensure currentOccupants = 0
        saved.setCurrentOccupants(0);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RoomResponse update(String id, RoomUpdateRequest req) {
        Room entity = roomRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapper.update(entity, req);
        // re-calc current occupants from DB to keep truth
        if (entity.getCurrentOccupants() < 0 || entity.getCurrentOccupants() > entity.getMaxOccupants()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "currentOccupants out of range");
        }
        return mapper.toResponse(roomRepo.save(entity));
    }

    @Override
    public void delete(String id) {
        if (!roomRepo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        roomRepo.deleteById(id);
    }

    @Override
    public RoomResponse findById(String id) {
        Room r = roomRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        // refresh occupants for response
        r.setCurrentOccupants((int) studentRepo.countByRoomId(id));
        return mapper.toResponse(r);
    }

    @Override
    public ResultPaginationDTO getAll(Pageable pageable) {
        // tái dùng search4AsDto với tất cả filter = null => lấy toàn bộ
        Page<RoomResponse> page = roomRepo.search4AsDto(null, null, null, null, pageable);
        return wrap(page);
    }

    @Override
    public ResultPaginationDTO findByDormNamePaged(String dormName, Pageable pageable) {
        Page<RoomResponse> page = roomRepo.findByDormNamePagedAsDto(dormName, pageable);
        return wrap(page);
    }

    @Override
    public ResultPaginationDTO findByRoomTypePaged(RoomType type, Pageable pageable) {
        Page<RoomResponse> page = roomRepo.findByRoomTypePagedAsDto(type, pageable);
        return wrap(page);
    }

    @Override
    public ResultPaginationDTO findByMaxOccPaged(Integer max, Pageable pageable) {
        Page<RoomResponse> page = roomRepo.findByMaxOccPagedAsDto(max, pageable);
        return wrap(page);
    }

    @Override
    public ResultPaginationDTO findByFloorNumPaged(int floor, Pageable pageable) {
        Page<RoomResponse> page = roomRepo.findByFloorNumPagedAsDto(floor, pageable);
        return wrap(page);
    }

    @Override
    public ResultPaginationDTO search4AsDto(String dormName, RoomType type, Integer maxOccupants, Integer floor, Pageable pageable) {
        Page<RoomResponse> page = roomRepo.search4AsDto(blankToNull(dormName), type, maxOccupants, floor, pageable);
        return wrap(page);
    }

    @Override
    public ResultPaginationDTO search3AsDto(String dormName, RoomType type, Integer maxOccupants, Pageable pageable) {
        Page<RoomResponse> page = roomRepo.search4AsDto(blankToNull(dormName), type, maxOccupants, null, pageable);
        return wrap(page);
    }

    @Override
    public int getMaxFloor() {
        Integer max = roomRepo.findMaxFloor();
        return max == null ? 0 : max; // nếu chưa có phòng nào
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
