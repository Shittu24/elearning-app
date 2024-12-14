package com.ncnmo.aspire.elearning.security;

import com.ncnmo.aspire.elearning.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // List of paths that don't require authentication
        String[] excludedPaths = {"/api/authenticate", "/api/register", "/api/register-first-admin", "/oauth2/**"};
        AntPathMatcher pathMatcher = new AntPathMatcher();

        // Check if the request matches any of the excluded paths
        for (String path : excludedPaths) {
            if (pathMatcher.match(path, request.getRequestURI())) {
                logger.debug("Skipping JWT validation for public path: " + request.getRequestURI());
                chain.doFilter(request, response);  // Skip JWT validation
                return;  // Stop further processing of the filter
            }
        }

        logger.debug("Starting JWT request filter for request: {}", request.getRequestURI());

        // Retrieve the JWT token from the Authorization header or URL parameter
        String jwtToken = extractJwtFromHeaderOrUrl(request);
        String username = null;

        if (jwtToken != null) {
            try {
                // Extract username (from 'sub' claim in JWT)
                username = jwtUtil.extractUsername(jwtToken);
                logger.debug("JWT Token found, extracted username: {}", username);
            } catch (ExpiredJwtException e) {
                logger.warn("JWT Token has expired for request: {}", request.getRequestURI());
                handleUnauthorizedResponse(response, "JWT Token has expired");
                return;
            } catch (Exception e) {
                logger.warn("Unable to extract JWT Token: {}", e.getMessage());
                handleUnauthorizedResponse(response, "Invalid JWT Token");
                return;
            }
        } else {
            logger.warn("No JWT token found for request: {}", request.getRequestURI());
        }

        // Validate token if username is not null and user is not authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate the token
            if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {
                // Create authentication token and set it in the context
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                logger.debug("JWT token validated successfully, user {} authenticated", userDetails.getUsername());
                chain.doFilter(request, response);  // Proceed with the request
                return;
            } else {
                logger.warn("JWT token is invalid for user: {}", userDetails.getUsername());
                handleUnauthorizedResponse(response, "Invalid JWT Token");
                return;
            }
        }

        // Continue the filter chain
        chain.doFilter(request, response);
    }

    // Helper method to extract JWT token from the Authorization header or URL parameters
    private String extractJwtFromHeaderOrUrl(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);  // Extract the token after "Bearer "
        } else {
            // Check for JWT token in URL parameter (for OAuth2 login redirection)
            String urlToken = request.getParameter("token");
            logger.debug("Extracting token from URL: {}", urlToken);
            return urlToken;
        }
    }

    // Helper method to handle Unauthorized response
    private void handleUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(message);
    }
}
