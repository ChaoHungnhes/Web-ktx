package com.example.WebKtx.repository;

import com.example.WebKtx.entity.RoomService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomServiceRepository extends JpaRepository<RoomService, String> {
    Optional<RoomService> findByNameIgnoreCase(String name);
}