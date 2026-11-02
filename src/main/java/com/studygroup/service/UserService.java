package com.studygroup.service;

import com.studygroup.dto.ProfileForm;
import com.studygroup.dto.RegisterForm;
import com.studygroup.model.AppUser;

import java.util.List;
import java.util.Optional;

public interface UserService extends CrudService<AppUser, Long> {
    AppUser register(RegisterForm form);

    Optional<AppUser> authenticate(String email, String password);

    AppUser updateProfile(Long userId, ProfileForm form);

    List<AppUser> search(String keyword);

    Optional<AppUser> findByEmail(String email);
}
