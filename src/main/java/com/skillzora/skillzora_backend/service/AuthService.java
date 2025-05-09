package com.skillzora.skillzora_backend.service;

import com.skillzora.skillzora_backend.dto.JwtResponse;
import com.skillzora.skillzora_backend.dto.LoginRequest;
import com.skillzora.skillzora_backend.dto.SignupRequest;
import com.skillzora.skillzora_backend.exception.ResourceAlreadyExistsException;
import com.skillzora.skillzora_backend.model.User;
import com.skillzora.skillzora_backend.repository.UserRepository;
import com.skillzora.skillzora_backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + userDetails.getUsername()));

        return new JwtResponse(
                jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
        );
    }

    public void registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Email is already in use!");
        }

        // Create new user account
        User user = new User();
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setDateJoined(new Date());
        
        // Set default role to "ROLE_USER"
        user.setRoles(new ArrayList<>(Collections.singletonList("ROLE_USER")));
        
        // Initialize collections
        user.setFollowing(new ArrayList<>());
        user.setFollowers(new ArrayList<>());
        user.setSavedPosts(new ArrayList<>());
        
        // Set account status
        user.setActive(true);
        
        userRepository.save(user);
    }
    
    // Method to handle user registration via OAuth
    public User registerOAuthUser(String email, String firstName, String lastName, String picture) {
        if (userRepository.existsByEmail(email)) {
            return userRepository.findByEmail(email).orElseThrow();
        }
        
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName != null ? firstName : "");
        user.setLastName(lastName != null ? lastName : "");
        
        // Generate a username based on email
        String baseUsername = email.split("@")[0];
        user.setUsername(baseUsername + "_" + System.currentTimeMillis() % 10000);
        
        // Set a random password - OAuth users won't use password login
        user.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
        
        // Set profile picture if provided
        if (picture != null && !picture.isEmpty()) {
            user.setProfilePicture(picture);
        }
        
        user.setDateJoined(new Date());
        user.setRoles(new ArrayList<>(Collections.singletonList("ROLE_USER")));
        user.setFollowing(new ArrayList<>());
        user.setFollowers(new ArrayList<>());
        user.setSavedPosts(new ArrayList<>());
        user.setActive(true);
        
        return userRepository.save(user);
    }
}