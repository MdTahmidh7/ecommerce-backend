package com.ecommerce.eshop.ecommerce_backend.service;

import com.ecommerce.eshop.ecommerce_backend.entity.UserProfile;

public interface UserProfileService {

    UserProfile createOrUpdateProfile(Long userId, Integer upazilaId);

    UserProfile getProfileByUserId(Long userId);

}
