package com.capstone.workspace.entities.store;

import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@Entity
@Table(name = "menu", schema = "public")
@Where(clause = "is_deleted=false")
public class Menu extends BaseEntity implements IStoreEntity {
    @Column
    private String storeId;

    @OneToMany(mappedBy = "menu", fetch = FetchType.EAGER)
    private List<MenuSection> menuSections;
}
