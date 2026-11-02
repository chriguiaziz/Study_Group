package com.studygroup.service;

import com.studygroup.dto.ResourceForm;
import com.studygroup.model.AppUser;
import com.studygroup.model.LearningResource;

import java.util.List;

public interface ResourceService {
    LearningResource share(Long groupId, ResourceForm form, AppUser owner);

    List<LearningResource> groupResources(Long groupId);
}
