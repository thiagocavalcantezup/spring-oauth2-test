package br.com.handora.springoauth2test.controllers;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UserInfoController.BASE_URI)
public class UserInfoController {

    public final static String BASE_URI = "/user/info";

    @GetMapping("/via-authentication")
    public Map<String, Object> getUserInfo(Authentication authentication) {
        Jwt principal = (Jwt) authentication.getPrincipal();

        return Collections.singletonMap("token", principal);
    }

    @GetMapping("/via-principal")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal Jwt principal) {
        return Collections.singletonMap("token", principal);
    }

    @GetMapping("/username/via-principal")
    public Map<String, Object> getUsername(@AuthenticationPrincipal Jwt principal) {
        return Collections.singletonMap("username", principal.getClaim("preferred_username"));
    }

    @GetMapping("/username/via-claims")
    public Map<String, Object> getUsername(@AuthenticationPrincipal(expression = "claims") Map<String, Object> claims) {
        return Collections.singletonMap("username", claims.get("preferred_username"));
    }

}
