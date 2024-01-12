package com.capstone.workspace.entities.product;

import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@Entity
@Table(name = "product_option_section", schema = "public")
@SQLDelete(sql = "UPDATE product_option_section SET is_deleted = true WHERE id = ? AND version = ?")
@Where(clause = "is_deleted=false")
public class ProductOptionSection extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int priority;

    @Column
    private Boolean isRequired;

    @Column
    private Integer maxAllowedChoices;

    @OneToMany(mappedBy = "productOptionSection", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ProductAddon> productAddons;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
