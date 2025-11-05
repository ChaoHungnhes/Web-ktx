package com.example.WebKtx.repository;

import com.example.WebKtx.dto.StudentDto.StudentInRoomResponse;
import com.example.WebKtx.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    boolean existsByIdentityNumber(String identityNumber);
    @Query("select count(s) from Student s where s.room.id = :roomId")
    long countByRoomId(@Param("roomId") String roomId);
    @Query("select s.id from Student s where s.user.id = :userId")
    Optional<String> findStudentIdByUserId(@Param("userId") String userId);
    @Query("""
       select new com.example.WebKtx.dto.StudentDto.StudentInRoomResponse(
         s.id,
         concat(s.firstName, ' ', s.lastName),
         s.gender,
         s.academicYear,
         s.className,
         s.phone
       )
       from Student s
       where s.room.id = :roomId
       order by s.lastName asc, s.firstName asc
    """)
    List<StudentInRoomResponse> findByRoomIdAsDto(@Param("roomId") String roomId);
}