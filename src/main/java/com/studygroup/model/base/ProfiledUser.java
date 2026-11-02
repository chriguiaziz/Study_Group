package com.studygroup.model.base;

import com.studygroup.model.contract.Displayable;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ProfiledUser extends AuditableEntity implements Displayable {

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    protected ProfiledUser() {
    }

    protected ProfiledUser(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    @Override
    public String getDisplayName() {
        return fullName == null || fullName.isBlank() ? email : fullName;
    }

    public String getInitials() {
        if (fullName == null || fullName.isBlank()) {
            return "?";
        }
        String[] parts = fullName.trim().split("\\s+");
        String first = parts[0].substring(0, 1);
        String second = parts.length > 1 ? parts[parts.length - 1].substring(0, 1) : "";
        return (first + second).toUpperCase();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
