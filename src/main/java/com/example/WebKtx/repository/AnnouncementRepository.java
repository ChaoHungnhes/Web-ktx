package com.example.WebKtx.repository;

import com.example.WebKtx.common.Enum.Channel;
import com.example.WebKtx.common.Enum.Target;
import com.example.WebKtx.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<Announcement, String> {
    @EntityGraph(attributePaths = {"createdBy"})
    Page<Announcement> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"createdBy"})
    Optional<Announcement> findById(String id);

    // filter theo target / channel
    @EntityGraph(attributePaths = {"createdBy"})
    Page<Announcement> findByTarget(Target target, Pageable pageable);

    @EntityGraph(attributePaths = {"createdBy"})
    Page<Announcement> findByChannel(Channel channel, Pageable pageable);

    // search theo từ khóa title/summary/content
    @Query("""
        select a from Announcement a
        where (:q is null 
               or lower(a.title) like lower(concat('%', :q, '%'))
               or a.content like concat('%', :q, '%'))
          and (:target is null or a.target = :target)
          and (:channel is null or a.channel = :channel)
        """)
    Page<Announcement> search(
            @Param("q") String q,
            @Param("target") Target target,
            @Param("channel") Channel channel,
            Pageable pageable
    );
}