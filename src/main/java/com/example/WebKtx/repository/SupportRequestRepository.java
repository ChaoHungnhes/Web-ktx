package com.example.WebKtx.repository;

import com.example.WebKtx.entity.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.WebKtx.common.Enum.SupportStatus;
import com.example.WebKtx.dto.SupportRequestDto.SupportRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupportRequestRepository extends JpaRepository<SupportRequest, String> {

    @Query("""
       select new com.example.WebKtx.dto.SupportRequestDto.SupportRequestResponse(
         sr.id,
         s.id,
         concat(s.firstName, ' ', s.lastName),
         s.phone,
         sr.roomName,
         sr.status,
         sr.createdAt,
         sr.resolvedAt,
         u.id,
         u.name,
         sr.requestContent,
         sr.content,
         sr.noteExtend
       )
       from SupportRequest sr
       join sr.student s
       left join sr.handledBy u
       where sr.id = :id
    """)
    Optional<SupportRequestResponse> findByIdAsDto(@Param("id") String id);

    @Query("""
       select new com.example.WebKtx.dto.SupportRequestDto.SupportRequestResponse(
         sr.id,
         s.id,
         concat(s.firstName, ' ', s.lastName),
         s.phone,
         sr.roomName,
         sr.status,
         sr.createdAt,
         sr.resolvedAt,
         u.id,
         u.name,
         sr.requestContent,
         sr.content,
         sr.noteExtend
       )
       from SupportRequest sr
       join sr.student s
       left join sr.handledBy u
       order by sr.createdAt desc
    """)
    Page<SupportRequestResponse> findAllAsDto(Pageable pageable);

    // ====== SEARCH: theo ngày tạo (khoảng [start, end]) ======
    @Query("""
       select new com.example.WebKtx.dto.SupportRequestDto.SupportRequestResponse(
         sr.id,
         s.id,
         concat(s.firstName, ' ', s.lastName),
         s.phone,
         sr.roomName,
         sr.status,
         sr.createdAt,
         sr.resolvedAt,
         u.id,
         u.name,
         sr.requestContent,
         sr.content,
         sr.noteExtend
       )
       from SupportRequest sr
       join sr.student s
       left join sr.handledBy u
       where sr.createdAt between :start and :end
       order by sr.createdAt desc
    """)
    Page<SupportRequestResponse> findByCreatedAtBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    // ====== SEARCH: theo ngày xử lý (resolvedAt) ======
    @Query("""
       select new com.example.WebKtx.dto.SupportRequestDto.SupportRequestResponse(
         sr.id,
         s.id,
         concat(s.firstName, ' ', s.lastName),
         s.phone,
         sr.roomName,
         sr.status,
         sr.createdAt,
         sr.resolvedAt,
         u.id,
         u.name,
         sr.requestContent,
         sr.content,
         sr.noteExtend
       )
       from SupportRequest sr
       join sr.student s
       left join sr.handledBy u
       where sr.resolvedAt between :start and :end
       order by sr.resolvedAt desc
    """)
    Page<SupportRequestResponse> findByResolvedAtBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    // ====== SEARCH: theo status ======
    @Query("""
       select new com.example.WebKtx.dto.SupportRequestDto.SupportRequestResponse(
         sr.id,
         s.id,
         concat(s.firstName, ' ', s.lastName),
         s.phone,
         sr.roomName,
         sr.status,
         sr.createdAt,
         sr.resolvedAt,
         u.id,
         u.name,
         sr.requestContent,
         sr.content,
         sr.noteExtend
       )
       from SupportRequest sr
       join sr.student s
       left join sr.handledBy u
       where sr.status = :status
       order by sr.createdAt desc
    """)
    Page<SupportRequestResponse> findByStatusAsDto(
            @Param("status") SupportStatus status,
            Pageable pageable
    );

    // ====== SEARCH: theo roomName (like) ======
    @Query("""
       select new com.example.WebKtx.dto.SupportRequestDto.SupportRequestResponse(
         sr.id,
         s.id,
         concat(s.firstName, ' ', s.lastName),
         s.phone,
         sr.roomName,
         sr.status,
         sr.createdAt,
         sr.resolvedAt,
         u.id,
         u.name,
         sr.requestContent,
         sr.content,
         sr.noteExtend
       )
       from SupportRequest sr
       join sr.student s
       left join sr.handledBy u
       where (:roomName is null or lower(sr.roomName) like lower(concat('%', :roomName, '%')))
       order by sr.createdAt desc
    """)
    Page<SupportRequestResponse> findByRoomNameLike(
            @Param("roomName") String roomName,
            Pageable pageable
    );

    // ====== SEARCH: theo studentId ======
    @Query("""
       select new com.example.WebKtx.dto.SupportRequestDto.SupportRequestResponse(
         sr.id,
         s.id,
         concat(s.firstName, ' ', s.lastName),
         s.phone,
         sr.roomName,
         sr.status,
         sr.createdAt,
         sr.resolvedAt,
         u.id,
         u.name,
         sr.requestContent,
         sr.content,
         sr.noteExtend
       )
       from SupportRequest sr
       join sr.student s
       left join sr.handledBy u
       where s.id = :studentId
       order by sr.createdAt desc
    """)
    Page<SupportRequestResponse> findByStudentId(
            @Param("studentId") String studentId,
            Pageable pageable
    );

}
