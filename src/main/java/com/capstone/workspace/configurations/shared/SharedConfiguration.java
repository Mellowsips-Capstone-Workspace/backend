package com.capstone.workspace.configurations.shared;

import com.capstone.workspace.repositories.shared.BaseRepositoryImplement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableJpaRepositories(basePackages = "com.capstone.workspace.repositories",repositoryBaseClass = BaseRepositoryImplement.class)
public class SharedConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
