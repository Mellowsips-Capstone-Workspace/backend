package com.capstone.workspace.configurations.shared;

import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.entities.store.Store;
import com.capstone.workspace.repositories.product.ProductRepository;
import com.capstone.workspace.repositories.product.ProductRepositoryImplement;
import com.capstone.workspace.repositories.shared.BaseRepositoryImplement;
import com.capstone.workspace.repositories.store.StoreRepository;
import com.capstone.workspace.repositories.store.StoreRepositoryImplement;
import jakarta.persistence.EntityManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableJpaRepositories(basePackages = "com.capstone.workspace.repositories",repositoryBaseClass = BaseRepositoryImplement.class, repositoryImplementationPostfix = "Implement")
@RequiredArgsConstructor
public class SharedConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    @Primary
    public ProductRepository productRepositoryImplement(EntityManager entityManager) {
        return new ProductRepositoryImplement(Product.class, entityManager);
    }

    @Bean
    @Primary
    public StoreRepository storeRepositoryImplement(EntityManager entityManager) {
        return new StoreRepositoryImplement(Store.class, entityManager);
    }
}
