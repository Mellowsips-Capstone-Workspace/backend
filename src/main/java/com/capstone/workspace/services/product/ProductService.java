package com.capstone.workspace.services.product;

import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.repositories.product.ProductRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    @NonNull
    private final ProductRepository repository;

    public Product getProductById(UUID id) {
        Product entity = repository.findById(id).orElse(null);

        if (entity == null) {
            throw new NotFoundException("Product not found");
        }

        return entity;
    }
}
