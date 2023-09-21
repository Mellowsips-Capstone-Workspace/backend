package com.capstone.workspace.configurations.shared;

import com.capstone.workspace.repositories.shared.BaseRepositoryImplement;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.capstone.workspace.repositories",repositoryBaseClass = BaseRepositoryImplement.class)
public class SharedConfiguration {
}
