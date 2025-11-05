package com.example.WebKtx.repository;

import com.example.WebKtx.common.Enum.RoleEnum;
import com.example.WebKtx.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}