package com.capstone.workspace.entities.application;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.enums.application.ApplicationStatus;
import com.capstone.workspace.enums.application.ApplicationType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Data
@Entity
@Table(name = "application", schema = "public")
@Where(clause = "is_deleted=false")
public class Application extends BaseEntity implements IPartnerEntity {
    @Column
    private Instant approvedAt;

    @Column
    private String approvedBy;

    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> jsonData;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplicationType type;

    @Column
    private String partnerId;
}
