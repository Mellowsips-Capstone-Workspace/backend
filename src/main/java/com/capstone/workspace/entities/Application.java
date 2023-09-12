package com.capstone.workspace.entities;

import com.capstone.workspace.enums.ApplicationStatus;
import com.capstone.workspace.enums.ApplicationType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Entity
@Table(name = "application", schema = "public")
@Where(clause = "is_deleted=false")
public class Application extends BaseEntity {
    @Column
    private LocalDateTime approvedAt;

    @Column
    private String approvedBy;

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> jsonData;

    @Column
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private ApplicationType type;
}
