package com.platformone.booking.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignAuthInterceptor implements RequestInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Logger log = LoggerFactory.getLogger(FeignAuthInterceptor.class);

    @Value("${internal.api.header-name}")
    private String internalApiHeaderName;

    @Value("${internal.api.key}")
    private String internalApiKey;

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest request = getCurrentRequest();
        String userToken = null;

        if (request != null) {
            userToken = request.getHeader(AUTHORIZATION_HEADER);
        }

        if (userToken != null && userToken.startsWith("Bearer ")) {
            template.header(AUTHORIZATION_HEADER, userToken);
            log.debug("--- FeignAuthInterceptor: Forwarding user Bearer token ---");
        }
        else {
            template.header(internalApiHeaderName, internalApiKey);
            log.debug("--- FeignAuthInterceptor: Adding Internal API Key header ---");
        }
    }

    private HttpServletRequest getCurrentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }
        return null;
    }
}