package com.ecommerce.eshop.authmodule.service;

import com.ecommerce.eshop.authmodule.entity.User;
import com.ecommerce.eshop.authmodule.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // Make this a Spring-managed service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        // Here, phoneNumber is treated as the username for authentication
        User user = userRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + phoneNumber));

        // The 'User' model itself should implement UserDetails for Spring Security
        // If your User model already implements UserDetails, you can return 'user' directly.
        // Otherwise, you might convert it to Spring Security's User object:
        // return new org.springframework.security.core.userdetails.User(
        //     user.getPhoneNumber(),
        //     user.getPassword(),
        //     user.getAuthorities() // Assuming getAuthorities() is implemented in your User model
        // );

        // Assuming your 'com.ecommerce.eshop.authmodule.model.User' class already implements UserDetails
        return user;
    }
}
