package com.example.health_service.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();

        MDC.put("request_id", requestId);
        MDC.put("http_method", req.getMethod());
        MDC.put("http_path", req.getRequestURI());
        MDC.put("remote_ip", clientIp(req));

        try {
            log.info("Incoming request");
            chain.doFilter(req, res);
        } finally {
            long duration = System.currentTimeMillis() - start;
            MDC.put("http_status", String.valueOf(res.getStatus()));
            MDC.put("duration_ms", String.valueOf(duration));
            log.info("Request completed");
            MDC.clear();
        }
    }

    private String clientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        return (xff != null && !xff.isEmpty()) ? xff.split(",")[0].trim() : req.getRemoteAddr();
    }
}