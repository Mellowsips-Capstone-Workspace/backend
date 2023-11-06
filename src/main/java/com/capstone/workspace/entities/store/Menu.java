package com.capstone.workspace.entities.store;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@Entity
@Table(name = "menu", schema = "public")
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

    @OneToMany(mappedBy = "menu", fetch = FetchType.EAGER)
    private List<MenuSection> menuSections;
}
