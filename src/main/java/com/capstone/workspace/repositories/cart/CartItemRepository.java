package com.capstone.workspace.repositories.cart;

import com.capstone.workspace.entities.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    @Query(
        value = "SELECT * FROM cart_item c " +
                "WHERE c.is_deleted = FALSE " +
                "AND c.created_by = :createdBy " +
                "AND c.product_id = :productId " +
                "AND (c.addons @> :addons) AND (:addons @> c.addons) " +
                "AND ((c.note IS NULL AND :note IS NULL) OR c.note = :note)",
        nativeQuery = true
    )
    CartItem findByCreatedByAndProduct_IdAndAddonsAndNote(
        @Param("createdBy") String createdBy,
        @Param("productId") UUID productId,
        @Param("addons") UUID[] addons,
        @Param("note") String note
    );

    int countByCreatedByAndCart_StoreId(
      @Param("createdBy") String createdBy,
      @Param("storeId") String storeId
    );

    List<CartItem> findAllByCreatedByAndCart_StoreId(
      @Param("createdBy") String createdBy,
      @Param("storeId") String storeId
    );
}
