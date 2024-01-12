package com.capstone.workspace.entities.user;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.entities.store.IStoreEntity;
import com.capstone.workspace.enums.auth.AuthProviderType;
import com.capstone.workspace.enums.user.UserType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.time.Instant;

@Data
@Entity
@Table(name = "user", schema = "public")
@Where(clause = "is_deleted=false")
public class User extends BaseEntity implements IPartnerEntity, IStoreEntity {
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
    private Boolean isActive = true;

    @Column
    private Instant inactiveUntil;

    @Column
    private int numberOfFlakes = 0;

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
