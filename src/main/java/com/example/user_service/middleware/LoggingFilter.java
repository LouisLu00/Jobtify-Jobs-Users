package com.example.user_service.middleware;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Log details before the request is processed
        System.out.println("Incoming request: " + httpRequest.getMethod() + " " + httpRequest.getRequestURI());

        // Pass request to the next filter or controller
        chain.doFilter(request, response);

        // Log details after the request is processed
        System.out.println("Completed request: " + httpRequest.getMethod() + " " + httpRequest.getRequestURI() +
                " with status " + httpResponse.getStatus());
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
