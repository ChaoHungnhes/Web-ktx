package com.example.WebKtx.controller;

import com.example.WebKtx.anotation.ApiMessage;
import com.example.WebKtx.common.Enum.RoomType;
import com.example.WebKtx.dto.ResultPaginationDTO;
import com.example.WebKtx.dto.RoomDto.RoomCreateRequest;
import com.example.WebKtx.dto.RoomDto.RoomResponse;
import com.example.WebKtx.dto.RoomDto.RoomUpdateRequest;
import com.example.WebKtx.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webktx/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody RoomCreateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody RoomUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        service.delete(id); return ResponseEntity.ok("Delete complete");
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) { return ResponseEntity.ok(service.findById(id)); }
    @GetMapping
    @ApiMessage("Get rooms (paged) success")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.getAll(PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/by-dorm")
    @ApiMessage("Get rooms by dorm success")
    public ResponseEntity<?> findByDormNamePaged(
            @RequestParam String dormName,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.findByDormNamePaged(dormName, PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/by-type")
    @ApiMessage("Get rooms by type success")
    public ResponseEntity<?> findByRoomTypePaged(
            @RequestParam RoomType type,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.findByRoomTypePaged(type, PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/by-max")
    @ApiMessage("Get rooms by max occupants success")
    public ResponseEntity<?> findByMaxOccPaged(
            @RequestParam Integer max,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.findByMaxOccPaged(max, PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/by-floor")
    @ApiMessage("Get rooms by floor success")
    public ResponseEntity<?> findByFloorNumPaged(
            @RequestParam int floor,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.findByFloorNumPaged(floor, PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/_search")
    @ApiMessage("Search rooms success")
    public ResponseEntity<?> search4(
            @RequestParam(required = false) String dormName,
            @RequestParam(required = false) RoomType type,
            @RequestParam(required = false) Integer maxOccupants,
            @RequestParam(required = false) Integer floor,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.search4AsDto(dormName, type, maxOccupants, floor, PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/_search3")
    @ApiMessage("Search rooms (3 fields) success")
    public ResponseEntity<?> search3(
            @RequestParam(required = false) String dormName,
            @RequestParam(required = false) RoomType type,
            @RequestParam(required = false) Integer maxOccupants,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ResultPaginationDTO dto = service.search3AsDto(dormName, type, maxOccupants, PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/max-floor")
    @ApiMessage("Get global max floor success")
    public ResponseEntity<?> getMaxFloor() {
        return ResponseEntity.ok(service.getMaxFloor());
    }
}

