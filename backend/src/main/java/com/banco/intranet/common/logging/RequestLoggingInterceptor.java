package com.banco.intranet.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Interceptor para logging de requests y responses
 */
@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID = "X-Request-ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String requestId = UUID.randomUUID().toString();
        request.setAttribute(REQUEST_ID, requestId);

        log.info("[{}] {} {} - Remote: {}",
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                               Object handler, Exception ex) throws Exception {
        String requestId = (String) request.getAttribute(REQUEST_ID);
        
        if (ex != null) {
            log.error("[{}] Request failed with exception: {}", requestId, ex.getMessage(), ex);
        } else {
            log.info("[{}] Response status: {}", requestId, response.getStatus());
        }
    }
}
