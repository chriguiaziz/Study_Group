package com.studygroup.repository;

import com.studygroup.model.LearningResource;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {

    @EntityGraph(attributePaths = {"owner", "group"})
    List<LearningResource> findByGroupIdOrderByCreatedAtDesc(Long groupId);
}
