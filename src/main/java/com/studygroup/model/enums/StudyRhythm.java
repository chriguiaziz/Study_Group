package com.studygroup.model.enums;

public enum StudyRhythm {
    WEEKLY("Weekly"),
    TWICE_A_WEEK("Twice a week"),
    EXAM_INTENSIVE("Exam intensive"),
    PROJECT_WORKSHOP("Project workshop"),
    WEEKEND_SESSION("Weekend session");

    private final String label;

    StudyRhythm(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
