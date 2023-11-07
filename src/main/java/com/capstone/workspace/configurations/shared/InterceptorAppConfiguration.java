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
                .addPathPatterns("/api/documents")
                .addPathPatterns("/api/stores/**/qrcodes")
                .addPathPatterns("/api/stores/search")
                .addPathPatterns("/api/stores/**/profile-image")
                .addPathPatterns("/api/stores/**/cover-image")
                .addPathPatterns("/api/stores/**/operational-hours")
                .addPathPatterns("/api/menus/**")
                .addPathPatterns("/api/qrcodes/**")
                .addPathPatterns("/api/carts/**")
                .addPathPatterns("/api/orders/**")
                .addPathPatterns("/api/vouchers/**")
                .addPathPatterns("/api/products")
                .addPathPatterns("/api/products/search")
                .addPathPatterns("/api/notifications/**")
                .addPathPatterns("/api/users/**")
                .excludePathPatterns("/api/vouchers/details/**")
                .excludePathPatterns("/api/orders/zalopay/callback");
    }
}
