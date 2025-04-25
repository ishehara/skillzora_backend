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

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

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
        User user = new User(
                signupRequest.getFirstName(),
                signupRequest.getLastName(),
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword())
        );

        // Set default role to "ROLE_USER"
        user.setRoles(new ArrayList<>(Collections.singletonList("ROLE_USER")));
        
        userRepository.save(user);
    }
}