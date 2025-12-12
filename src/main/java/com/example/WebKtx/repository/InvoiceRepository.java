package com.example.WebKtx.repository;

import com.example.WebKtx.common.Enum.InvoiceStatus;
import com.example.WebKtx.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    @Query("""
        select i from Invoice i
        where i.month = :month
    """)
    Page<Invoice> findByMonth(@Param("month") LocalDate month, Pageable pageable);

    @Query("""
        select i from Invoice i
        where i.room.id = :roomId
    """)
    Page<Invoice> findByRoomId(@Param("roomId") String roomId, Pageable pageable);

    Page<Invoice> findByStatus(InvoiceStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"room", "serviceDetails", "serviceDetails.service"})
    Optional<Invoice> findFirstByRoom_IdAndStatusOrderByMonthDesc(
            String roomId,
            InvoiceStatus status
    );

    // count invoices for month
    @Query("select count(i) from Invoice i where i.month = :month")
    long countByMonth(@Param("month") LocalDate month);

    @Query("select count(i) from Invoice i where i.month = :month and i.status = :status")
    long countByMonthAndStatus(@Param("month") LocalDate month, @Param("status") InvoiceStatus status);

    @Query("select coalesce(sum(i.totalAmount), 0) from Invoice i where i.month = :month")
    BigDecimal sumTotalAmountByMonth(@Param("month") LocalDate month);

    // sum paid via payments (successful)
    @Query("""
    select coalesce(sum(p.amount), 0)
    from Payment p
    join p.invoice i
    where i.month = :month and p.status = com.example.WebKtx.common.Enum.PaymentStatus.SUCCESS
""")
    BigDecimal sumPaidAmountByMonth(@Param("month") LocalDate month);
}