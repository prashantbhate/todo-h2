package com.cd.controller;

import com.cd.dto.LoginRequest;
import com.cd.repository.JwtStore;
import com.cd.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtStore jwtStore;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        String username = loginRequest.getUsername();
        String jti = UUID.randomUUID().toString(); // Generate a unique ID
        jwtStore.saveJti(username, jti); // Save jti to the store

        String token = jwtService.generateJwt(loginRequest.getUsername(), Map.of("jti", jti));

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }
}