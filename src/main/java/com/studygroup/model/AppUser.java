package com.studygroup.model;

import com.studygroup.model.base.ProfiledUser;
import com.studygroup.model.contract.Searchable;
import com.studygroup.model.enums.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class AppUser extends ProfiledUser implements Searchable {

    @Column(nullable = false)
    private String passwordHash;

    private String university;
    private String major;

    @Column(length = 800)
    private String bio;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.STUDENT;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membership> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    protected AppUser() {
    }

    public AppUser(String fullName, String email, String passwordHash) {
        super(fullName, email);
        this.passwordHash = passwordHash;
    }

    public void updateProfile(String fullName, String university, String major, String bio) {
        setFullName(fullName);
        this.university = university;
        this.major = major;
        this.bio = bio;
    }

    public void updateProfile(String fullName, String university, String major, String bio, String avatarUrl) {
        updateProfile(fullName, university, major, bio);
        this.avatarUrl = avatarUrl;
    }

    @Override
    public boolean matches(String keyword) {
        String value = keyword == null ? "" : keyword.toLowerCase();
        return getDisplayName().toLowerCase().contains(value)
                || getEmail().toLowerCase().contains(value)
                || safe(major).contains(value)
                || safe(university).contains(value);
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<Membership> getMemberships() {
        return memberships;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }
}
