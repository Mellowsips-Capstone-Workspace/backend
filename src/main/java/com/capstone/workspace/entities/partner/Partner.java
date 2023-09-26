package com.capstone.workspace.entities.partner;

import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.enums.partner.BusinessType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "partner", schema = "public")
@Where(clause = "is_deleted=false")
public class Partner extends BaseEntity {
    @Column
    private String name;

    @Column
    private String logo;

    @Column
    private String businessCode;

    @Column
    private String taxCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BusinessType type;

    @Column
    private LocalDate businessIdentityIssueDate;

    @Convert(attributeName = "businessIdentityImages")
    @Column
    private List<String> businessIdentityImages;
}
