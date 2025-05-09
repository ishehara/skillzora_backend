package com.skillzora.skillzora_backend.security.oauth2;

import com.skillzora.skillzora_backend.exception.OAuth2AuthenticationProcessingException;
import com.skillzora.skillzora_backend.model.User;
import com.skillzora.skillzora_backend.repository.UserRepository;
import com.skillzora.skillzora_backend.security.UserPrincipal;
import com.skillzora.skillzora_backend.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthService authService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        logger.info("Loading OAuth2 user for provider: {}", oAuth2UserRequest.getClientRegistration().getRegistrationId());

        try {
            OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
            logger.debug("OAuth2 user attributes: {}", oAuth2User.getAttributes());
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            logger.error("Authentication exception during OAuth2 processing", ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error during OAuth2 processing", ex);
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        logger.info("Processing OAuth2 user from provider: {}", registrationId);
        
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());
        
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            logger.error("Email not found from OAuth2 provider");
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        logger.info("Looking up user by email: {}", oAuth2UserInfo.getEmail());
        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        
        if (userOptional.isPresent()) {
            user = userOptional.get();
            logger.info("Existing user found with ID: {}", user.getId());
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            logger.info("No existing user found, registering new user");
            // Use AuthService to register the user
            user = authService.registerOAuthUser(
                oAuth2UserInfo.getEmail(),
                oAuth2UserInfo.getFirstName(),
                oAuth2UserInfo.getLastName(),
                oAuth2UserInfo.getImageUrl()
            );
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        logger.info("Updating existing user: {}", existingUser.getId());
        
        if (StringUtils.hasText(oAuth2UserInfo.getFirstName())) {
            existingUser.setFirstName(oAuth2UserInfo.getFirstName());
        }
        
        if (StringUtils.hasText(oAuth2UserInfo.getLastName())) {
            existingUser.setLastName(oAuth2UserInfo.getLastName());
        }
        
        if (StringUtils.hasText(oAuth2UserInfo.getImageUrl())) {
            existingUser.setProfilePicture(oAuth2UserInfo.getImageUrl());
        }
        
        User updatedUser = userRepository.save(existingUser);
        logger.info("User updated successfully: {}", updatedUser.getId());
        
        return updatedUser;
    }
}