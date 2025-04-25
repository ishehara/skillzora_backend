package com.skillzora.skillzora_backend.controller;


import com.skillzora.skillzora_backend.dto.PasswordChangeRequest;
import com.skillzora.skillzora_backend.dto.ProfileUpdateRequest;
import com.skillzora.skillzora_backend.dto.UserDto;
import com.skillzora.skillzora_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDto userDto = userService.getUserProfileByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable String username) {
        UserDto userDto = userService.getUserProfileByUsername(username);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody ProfileUpdateRequest updateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDto userDto = userService.getUserProfileByUsername(userDetails.getUsername());
        
        UserDto updatedUser = userService.updateProfile(userDto.getId(), updateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeRequest passwordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDto userDto = userService.getUserProfileByUsername(userDetails.getUsername());
        
        userService.changePassword(userDto.getId(), passwordRequest);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/follow/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> followUser(@PathVariable String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDto userDto = userService.getUserProfileByUsername(userDetails.getUsername());
        
        userService.followUser(userDto.getId(), userId);
        return ResponseEntity.ok("User followed successfully");
    }

    @PostMapping("/unfollow/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> unfollowUser(@PathVariable String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDto userDto = userService.getUserProfileByUsername(userDetails.getUsername());
        
        userService.unfollowUser(userDto.getId(), userId);
        return ResponseEntity.ok("User unfollowed successfully");
    }

    @PostMapping("/save-post/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> savePost(@PathVariable String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDto userDto = userService.getUserProfileByUsername(userDetails.getUsername());
        
        userService.savePost(userDto.getId(), postId);
        return ResponseEntity.ok("Post saved successfully");
    }

    @PostMapping("/unsave-post/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> unsavePost(@PathVariable String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDto userDto = userService.getUserProfileByUsername(userDetails.getUsername());
        
        userService.unsavePost(userDto.getId(), postId);
        return ResponseEntity.ok("Post unsaved successfully");
    }

    @GetMapping("/saved-posts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getSavedPosts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDto userDto = userService.getUserProfileByUsername(userDetails.getUsername());
        
        List<String> savedPosts = userService.getSavedPosts(userDto.getId());
        return ResponseEntity.ok(savedPosts);
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<UserDto>> getFollowers(@PathVariable String userId) {
        List<UserDto> followers = userService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<List<UserDto>> getFollowing(@PathVariable String userId) {
        List<UserDto> following = userService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String query) {
        List<UserDto> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDto userDto = userService.getUserProfileByUsername(userDetails.getUsername());
        
        userService.deleteAccount(userDto.getId());
        return ResponseEntity.ok("Account deactivated successfully");
    }
}
