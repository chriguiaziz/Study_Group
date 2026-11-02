package com.studygroup.model.contract;

import com.studygroup.model.enums.ActivityVerb;

public interface GroupActivity {
    ActivityVerb getActivityVerb();

    String getSummary();
}
