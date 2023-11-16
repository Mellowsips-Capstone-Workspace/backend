package com.capstone.workspace.repositories.product;

import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.repositories.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends BaseRepository<Product, UUID> {
    Product findByIdAndIsDeletedIsFalse(UUID id);
}
