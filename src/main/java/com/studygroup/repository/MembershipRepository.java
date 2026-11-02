package com.studygroup.repository;

import com.studygroup.model.Membership;
import com.studygroup.model.enums.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Optional<Membership> findByUserIdAndGroupId(Long userId, Long groupId);

    List<Membership> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, MembershipStatus status);

    List<Membership> findByGroupIdAndStatusOrderByCreatedAtDesc(Long groupId, MembershipStatus status);
}
