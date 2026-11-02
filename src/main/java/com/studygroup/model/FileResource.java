package com.studygroup.model;

import com.studygroup.model.enums.ResourceType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FILE")
public class FileResource extends LearningResource {

    private String fileName;
    private String storagePath;

    protected FileResource() {
    }

    public FileResource(String title, String description, String fileName, String storagePath, AppUser owner, StudyGroup group) {
        super(title, description, owner, group);
        this.fileName = fileName;
        this.storagePath = storagePath;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.FILE;
    }

    @Override
    public String getAccessLabel() {
        return fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }
}
