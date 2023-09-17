package com.capstone.workspace.configurations.shared;

import com.capstone.workspace.interceptors.AuthUserInterceptor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorAppConfiguration implements WebMvcConfigurer {
    @NonNull
    private final AuthUserInterceptor authUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authUserInterceptor)
                .addPathPatterns("/api/auth/logout")
                .addPathPatterns("/api/auth/me/**")
                .addPathPatterns("/api/applications/**")
                .addPathPatterns("/api/documents/**");
    }
}