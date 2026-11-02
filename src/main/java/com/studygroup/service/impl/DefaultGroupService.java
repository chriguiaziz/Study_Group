package com.studygroup.service.impl;

import com.studygroup.dto.GroupForm;
import com.studygroup.model.AppUser;
import com.studygroup.model.Category;
import com.studygroup.model.Membership;
import com.studygroup.model.StudyGroup;
import com.studygroup.model.enums.MembershipRole;
import com.studygroup.model.enums.MembershipStatus;
import com.studygroup.model.enums.NotificationType;
import com.studygroup.model.enums.StudyFormat;
import com.studygroup.model.enums.StudyRhythm;
import com.studygroup.repository.CategoryRepository;
import com.studygroup.repository.MembershipRepository;
import com.studygroup.repository.StudyGroupRepository;
import com.studygroup.service.AbstractCrudService;
import com.studygroup.service.GroupService;
import com.studygroup.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class DefaultGroupService extends AbstractCrudService<StudyGroup, Long> implements GroupService {

    private final StudyGroupRepository groupRepository;
    private final CategoryRepository categoryRepository;
    private final MembershipRepository membershipRepository;
    private final NotificationService notificationService;

    public DefaultGroupService(StudyGroupRepository groupRepository,
                               CategoryRepository categoryRepository,
                               MembershipRepository membershipRepository,
                               NotificationService notificationService) {
        super(groupRepository);
        this.groupRepository = groupRepository;
        this.categoryRepository = categoryRepository;
        this.membershipRepository = membershipRepository;
        this.notificationService = notificationService;
    }

    @Override
    public StudyGroup createGroup(GroupForm form, AppUser owner) {
        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found."));
        StudyGroup group = new StudyGroup(form.getName(), form.getDescription(), owner, category);
        applyForm(group, form);
        StudyGroup saved = groupRepository.save(group);
        membershipRepository.save(new Membership(owner, saved, MembershipRole.OWNER));
        notificationService.notify(owner, "Group created", "Your group " + saved.getName() + " is ready.", NotificationType.ACTIVITY, saved);
        return saved;
    }

    @Override
    public StudyGroup updateGroup(Long groupId, GroupForm form, AppUser editor) {
        StudyGroup group = findDetailed(groupId);
        requireOwner(group, editor);
        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found."));
        group.setCategory(category);
        applyForm(group, form);
        return groupRepository.save(group);
    }

    @Override
    public void deleteGroup(Long groupId, AppUser editor) {
        StudyGroup group = findDetailed(groupId);
        requireOwner(group, editor);
        groupRepository.delete(group);
    }

    @Override
    public void joinGroup(Long groupId, AppUser user) {
        StudyGroup group = findDetailed(groupId);
        if (group.isFull()) {
            throw new IllegalStateException("This group is already full.");
        }
        Membership membership = membershipRepository.findByUserIdAndGroupId(user.getId(), groupId)
                .orElseGet(() -> new Membership(user, group, MembershipRole.MEMBER));
        membership.setStatus(MembershipStatus.ACTIVE);
        membershipRepository.save(membership);
        notificationService.notify(group.getOwner(), "New member", user.getDisplayName() + " joined " + group.getName(), NotificationType.ACTIVITY, group);
    }

    @Override
    public void leaveGroup(Long groupId, AppUser user) {
        membershipRepository.findByUserIdAndGroupId(user.getId(), groupId)
                .ifPresent(membership -> {
                    if (membership.getRole() != MembershipRole.OWNER) {
                        membership.setStatus(MembershipStatus.LEFT);
                        membershipRepository.save(membership);
                    }
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyGroup> search(String keyword, Long categoryId) {
        if (categoryId != null) {
            return groupRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId);
        }
        if (keyword != null && !keyword.isBlank()) {
            return groupRepository.search(keyword);
        }
        return groupRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyGroup> joinedGroups(Long userId) {
        return groupRepository.findJoinedGroups(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public StudyGroup findDetailed(Long groupId) {
        return groupRepository.findDetailedById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Study group not found."));
    }

    private void applyForm(StudyGroup group, GroupForm form) {
        StudyFormat format = form.getFormat() == null ? StudyFormat.HYBRID : form.getFormat();
        StudyRhythm rhythm = form.getRhythm() == null ? StudyRhythm.WEEKLY : form.getRhythm();
        group.setName(form.getName());
        group.setDescription(form.getDescription());
        group.setFormat(format);
        group.setRhythm(rhythm);
        group.setSubject(form.getSubject());
        group.setVisibility(form.getVisibility());
        group.setCapacity(form.getCapacity());
        group.setFree(form.isFree());
        group.setPrice(form.isFree() ? BigDecimal.ZERO : form.getPrice());
        group.setMeetingLink(format == StudyFormat.IN_PERSON ? null : form.getMeetingLink());
        group.setLocation(format == StudyFormat.ONLINE ? null : form.getLocation());
    }

    private void requireOwner(StudyGroup group, AppUser editor) {
        if (!group.isOwnedBy(editor)) {
            throw new IllegalArgumentException("Only the group owner can change this group.");
        }
    }
}
