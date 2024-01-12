package com.capstone.workspace.entities.document;

import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "document", schema = "public")
@Where(clause = "is_deleted=false")
public class Document extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private byte[] content;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private long size;

    @Column
    private UUID reference;

    @Column
    private String referenceType;
}
