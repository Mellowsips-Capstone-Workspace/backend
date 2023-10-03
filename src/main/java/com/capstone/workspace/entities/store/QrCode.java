package com.capstone.workspace.entities.store;

import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Where;

@Data
@Entity
@Table(name = "qr_code", schema = "public")
@Where(clause = "is_deleted=false")
public class QrCode extends BaseEntity implements IStoreEntity {
    @Column(nullable = false)
    private String name;

    @Column
    private String storeId;
}
