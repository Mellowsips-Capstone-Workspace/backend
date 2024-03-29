package com.capstone.workspace.entities.product;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.entities.store.IStoreEntity;
import com.capstone.workspace.entities.store.Menu;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@Entity
@Table(name = "product", schema = "public")
@SQLDelete(sql = "UPDATE product SET is_deleted = true WHERE id = ? AND version = ?")
@Where(clause = "is_deleted=false")
public class Product extends BaseEntity implements IPartnerEntity, IStoreEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long price;

    @Column
    private String coverImage;

    @Column
    private String description;

    @Convert(attributeName = "categories")
    @Column
    private List<String> categories;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isSoldOut = false;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy(value = "priority ASC")
    private List<ProductOptionSection> productOptionSections;

    @Column
    private String partnerId;

    @Column
    private String storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Product parent;
}
