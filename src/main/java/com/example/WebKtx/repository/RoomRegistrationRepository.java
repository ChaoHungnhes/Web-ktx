package com.example.WebKtx.repository;

import com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationResponse;
import com.example.WebKtx.entity.RoomRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRegistrationRepository extends JpaRepository<RoomRegistration, String> {
    @Query("""
       select new com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationResponse(
         rr.id,
         s.id,
         concat(s.firstName, ' ', s.lastName),
         s.gender,
         s.academicYear,
         s.className,
         r.id,
         r.name,
         d.id,
         d.name,
         rr.registrationDate,
         rr.status,
         rr.requestType
       )
       from RoomRegistration rr
       join rr.student s
       join rr.room r
       join r.dormitory d
       where rr.id = :id
    """)
    Optional<RoomRegistrationResponse> findByIdAsDto(@Param("id") String id);

    @Query("""
       select new com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationResponse(
         rr.id,
         s.id,
         concat(s.firstName, ' ', s.lastName),
         s.gender,
         s.academicYear,
         s.className,
         r.id,
         r.name,
         d.id,
         d.name,
         rr.registrationDate,
         rr.status,
         rr.requestType
       )
       from RoomRegistration rr
       join rr.student s
       join rr.room r
       join r.dormitory d
    """)
    Page<RoomRegistrationResponse> findAllAsDto(Pageable pageable);
    // ✅ LIST theo studentId (không dính LAZY, không cần MapStruct)
    @Query("""
       select new com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationResponse(
         rr.id,
         s.id,
         concat(s.firstName, ' ', s.lastName),
         s.gender,
         s.academicYear,
         s.className,
         r.id,
         r.name,
         d.id,
         d.name,
         rr.registrationDate,
         rr.status,
         rr.requestType
       )
       from RoomRegistration rr
       join rr.student s
       join rr.room r
       join r.dormitory d
       where s.id = :studentId
       order by rr.registrationDate desc
    """)
    List<RoomRegistrationResponse> findAllByStudentAsDto(@Param("studentId") String studentId);

    @Query("""
       select new com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationResponse(
         rr.id,
         s.id,
         concat(s.firstName, ' ', s.lastName),
         s.gender,
         s.academicYear,
         s.className,
         r.id,
         r.name,
         d.id,
         d.name,
         rr.registrationDate,
         rr.status,
         rr.requestType
       )
       from RoomRegistration rr
       join rr.student s
       join rr.room r
       join r.dormitory d
       where rr.status = 'APPROVED'
    """)
    Page<RoomRegistrationResponse> findAllByApproved(Pageable pageable);
    @Query("""
       select new com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationResponse(
         rr.id,
         s.id,
         concat(s.firstName, ' ', s.lastName),
         s.gender,
         s.academicYear,
         s.className,
         r.id,
         r.name,
         d.id,
         d.name,
         rr.registrationDate,
         rr.status,
         rr.requestType
       )
       from RoomRegistration rr
       join rr.student s
       join rr.room r
       join r.dormitory d
       where rr.status = 'PENDING'
    """)
    Page<RoomRegistrationResponse> findAllByPending(Pageable pageable);
    @Query("""
       select new com.example.WebKtx.dto.RoomRegistrationDto.RoomRegistrationResponse(
         rr.id,
         s.id,
         concat(s.firstName, ' ', s.lastName),
         s.gender,
         s.academicYear,
         s.className,
         r.id,
         r.name,
         d.id,
         d.name,
         rr.registrationDate,
         rr.status,
         rr.requestType
       )
       from RoomRegistration rr
       join rr.student s
       join rr.room r
       join r.dormitory d
       where rr.status = 'REJECTED'
    """)
    Page<RoomRegistrationResponse> findAllByRejected(Pageable pageable);
}