package com.capstone.workspace.repositories.product;

import com.capstone.workspace.entities.product.ProductOptionSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductOptionSectionRepository extends JpaRepository<ProductOptionSection, UUID> {
}
