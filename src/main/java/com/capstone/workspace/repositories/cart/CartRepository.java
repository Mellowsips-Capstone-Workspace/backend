package com.capstone.workspace.repositories.cart;

import com.capstone.workspace.entities.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Cart findByCreatedByAndStoreId(String createdBy, String storeId);

    List<Cart> findAllByCreatedBy(String createdBy);

    @Query(
            value = "SELECT c.*, ci.* FROM cart c " +
                    "INNER JOIN cart_item ci ON c.id = ci.cart_id " +
                    "INNER JOIN product p ON ci.product_id = p.id " +
                    "WHERE c.id = :id " +
                    "AND p. = TRUE",
            nativeQuery = true
    )
    Cart findCartWithDeletedProducts(@Param("id") UUID id);

}
