package com.capstone.workspace.entities.user;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.enums.auth.AuthProviderType;
import com.capstone.workspace.enums.user.UserType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

@Data
@Entity
@Table(name = "user", schema = "public")
@Where(clause = "is_deleted=false")
public class User extends BaseEntity implements IPartnerEntity {
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String displayName;

    @Column
    private String phone;

    @Column
    private String email;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isVerified;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isActive;

    @Column
    private String avatar;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthProviderType provider;

    @Column
    private String partnerId;

    @Column
    private String storeId;
}
