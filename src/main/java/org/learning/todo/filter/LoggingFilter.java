package org.learning.todo.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoggingFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger("RequestLogger");

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        logger.info("{} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
        long startTime = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            long timeTaken = System.currentTimeMillis() - startTime;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            logger.info(
                    "{} {} Status: {} Time(ms): {}",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    httpResponse.getStatus(),
                    timeTaken
            );
        }
    }
}
