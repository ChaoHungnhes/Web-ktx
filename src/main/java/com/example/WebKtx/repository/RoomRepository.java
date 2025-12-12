package com.example.WebKtx.repository;

import com.example.WebKtx.common.Enum.RoomType;
import com.example.WebKtx.dto.RoomDto.RoomIdNameDormResponse;
import com.example.WebKtx.dto.RoomDto.RoomResponse;
import com.example.WebKtx.dto.reportDto.DebtRoomDto;
import com.example.WebKtx.dto.reportDto.RoomOccupancyDto;
import com.example.WebKtx.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // ✅ đúng import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    @Query("select count(s) from Student s where s.room.id = :roomId")
    long countStudentsInRoom(@Param("roomId") String roomId);

    // 1) Theo tên ký túc (dorm)
    @Query("""
           select new com.example.WebKtx.dto.RoomDto.RoomResponse(
               r.id, r.name, r.type, r.currentOccupants, r.maxOccupants, r.price, r.floor,
               d.id, d.name
           )
           from Room r join r.dormitory d
           where lower(d.name) = lower(:dormName)
           """)
    Page<RoomResponse> findByDormNamePagedAsDto(@Param("dormName") String dormName, Pageable pageable);

    // 2) Theo loại phòng
    @Query("""
           select new com.example.WebKtx.dto.RoomDto.RoomResponse(
               r.id, r.name, r.type, r.currentOccupants, r.maxOccupants, r.price, r.floor,
               d.id, d.name
           )
           from Room r join r.dormitory d
           where r.type = :type
           """)
    Page<RoomResponse> findByRoomTypePagedAsDto(@Param("type") RoomType type, Pageable pageable);

    // 3) Theo sức chứa tối đa
    @Query("""
           select new com.example.WebKtx.dto.RoomDto.RoomResponse(
               r.id, r.name, r.type, r.currentOccupants, r.maxOccupants, r.price, r.floor,
               d.id, d.name
           )
           from Room r join r.dormitory d
           where r.maxOccupants = :max
           """)
    Page<RoomResponse> findByMaxOccPagedAsDto(@Param("max") Integer max, Pageable pageable);

    // 4) Theo tầng
    @Query("""
           select new com.example.WebKtx.dto.RoomDto.RoomResponse(
               r.id, r.name, r.type, r.currentOccupants, r.maxOccupants, r.price, r.floor,
               d.id, d.name
           )
           from Room r join r.dormitory d
           where r.floor = :floor
           """)
    Page<RoomResponse> findByFloorNumPagedAsDto(@Param("floor") int floor, Pageable pageable);

    // =========================
    // 3 tiêu chí (List)
    // =========================
    @Query("""
           select r from Room r join r.dormitory d
           where lower(d.name) = lower(:dormName)
             and r.type = :type
             and r.maxOccupants = :maxOccupants
           """)
    List<Room> findByAll3(@Param("dormName") String dormName,
                          @Param("type") RoomType type,
                          @Param("maxOccupants") Integer maxOccupants);

    // =========================
    // 3 tiêu chí + phân trang (Page<Room>)
    // =========================
    @Query("""
           select r from Room r join r.dormitory d
           where (:dormName is null or lower(d.name) = lower(:dormName))
             and (:type is null or r.type = :type)
             and (:maxOccupants is null or r.maxOccupants = :maxOccupants)
           """)
    Page<Room> search3(@Param("dormName") String dormName,
                       @Param("type") RoomType type,
                       @Param("maxOccupants") Integer maxOccupants,
                       Pageable pageable);

    // =========================
    // 4 tiêu chí (List)
    // =========================
    @Query("""
           select r from Room r join r.dormitory d
           where lower(d.name) = lower(:dormName)
             and r.type = :type
             and r.maxOccupants = :maxOccupants
             and r.floor = :floor
           """)
    List<Room> findByAll4(@Param("dormName") String dormName,
                          @Param("type") RoomType type,
                          @Param("maxOccupants") Integer maxOccupants,
                          @Param("floor") int floor);

    // =========================
    // 4 tiêu chí + phân trang (Page<Room>)
    // =========================
    @Query("""
           select r from Room r join r.dormitory d
           where (:dormName is null or lower(d.name) = lower(:dormName))
             and (:type is null or r.type = :type)
             and (:maxOccupants is null or r.maxOccupants = :maxOccupants)
             and (:floor is null or r.floor = :floor)
           """)
    Page<Room> search4(@Param("dormName") String dormName,
                       @Param("type") RoomType type,
                       @Param("maxOccupants") Integer maxOccupants,
                       @Param("floor") Integer floor,
                       Pageable pageable);

    // =========================
    // DTO Projection (Page<RoomResponse>)
    // =========================
    @Query("""
           select new com.example.WebKtx.dto.RoomDto.RoomResponse(
               r.id, r.name, r.type, r.currentOccupants, r.maxOccupants, r.price, r.floor,
               d.id, d.name
           )
           from Room r join r.dormitory d
           where (:dormName is null or lower(d.name) = lower(:dormName))
             and (:type is null or r.type = :type)
             and (:maxOccupants is null or r.maxOccupants = :maxOccupants)
             and (:floor is null or r.floor = :floor)
           """)
    Page<RoomResponse> search4AsDto(@Param("dormName") String dormName,
                                    @Param("type") RoomType type,
                                    @Param("maxOccupants") Integer maxOccupants,
                                    @Param("floor") Integer floor,
                                    Pageable pageable);

    // =========================
    // DTO Projection (List<RoomResponse>) - nếu cần bản không phân trang
    // =========================
    @Query("""
           select new com.example.WebKtx.dto.RoomDto.RoomResponse(
               r.id, r.name, r.type, r.currentOccupants, r.maxOccupants, r.price, r.floor,
               d.id, d.name
           )
           from Room r join r.dormitory d
           where lower(d.name) = lower(:dormName)
             and r.type = :type
             and r.maxOccupants = :maxOccupants
           """)
    List<RoomResponse> findAll3AsDto(@Param("dormName") String dormName,
                                     @Param("type") RoomType type,
                                     @Param("maxOccupants") Integer maxOccupants);
    @Query("select max(r.floor) from Room r")
    Integer findMaxFloor();

    @Query("""
       select new com.example.WebKtx.dto.RoomDto.RoomIdNameDormResponse(
            r.id,
            r.name,
            d.id,
            d.name
       )
       from Room r
       join r.dormitory d
       order by d.name asc, r.name asc
       """)
    List<RoomIdNameDormResponse> findAllIdNameAndDorm();

    // 1) Debt rooms for a given month (room + invoice left join)
    @Query("""
        select new com.example.WebKtx.dto.reportDto.DebtRoomDto(
            r.id,
            r.name,
            d.id,
            d.name,
            r.floor,
            (select count(s2) from Student s2 where s2.room.id = r.id),
            i.totalAmount,
            coalesce((select sum(p.amount) from Payment p where p.invoice.id = i.id and p.status = com.example.WebKtx.common.Enum.PaymentStatus.SUCCESS), 0),
            case when i.totalAmount is null then null
                 else i.totalAmount - coalesce((select sum(p2.amount) from Payment p2 where p2.invoice.id = i.id and p2.status = com.example.WebKtx.common.Enum.PaymentStatus.SUCCESS), 0)
            end,
            i.id,
            case when i.status is null then null else i.status end
        )
        from Room r
        join r.dormitory d
        left join r.invoices i with i.month = :month
        where (i is null) or (i.status = com.example.WebKtx.common.Enum.InvoiceStatus.UNPAID)
        order by d.name, r.name
    """)
    List<DebtRoomDto> findDebtRoomsByMonth(@Param("month") LocalDate month);

    // 2) occupancy per room (counts students)
    @Query("""
        select new com.example.WebKtx.dto.reportDto.RoomOccupancyDto(
            r.id, r.name, d.id, d.name, r.floor,
            count(s.id), r.maxOccupants
        )
        from Room r
        join r.dormitory d
        left join r.students s
        group by r.id, r.name, d.id, d.name, r.floor, r.maxOccupants
        order by d.name, r.floor, r.name
    """)
    List<RoomOccupancyDto> findAllRoomOccupancy();

    // option: occupancy by dorm
    @Query("""
        select new com.example.WebKtx.dto.reportDto.RoomOccupancyDto(
            r.id, r.name, d.id, d.name, r.floor,
            count(s.id), r.maxOccupants
        )
        from Room r
        join r.dormitory d
        left join r.students s
        where d.id = :dormId
        group by r.id, r.name, d.id, d.name, r.floor, r.maxOccupants
        order by r.floor, r.name
    """)
    List<RoomOccupancyDto> findRoomOccupancyByDorm(@Param("dormId") String dormId);

}
