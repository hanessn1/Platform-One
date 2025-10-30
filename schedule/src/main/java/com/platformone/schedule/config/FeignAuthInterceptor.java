package com.platformone.schedule.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignAuthInterceptor implements RequestInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Logger log = LoggerFactory.getLogger(FeignAuthInterceptor.class);

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest request = getCurrentRequest();

        if (request != null) {
            String token = request.getHeader(AUTHORIZATION_HEADER);
            if (token != null && token.startsWith("Bearer ")) {
                template.header(AUTHORIZATION_HEADER, token);
                log.debug("--- FeignAuthInterceptor: Added Authorization header ---");
            } else {
                log.debug("--- FeignAuthInterceptor: No Bearer token found in incoming request ---");
            }
        } else {
            log.debug("--- FeignAuthInterceptor: Could not get current request ---");
        }
    }

    private HttpServletRequest getCurrentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }
        return null;
    }
}