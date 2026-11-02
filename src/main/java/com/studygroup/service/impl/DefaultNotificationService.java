package com.studygroup.service.impl;

import com.studygroup.model.AppUser;
import com.studygroup.model.Notification;
import com.studygroup.model.StudyGroup;
import com.studygroup.model.enums.NotificationType;
import com.studygroup.repository.NotificationRepository;
import com.studygroup.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DefaultNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;

    public DefaultNotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Notification notify(AppUser recipient, String title, String body) {
        return notify(recipient, title, body, NotificationType.ACTIVITY);
    }

    @Override
    public Notification notify(AppUser recipient, String title, String body, NotificationType type) {
        return notify(recipient, title, body, type, null);
    }

    @Override
    public Notification notify(AppUser recipient, String title, String body, NotificationType type, StudyGroup group) {
        Notification notification = new Notification(title, body, type, recipient);
        notification.setGroup(group);
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> forUser(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndReadStatusFalse(userId);
    }

    @Override
    public void markRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.markAsRead();
            notificationRepository.save(notification);
        });
    }
}
