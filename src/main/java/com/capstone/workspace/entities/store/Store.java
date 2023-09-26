package com.capstone.workspace.entities.store;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@Entity
@Table(name = "store", schema = "public")
@Where(clause = "is_deleted=false")
public class Store extends BaseEntity implements IPartnerEntity {
    @Column(nullable = false)
    private String name;

    @Column
    private String phone;

    @Column
    private String email;

    @Column(nullable = false)
    private String address;

    @Column
    private String profileImage;

    @Column
    private String coverImage;

    @Convert(attributeName = "categories")
    @Column
    private List<String> categories;

    @Column
    private boolean isActive;

    @Column
    private boolean isOpen;

    @Column
    private String partnerId;
}
