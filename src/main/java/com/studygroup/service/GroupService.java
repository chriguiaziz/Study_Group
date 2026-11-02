package com.studygroup.service;

import com.studygroup.dto.GroupForm;
import com.studygroup.model.AppUser;
import com.studygroup.model.StudyGroup;

import java.util.List;

public interface GroupService extends CrudService<StudyGroup, Long> {
    StudyGroup createGroup(GroupForm form, AppUser owner);

    StudyGroup updateGroup(Long groupId, GroupForm form, AppUser editor);

    void deleteGroup(Long groupId, AppUser editor);

    void joinGroup(Long groupId, AppUser user);

    void leaveGroup(Long groupId, AppUser user);

    List<StudyGroup> search(String keyword, Long categoryId);

    List<StudyGroup> joinedGroups(Long userId);

    StudyGroup findDetailed(Long groupId);
}
