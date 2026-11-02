package com.studygroup.model;

import com.studygroup.model.base.AuditableEntity;
import com.studygroup.model.contract.GroupActivity;
import com.studygroup.model.enums.ActivityVerb;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages")
public class Message extends AuditableEntity implements GroupActivity {

    @Column(nullable = false, length = 1600)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private AppUser sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group;

    protected Message() {
    }

    public Message(String content, AppUser sender, StudyGroup group) {
        this.content = content;
        this.sender = sender;
        this.group = group;
    }

    @Override
    public ActivityVerb getActivityVerb() {
        return ActivityVerb.POSTED;
    }

    @Override
    public String getSummary() {
        return sender.getDisplayName() + " posted in " + group.getName();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AppUser getSender() {
        return sender;
    }

    public void setSender(AppUser sender) {
        this.sender = sender;
    }

    public StudyGroup getGroup() {
        return group;
    }

    public void setGroup(StudyGroup group) {
        this.group = group;
    }
}
