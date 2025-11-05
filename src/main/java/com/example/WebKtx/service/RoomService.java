package com.example.WebKtx.service;

import com.example.WebKtx.common.Enum.RoomType;
import com.example.WebKtx.dto.ResultPaginationDTO;
import com.example.WebKtx.dto.RoomDto.RoomCreateRequest;
import com.example.WebKtx.dto.RoomDto.RoomResponse;
import com.example.WebKtx.dto.RoomDto.RoomUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomService {
    RoomResponse create(RoomCreateRequest req);
    RoomResponse update(String id, RoomUpdateRequest req);
    void delete(String id);
    RoomResponse findById(String id);
    ResultPaginationDTO getAll(Pageable pageable);


    ResultPaginationDTO findByDormNamePaged(String dormName, Pageable pageable);
    ResultPaginationDTO findByRoomTypePaged(RoomType type, Pageable pageable);
    ResultPaginationDTO findByMaxOccPaged(Integer max, Pageable pageable);
    ResultPaginationDTO findByFloorNumPaged(int floor, Pageable pageable);

    ResultPaginationDTO search4AsDto(String dormName, RoomType type, Integer maxOccupants, Integer floor, Pageable pageable);
    ResultPaginationDTO search3AsDto(String dormName, RoomType type, Integer maxOccupants, Pageable pageable);
    int getMaxFloor();
}
