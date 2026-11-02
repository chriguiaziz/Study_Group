package com.studygroup.repository;

import com.studygroup.model.StudyGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    @EntityGraph(attributePaths = {"owner", "category", "memberships", "memberships.user"})
    @Query("select g from StudyGroup g where g.id = :id")
    Optional<StudyGroup> findDetailedById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"owner", "category", "memberships"})
    List<StudyGroup> findTop8ByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"owner", "category", "memberships"})
    List<StudyGroup> findAllByOrderByCreatedAtDesc();

    Optional<StudyGroup> findByNameIgnoreCase(String name);

    @EntityGraph(attributePaths = {"owner", "category", "memberships"})
    @Query("""
            select g from StudyGroup g
            where lower(g.name) like lower(concat('%', :keyword, '%'))
               or lower(g.description) like lower(concat('%', :keyword, '%'))
               or lower(g.subject) like lower(concat('%', :keyword, '%'))
               or lower(g.category.name) like lower(concat('%', :keyword, '%'))
            order by g.createdAt desc
            """)
    List<StudyGroup> search(@Param("keyword") String keyword);

    @EntityGraph(attributePaths = {"owner", "category", "memberships"})
    List<StudyGroup> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);

    @EntityGraph(attributePaths = {"owner", "category", "memberships"})
    @Query("""
            select g from StudyGroup g
            join g.memberships m
            where m.user.id = :userId and m.status = com.studygroup.model.enums.MembershipStatus.ACTIVE
            order by g.createdAt desc
            """)
    List<StudyGroup> findJoinedGroups(@Param("userId") Long userId);
}
