package com.capstone.workspace.entities.product;

import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@Entity
@Table(name = "product_option_section", schema = "public")
@Where(clause = "is_deleted=false")
public class ProductOptionSection extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int order;

    @Column
    protected Boolean isRequired;

    @Column
    private Integer maxAllowedChoices;

    @OneToMany(mappedBy = "productOptionSection", fetch = FetchType.EAGER)
    private List<ProductAddon> productAddons;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
