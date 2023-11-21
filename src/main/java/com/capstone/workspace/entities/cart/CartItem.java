package com.capstone.workspace.entities.cart;

import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "cart_item", schema = "public")
@SQLDelete(sql = "UPDATE cart_item SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted=false")
public class CartItem extends BaseEntity {
    @Column(nullable = false)
    private int quantity;

    @Column
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Convert(attributeName = "addons")
    @Column
    private List<UUID> addons;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column
    private Boolean isBought = false;
}
