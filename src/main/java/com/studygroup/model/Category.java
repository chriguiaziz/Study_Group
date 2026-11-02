package com.studygroup.model;

import com.studygroup.model.base.BaseEntity;
import com.studygroup.model.contract.Displayable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category extends BaseEntity implements Displayable {

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(length = 300)
    private String description;

    @Column(length = 80)
    private String academicLevel;

    @Column(length = 120)
    private String track;

    private String accentColor = "#4f46e5";

    @OneToMany(mappedBy = "category")
    private List<StudyGroup> groups = new ArrayList<>();

    protected Category() {
    }

    public Category(String name, String description, String accentColor) {
        this.name = name;
        this.description = description;
        this.accentColor = accentColor;
    }

    public Category(String name, String academicLevel, String track, String description, String accentColor) {
        this.name = name;
        this.academicLevel = academicLevel;
        this.track = track;
        this.description = description;
        this.accentColor = accentColor;
    }

    @Override
    public String getDisplayName() {
        return name;
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

    public String getAcademicLevel() {
        return academicLevel;
    }

    public void setAcademicLevel(String academicLevel) {
        this.academicLevel = academicLevel;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(String accentColor) {
        this.accentColor = accentColor;
    }

    public List<StudyGroup> getGroups() {
        return groups;
    }
}
