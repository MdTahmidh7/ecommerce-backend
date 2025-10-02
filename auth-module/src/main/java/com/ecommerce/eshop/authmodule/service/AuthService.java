package com.ecommerce.eshop.authmodule.service;

import com.ecommerce.eshop.authmodule.dto.JwtResponseDTO;
import com.ecommerce.eshop.authmodule.dto.LoginRequestDTO;
import com.ecommerce.eshop.authmodule.dto.RegisterRequestDTO;
import com.ecommerce.eshop.authmodule.entity.Role;
import com.ecommerce.eshop.authmodule.entity.User;
import com.ecommerce.eshop.authmodule.model.RoleName;
import com.ecommerce.eshop.authmodule.repository.RoleRepository;
import com.ecommerce.eshop.authmodule.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public User registerUser(RegisterRequestDTO request) {

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number is already in use!");
        }

        User user = new User(request.getPhoneNumber(), passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);
        user.setAddress(request.getAddress());
        user.setDistrictName(request.getDistrictName());
        user.setUpazilaName(request.getUpazilaName());

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository
                .findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: ROLE_USER not found. Please ensure roles are initialized."));
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public JwtResponseDTO authenticateUser(LoginRequestDTO request) { // Changed param type and return type

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User userDetails = (User) authentication.getPrincipal();
        String jwt = jwtService.generateToken(userDetails);

        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponseDTO(
                jwt,
                userDetails.getId(),
                userDetails.getPhoneNumber(),
                roles,
                userDetails.getFirstName(),
                userDetails.getLastName(),
                userDetails.getAddress()
        );
    }
}
