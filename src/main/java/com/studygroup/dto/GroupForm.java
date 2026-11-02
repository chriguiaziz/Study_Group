package com.studygroup.dto;

import com.studygroup.model.enums.GroupVisibility;
import com.studygroup.model.enums.StudyFormat;
import com.studygroup.model.enums.StudyRhythm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class GroupForm {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Long categoryId;

    private StudyFormat format = StudyFormat.HYBRID;
    private StudyRhythm rhythm = StudyRhythm.WEEKLY;
    private GroupVisibility visibility = GroupVisibility.PUBLIC;
    private String subject;

    @Positive
    private int capacity = 20;

    private boolean free = true;
    private BigDecimal price = BigDecimal.ZERO;
    private String meetingLink;
    private String location;

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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public StudyFormat getFormat() {
        return format;
    }

    public void setFormat(StudyFormat format) {
        this.format = format;
    }

    public StudyRhythm getRhythm() {
        return rhythm;
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
}
