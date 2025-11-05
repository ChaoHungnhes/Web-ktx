package com.example.WebKtx.repository;

import com.example.WebKtx.entity.Dormitory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DormitoryRepository extends JpaRepository<Dormitory, String> {
    boolean existsByNameIgnoreCase(String name);
    @Query("select d.name from Dormitory d order by d.name asc")
    List<String> findAllNames();
}