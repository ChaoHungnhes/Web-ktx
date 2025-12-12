package com.example.WebKtx.dto.reportDto;

public record RoomOccupancyDto(
        String roomId,
        String roomName,
        String dormId,
        String dormName,
        Integer floor,
        long occupants,
        Integer maxOccupants
) {}
