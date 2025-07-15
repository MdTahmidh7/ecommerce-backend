package com.ecommerce.eshop.ecommerce_backend.controller;

import com.ecommerce.eshop.ecommerce_backend.entity.UserProfile;
import com.ecommerce.eshop.ecommerce_backend.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping("/{userId}")
    public ResponseEntity<UserProfile> createOrUpdateProfile(
            @PathVariable Long userId,
            @RequestParam Integer upazilaId
    ) {
        UserProfile userProfile = userProfileService.createOrUpdateProfile(userId, upazilaId);
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfile> getProfileByUserId(@PathVariable Long userId) {
        UserProfile userProfile = userProfileService.getProfileByUserId(userId);
        return ResponseEntity.ok(userProfile);
    }
}
