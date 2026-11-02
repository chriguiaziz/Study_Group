package com.studygroup.model;

import com.studygroup.model.base.AuditableEntity;
import com.studygroup.model.enums.MembershipRole;
import com.studygroup.model.enums.MembershipStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "memberships")
public class Membership extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group;

    @Enumerated(EnumType.STRING)
    private MembershipRole role = MembershipRole.MEMBER;

    @Enumerated(EnumType.STRING)
    private MembershipStatus status = MembershipStatus.ACTIVE;

    protected Membership() {
    }

    public Membership(AppUser user, StudyGroup group, MembershipRole role) {
        this.user = user;
        this.group = group;
        this.role = role;
    }

    public boolean belongsTo(AppUser candidate) {
        return candidate != null && user != null && user.getId().equals(candidate.getId());
    }

    public boolean isActive() {
        return status == MembershipStatus.ACTIVE;
    }

    public boolean canModerate() {
        return role == MembershipRole.OWNER || role == MembershipRole.MODERATOR;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public StudyGroup getGroup() {
        return group;
    }

    public void setGroup(StudyGroup group) {
        this.group = group;
    }

    public MembershipRole getRole() {
        return role;
    }

    public void setRole(MembershipRole role) {
        this.role = role;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }
}
