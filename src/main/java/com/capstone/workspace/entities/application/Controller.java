package com.capstone.workspace.entities.application;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Where;

@Data
@Entity
@Table(name = "controller", schema = "public")
@Where(clause = "is_deleted=false")
public class Controller extends BaseEntity implements IPartnerEntity {
    @Column
    private String name;

    @Column
    private String phone;

    @Column
    private String email;

    @Column
    private String partnerId;
}
