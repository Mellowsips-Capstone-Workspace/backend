package com.capstone.workspace.entities.user;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@Entity
@Table(name = "role", schema = "public")
@Where(clause = "is_deleted=false")
public class Role extends BaseEntity implements IPartnerEntity {
    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> permissions;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    protected Boolean isAllowedEdit = true;

    @Column
    private String partnerId;
}
