package com.capstone.workspace.entities;

import com.capstone.workspace.enums.AuthProviderType;
import com.capstone.workspace.enums.UserType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

@Data
@Entity
@Table(name = "user", schema = "public")
@Where(clause = "is_deleted=false")
public class User extends BaseEntity {
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

    @Column
    private String avatar;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthProviderType provider;
}
