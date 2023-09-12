package com.capstone.workspace.entities.user;

import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

@Data
@Entity
@Table(name = "receiver_profile", schema = "public")
@Where(clause = "is_deleted=false")
public class ReceiverProfile extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column
    private Boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
