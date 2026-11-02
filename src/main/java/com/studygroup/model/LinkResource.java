package com.studygroup.model;

import com.studygroup.model.enums.ResourceType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("LINK")
public class LinkResource extends LearningResource {

    @Column(length = 600)
    private String url;

    protected LinkResource() {
    }

    public LinkResource(String title, String description, String url, AppUser owner, StudyGroup group) {
        super(title, description, owner, group);
        this.url = url;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.LINK;
    }

    @Override
    public String getAccessLabel() {
        return url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
