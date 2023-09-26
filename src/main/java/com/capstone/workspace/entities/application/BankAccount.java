package com.capstone.workspace.entities.application;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@Entity
@Table(name = "bank_account", schema = "public")
@Where(clause = "is_deleted=false")
public class BankAccount extends BaseEntity implements IPartnerEntity {
    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String bankBranch;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String accountNumber;

    @Convert(attributeName = "identityImages")
    @Column
    private List<String> identityImages;

    @Column
    private String partnerId;
}
