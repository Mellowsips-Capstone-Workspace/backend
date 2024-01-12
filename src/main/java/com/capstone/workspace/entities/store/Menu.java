package com.capstone.workspace.entities.store;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@Entity
@Table(name = "menu", schema = "public")
@SQLDelete(sql = "UPDATE menu SET is_deleted = true WHERE id = ? AND version = ?")
@Where(clause = "is_deleted=false")
public class Menu extends BaseEntity implements IStoreEntity, IPartnerEntity {
    @Column(nullable = false)
    private String name;

    @Column
    private Boolean isActive;

    @Column
    private String storeId;

    @Column
    private String partnerId;

    @OneToMany(mappedBy = "menu", fetch = FetchType.EAGER, orphanRemoval = true)
    @OrderBy(value = "priority ASC")
    private List<MenuSection> menuSections;
}
