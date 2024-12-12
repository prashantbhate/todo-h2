package com.cd.handlers;

import com.cd.repository.JwtStore;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtLogoutHandler implements LogoutHandler, LogoutSuccessHandler {

    @Autowired
    private JwtStore jwtStore;

    public JwtLogoutHandler(JwtStore jwtStore) {
        this.jwtStore = jwtStore;
    }


    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            Claims claims = (Claims) authentication.getDetails();
            jwtStore.invalidateJti(claims.getId());
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        String jsonResponse = "{\"message\":\"Logout successful\",\"status\":\"success\"}";
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
