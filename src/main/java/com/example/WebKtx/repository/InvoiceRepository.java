package com.example.WebKtx.repository;

import com.example.WebKtx.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    @Query("""
        select i from Invoice i
        where i.room.id = :roomId and i.month = :month
    """)
    Optional<Invoice> findByRoomAndMonth(@Param("roomId") String roomId,
                                         @Param("month") LocalDate month);

    @EntityGraph(attributePaths = {"room"})
    Page<Invoice> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"room", "serviceDetails", "serviceDetails.service"})
    Optional<Invoice> findWithDetailsById(String id);
}