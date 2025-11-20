package com.example.WebKtx.repository;

import com.example.WebKtx.common.Enum.ActiveEnum;
import com.example.WebKtx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findByActive(ActiveEnum active);
}