package com.studygroup.service.impl;

import com.studygroup.dto.ResourceForm;
import com.studygroup.model.AppUser;
import com.studygroup.model.FileResource;
import com.studygroup.model.LearningResource;
import com.studygroup.model.LinkResource;
import com.studygroup.model.Membership;
import com.studygroup.model.StudyGroup;
import com.studygroup.model.enums.MembershipStatus;
import com.studygroup.model.enums.NotificationType;
import com.studygroup.model.enums.ResourceType;
import com.studygroup.repository.LearningResourceRepository;
import com.studygroup.repository.MembershipRepository;
import com.studygroup.repository.StudyGroupRepository;
import com.studygroup.service.NotificationService;
import com.studygroup.service.ResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DefaultResourceService implements ResourceService {

    private final LearningResourceRepository resourceRepository;
    private final StudyGroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final NotificationService notificationService;

    public DefaultResourceService(LearningResourceRepository resourceRepository,
                                  StudyGroupRepository groupRepository,
                                  MembershipRepository membershipRepository,
                                  NotificationService notificationService) {
        this.resourceRepository = resourceRepository;
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.notificationService = notificationService;
    }

    @Override
    public LearningResource share(Long groupId, ResourceForm form, AppUser owner) {
        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Study group not found."));
        LearningResource resource = createResource(form, owner, group);
        LearningResource saved = resourceRepository.save(resource);
        membershipRepository.findByGroupIdAndStatusOrderByCreatedAtDesc(groupId, MembershipStatus.ACTIVE).stream()
                .map(Membership::getUser)
                .filter(member -> !member.getId().equals(owner.getId()))
                .forEach(member -> notificationService.notify(member, "New resource", owner.getDisplayName() + " shared " + saved.getTitle(), NotificationType.RESOURCE, group));
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningResource> groupResources(Long groupId) {
        return resourceRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
    }

    private LearningResource createResource(ResourceForm form, AppUser owner, StudyGroup group) {
        if (form.getResourceType() == ResourceType.FILE) {
            return new FileResource(form.getTitle(), form.getDescription(), form.getFileName(), form.getStoragePath(), owner, group);
        }
        return new LinkResource(form.getTitle(), form.getDescription(), form.getUrl(), owner, group);
    }
}
