package com.studygroup.repository;

import com.studygroup.model.Notification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @EntityGraph(attributePaths = {"group"})
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    long countByRecipientIdAndReadStatusFalse(Long recipientId);

    @Transactional
    @Modifying
    @Query("delete from Notification n where n.group.id = :groupId")
    void deleteByGroupId(@Param("groupId") Long groupId);
}
