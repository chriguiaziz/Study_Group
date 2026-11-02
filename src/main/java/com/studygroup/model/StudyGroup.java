package com.studygroup.model;

import com.studygroup.model.base.AuditableEntity;
import com.studygroup.model.contract.Ownable;
import com.studygroup.model.contract.Searchable;
import com.studygroup.model.enums.GroupVisibility;
import com.studygroup.model.enums.MembershipStatus;
import com.studygroup.model.enums.StudyFormat;
import com.studygroup.model.enums.StudyRhythm;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "study_groups")
public class StudyGroup extends AuditableEntity implements Searchable, Ownable {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 1200)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyFormat format = StudyFormat.HYBRID;

    @Enumerated(EnumType.STRING)
    private StudyRhythm rhythm = StudyRhythm.WEEKLY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupVisibility visibility = GroupVisibility.PUBLIC;

    @Column(length = 120)
    private String subject;

    private int capacity = 20;
    private boolean free = true;
    private BigDecimal price = BigDecimal.ZERO;
    private String meetingLink;
    private String location;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membership> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningResource> resources = new ArrayList<>();

    protected StudyGroup() {
    }

    public StudyGroup(String name, String description, AppUser owner, Category category) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.category = category;
    }

    public int getActiveMemberCount() {
        return (int) memberships.stream()
                .filter(membership -> membership.getStatus() == MembershipStatus.ACTIVE)
                .count();
    }

    public boolean isFull() {
        return capacity > 0 && getActiveMemberCount() >= capacity;
    }

    public boolean hasMember(AppUser user) {
        return memberships.stream().anyMatch(membership -> membership.belongsTo(user) && membership.isActive());
    }

    @Override
    public boolean matches(String keyword) {
        String value = keyword == null ? "" : keyword.toLowerCase();
        return name.toLowerCase().contains(value)
                || description.toLowerCase().contains(value)
                || safe(subject).contains(value)
                || category.getName().toLowerCase().contains(value);
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    public String priceLabel() {
        return free ? "Free" : "$" + price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public StudyFormat getFormat() {
        return format;
    }

    public void setFormat(StudyFormat format) {
        this.format = format;
    }

    public StudyRhythm getRhythm() {
        return rhythm == null ? StudyRhythm.WEEKLY : rhythm;
    }

    public void setRhythm(StudyRhythm rhythm) {
        this.rhythm = rhythm;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public GroupVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(GroupVisibility visibility) {
        this.visibility = visibility;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Membership> getMemberships() {
        return memberships;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<LearningResource> getResources() {
        return resources;
    }
}
