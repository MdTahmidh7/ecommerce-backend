package com.ecommerce.eshop.ecommerce_backend.service.impl;

import com.ecommerce.eshop.ecommerce_backend.entity.UserProfile;
import com.ecommerce.eshop.ecommerce_backend.repository.UserProfileRepository;
import com.ecommerce.eshop.ecommerce_backend.service.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public UserProfile createOrUpdateProfile(Long userId, Integer upazilaId) {

        UserProfile profile = userProfileRepository
                .findById(userId)
                .orElse(new UserProfile());

        profile.setId(userId);
        profile.setUpazilaId(upazilaId);

        return userProfileRepository.save(profile);
    }

    @Override
    public UserProfile getProfileByUserId(Long userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User profile not found for user ID: " + userId));
    }
}
