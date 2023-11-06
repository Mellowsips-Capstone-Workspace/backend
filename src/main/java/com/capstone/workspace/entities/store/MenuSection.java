package com.capstone.workspace.entities.store;

import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@Entity
@Table(name = "menu_section", schema = "public")
@Where(clause = "is_deleted=false")
public class MenuSection extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "menu_section_product",
            joinColumns = @JoinColumn(name = "menu_section_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;
}
