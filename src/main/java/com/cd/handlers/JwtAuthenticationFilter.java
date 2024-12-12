package com.cd.handlers;

import com.cd.repository.JwtStore;
import com.cd.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtStore jwtStore;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "UNAUTHORIZED", "No authentication token provided");
            return;
        }
        if (!authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "INVALID_AUTH_HEADER", "Authorization header must start with 'Bearer '");
            return;
        }

        String token = authHeader.substring(7);

        Claims claims;
        try {
            claims = jwtService.validateJwt(token);
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "TOKEN_EXPIRED", "JWT token has expired");
            return;
        } catch (JwtException e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN,
                    "INVALID_TOKEN", "Invalid JWT token");
            return;
        }
        String username = claims.getSubject();
        String jti = claims.getId();

        if (!jwtStore.isJtiValid(username, jti)) {
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN,
                    "TOKEN_REVOKED", "Token has been revoked or invalidated");
            return;
        }

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN,
                    "USER_NOT_FOUND", "User associated with token no longer exists");
            return;
        }

        // Successful authentication
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(claims);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);

    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode,
                                   String errorCode, String errorMessage) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Create a consistent error response JSON
        String jsonErrorResponse = String.format(
                "{\"error\":\"%s\",\"code\":\"%s\",\"message\":\"%s\"}",
                errorMessage, errorCode, errorMessage
        );

        response.getWriter().write(jsonErrorResponse);
        response.getWriter().flush();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/login");
    }
}
