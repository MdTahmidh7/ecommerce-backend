package com.ecommerce.eshop.authmodule.filter;



import com.ecommerce.eshop.authmodule.service.CustomUserDetailsService; // Correct import for your service
import com.ecommerce.eshop.authmodule.service.JwtService;             // Correct import for your service
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Make this a Spring-managed component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userPhoneNumber;

        // 1. Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Continue to the next filter
            return;
        }

        // 2. Extract JWT token
        jwt = authHeader.substring(7); // "Bearer ".length() == 7
        userPhoneNumber = jwtService.extractPhoneNumber(jwt);

        // 3. Validate token and set authentication context if not already authenticated
        if (userPhoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userPhoneNumber);

            if (jwtService.validateToken(jwt, userDetails)) {
                // If token is valid, create an Authentication object
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // credentials are not stored in context for JWT
                        userDetails.getAuthorities()
                );
                // Set details from the request (e.g., remote address)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Set the authentication object in the SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 4. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
