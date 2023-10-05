package com.capstone.workspace.repositories.product;

import com.capstone.workspace.entities.product.ProductAddon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductAddonRepository extends JpaRepository<ProductAddon, UUID> {
}
