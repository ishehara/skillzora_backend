package com.skillzora.skillzora_backend.service;

import com.skillzora.skillzora_backend.dto.PasswordChangeRequest;
import com.skillzora.skillzora_backend.dto.ProfileUpdateRequest;
import com.skillzora.skillzora_backend.dto.UserDto;
import com.skillzora.skillzora_backend.exception.ResourceNotFoundException;
import com.skillzora.skillzora_backend.exception.UnauthorizedException;
import com.skillzora.skillzora_backend.model.User;
import com.skillzora.skillzora_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDto getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return convertToUserDto(user);
    }

    public UserDto getUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return convertToUserDto(user);
    }

    public UserDto updateProfile(String userId, ProfileUpdateRequest updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getBio() != null) {
            user.setBio(updateRequest.getBio());
        }
        if (updateRequest.getProfilePicture() != null) {
            user.setProfilePicture(updateRequest.getProfilePicture());
        }

        userRepository.save(user);
        return convertToUserDto(user);
    }

    public void changePassword(String userId, PasswordChangeRequest passwordRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(user);
    }

    public void followUser(String currentUserId, String targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("Users cannot follow themselves");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found with id: " + currentUserId));
        
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found with id: " + targetUserId));

        if (!currentUser.getFollowing().contains(targetUserId)) {
            currentUser.getFollowing().add(targetUserId);
            userRepository.save(currentUser);
        }

        if (!targetUser.getFollowers().contains(currentUserId)) {
            targetUser.getFollowers().add(currentUserId);
            userRepository.save(targetUser);
        }
    }

    public void unfollowUser(String currentUserId, String targetUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found with id: " + currentUserId));
        
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found with id: " + targetUserId));

        currentUser.getFollowing().remove(targetUserId);
        userRepository.save(currentUser);

        targetUser.getFollowers().remove(currentUserId);
        userRepository.save(targetUser);
    }

    public void savePost(String userId, String postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!user.getSavedPosts().contains(postId)) {
            user.getSavedPosts().add(postId);
            userRepository.save(user);
        }
    }

    public void unsavePost(String userId, String postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.getSavedPosts().remove(postId);
        userRepository.save(user);
    }

    public List<String> getSavedPosts(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return new ArrayList<>(user.getSavedPosts());
    }

    public List<UserDto> searchUsers(String query) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(query);
        return users.stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getFollowers(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return user.getFollowers().stream()
                .map(followerId -> userRepository.findById(followerId)
                        .orElseThrow(() -> new ResourceNotFoundException("Follower not found with id: " + followerId)))
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getFollowing(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return user.getFollowing().stream()
                .map(followingId -> userRepository.findById(followingId)
                        .orElseThrow(() -> new ResourceNotFoundException("Following user not found with id: " + followingId)))
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    public void deleteAccount(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Instead of actually deleting, we can deactivate the account
        user.setActive(false);
        userRepository.save(user);
    }

    private UserDto convertToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setProfilePicture(user.getProfilePicture());
        userDto.setBio(user.getBio());
        userDto.setDateJoined(user.getDateJoined());
        userDto.setFollowersCount(user.getFollowers().size());
        userDto.setFollowingCount(user.getFollowing().size());
        userDto.setRoles(user.getRoles());
        return userDto;
    }
}