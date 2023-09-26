package com.capstone.workspace.entities.application;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.enums.partner.IdentityType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "representative", schema = "public")
@Where(clause = "is_deleted=false")
public class Representative extends BaseEntity implements IPartnerEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IdentityType identityType;

    @Column(nullable = false)
    private String identityNumber;

    @Column(nullable = false)
    private LocalDate identityIssueDate;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column
    private String identityFrontImage;

    @Column
    private String identityBackImage;

    @Column
    private String partnerId;
}
