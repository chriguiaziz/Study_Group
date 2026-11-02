package com.studygroup.repository;

import com.studygroup.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @EntityGraph(attributePaths = {"sender"})
    List<Message> findByGroupIdOrderByCreatedAtAsc(Long groupId);

    @EntityGraph(attributePaths = {"sender", "group"})
    @Query("""
            select distinct m from Message m
            join m.group g
            join g.memberships ms
            where ms.user.id = :userId and ms.status = com.studygroup.model.enums.MembershipStatus.ACTIVE
            order by m.createdAt desc
            """)
    List<Message> findRecentForUser(@Param("userId") Long userId, Pageable pageable);
}
