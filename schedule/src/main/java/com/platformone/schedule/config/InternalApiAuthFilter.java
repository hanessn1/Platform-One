package com.platformone.schedule.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class InternalApiAuthFilter extends OncePerRequestFilter {

    @Value("${internal.api.header-name}")
    private String internalApiHeaderName;

    @Value("${internal.api.key}")
    private String expectedInternalApiKey;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String actualApiKey = request.getHeader(internalApiHeaderName);

        if (actualApiKey != null && actualApiKey.equals(expectedInternalApiKey)) {
            SimpleGrantedAuthority internalAuthority = new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE");
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    "internal-service",
                    null,
                    Collections.singletonList(internalAuthority)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Internal API Key validated successfully.");

            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return false;
    }
}