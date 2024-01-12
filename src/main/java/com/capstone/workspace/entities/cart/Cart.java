package com.capstone.workspace.entities.cart;

import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.entities.store.IStoreEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@Entity
@Table(name = "cart", schema = "public")
@SQLDelete(sql = "UPDATE cart SET is_deleted = true WHERE id = ? AND version = ?")
@Where(clause = "is_deleted=false")
public class Cart extends BaseEntity implements IStoreEntity {
    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY)
    private List<CartItem> cartItems;

    @Column
    private String storeId;
}
