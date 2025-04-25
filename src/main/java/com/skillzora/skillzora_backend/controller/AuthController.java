package com.skillzora.skillzora_backend.controller;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GetMapping("/")
    public String home() {
        return "<h2>Welcome to SkillZora</h2><a href='/oauth2/authorization/google'>Login with Google</a>";
    }

   @GetMapping("/success")
public Map<String, Object> success(@AuthenticationPrincipal OAuth2User user) {
    Map<String, Object> userInfo = new HashMap<>();
    userInfo.put("name", user.getAttribute("name"));
    userInfo.put("email", user.getAttribute("email"));
    userInfo.put("picture", user.getAttribute("picture")); // profile image
    return userInfo;
}

}
