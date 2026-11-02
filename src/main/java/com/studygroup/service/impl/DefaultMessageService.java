package com.studygroup.service.impl;

import com.studygroup.model.AppUser;
import com.studygroup.model.Membership;
import com.studygroup.model.Message;
import com.studygroup.model.StudyGroup;
import com.studygroup.model.enums.MembershipStatus;
import com.studygroup.model.enums.NotificationType;
import com.studygroup.repository.MembershipRepository;
import com.studygroup.repository.MessageRepository;
import com.studygroup.repository.StudyGroupRepository;
import com.studygroup.service.MessageService;
import com.studygroup.service.NotificationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DefaultMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final StudyGroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final NotificationService notificationService;

    public DefaultMessageService(MessageRepository messageRepository,
                                 StudyGroupRepository groupRepository,
                                 MembershipRepository membershipRepository,
                                 NotificationService notificationService) {
        this.messageRepository = messageRepository;
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Message sendMessage(Long groupId, AppUser sender, String content) {
        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Study group not found."));
        Message message = messageRepository.save(new Message(content, sender, group));
        membershipRepository.findByGroupIdAndStatusOrderByCreatedAtDesc(groupId, MembershipStatus.ACTIVE).stream()
                .map(Membership::getUser)
                .filter(member -> !member.getId().equals(sender.getId()))
                .forEach(member -> notificationService.notify(member, "New message", sender.getDisplayName() + " posted in " + group.getName(), NotificationType.MESSAGE, group));
        return message;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> groupMessages(Long groupId) {
        return messageRepository.findByGroupIdOrderByCreatedAtAsc(groupId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> recentForUser(Long userId) {
        return messageRepository.findRecentForUser(userId, PageRequest.of(0, 8));
    }
}
