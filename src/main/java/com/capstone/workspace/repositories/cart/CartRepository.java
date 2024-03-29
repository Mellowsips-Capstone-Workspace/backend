package com.capstone.workspace.repositories.cart;

import com.capstone.workspace.entities.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Cart findByCreatedByAndStoreId(String createdBy, String storeId);

    List<Cart> findAllByCreatedBy(String createdBy);
}
