package com.studygroup.model.contract;

import com.studygroup.model.AppUser;

public interface Ownable {
    AppUser getOwner();

    default boolean isOwnedBy(AppUser user) {
        return user != null && getOwner() != null && getOwner().getId().equals(user.getId());
    }
}
