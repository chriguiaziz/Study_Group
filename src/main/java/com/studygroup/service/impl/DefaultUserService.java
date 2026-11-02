package com.studygroup.service.impl;

import com.studygroup.dto.ProfileForm;
import com.studygroup.dto.RegisterForm;
import com.studygroup.model.AppUser;
import com.studygroup.repository.AppUserRepository;
import com.studygroup.service.AbstractCrudService;
import com.studygroup.service.UserService;
import com.studygroup.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DefaultUserService extends AbstractCrudService<AppUser, Long> implements UserService {

    private final AppUserRepository userRepository;

    public DefaultUserService(AppUserRepository userRepository) {
        super(userRepository);
        this.userRepository = userRepository;
    }

    @Override
    public AppUser register(RegisterForm form) {
        if (userRepository.existsByEmailIgnoreCase(form.getEmail())) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }
        AppUser user = new AppUser(form.getFullName(), form.getEmail(), PasswordUtil.hash(form.getPassword()));
        user.setUniversity(form.getUniversity());
        user.setMajor(form.getMajor());
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppUser> authenticate(String email, String password) {
        return userRepository.findByEmailIgnoreCase(email)
                .filter(user -> PasswordUtil.matches(password, user.getPasswordHash()));
    }

    @Override
    public AppUser updateProfile(Long userId, ProfileForm form) {
        AppUser user = findById(userId);
        user.updateProfile(form.getFullName(), form.getUniversity(), form.getMajor(), form.getBio(), form.getAvatarUrl());
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppUser> search(String keyword) {
        String value = keyword == null ? "" : keyword;
        return userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrMajorContainingIgnoreCase(
                value, value, value);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppUser> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }
}
