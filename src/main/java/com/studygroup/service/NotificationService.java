package com.studygroup.service;

import com.studygroup.model.AppUser;
import com.studygroup.model.Notification;
import com.studygroup.model.StudyGroup;
import com.studygroup.model.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    Notification notify(AppUser recipient, String title, String body);

    Notification notify(AppUser recipient, String title, String body, NotificationType type);

    Notification notify(AppUser recipient, String title, String body, NotificationType type, StudyGroup group);

    List<Notification> forUser(Long userId);

    long unreadCount(Long userId);

    void markRead(Long notificationId);
}
