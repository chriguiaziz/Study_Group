package com.studygroup.model;

import com.studygroup.model.base.AuditableEntity;
import com.studygroup.model.contract.GroupActivity;
import com.studygroup.model.enums.ActivityVerb;
import com.studygroup.model.enums.ResourceType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "resources")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "resource_kind")
public abstract class LearningResource extends AuditableEntity implements GroupActivity {

    @Column(nullable = false, length = 140)
    private String title;

    @Column(length = 700)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group;

    protected LearningResource() {
    }

    protected LearningResource(String title, String description, AppUser owner, StudyGroup group) {
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.group = group;
    }

    public abstract ResourceType getResourceType();

    public abstract String getAccessLabel();

    @Override
    public ActivityVerb getActivityVerb() {
        return ActivityVerb.SHARED;
    }

    @Override
    public String getSummary() {
        return owner.getDisplayName() + " shared " + title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    public StudyGroup getGroup() {
        return group;
    }

    public void setGroup(StudyGroup group) {
        this.group = group;
    }
}
