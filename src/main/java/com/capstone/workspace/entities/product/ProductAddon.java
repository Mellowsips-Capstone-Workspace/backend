package com.capstone.workspace.entities.product;

import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
@Entity
@Table(name = "product_addon", schema = "public")
@SQLDelete(sql = "UPDATE product_addon SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted=false")
public class ProductAddon extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long price;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isSoldOut = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_section_id")
    private ProductOptionSection productOptionSection;
}
