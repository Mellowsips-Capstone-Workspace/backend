package com.capstone.workspace.entities.voucher;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.entities.store.IStoreEntity;
import com.capstone.workspace.enums.voucher.VoucherDiscountType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.time.Instant;

@Data
@Entity
@Table(name = "voucher", schema = "public")
@Where(clause = "is_deleted=false")
public class Voucher extends BaseEntity implements IPartnerEntity, IStoreEntity {
    @Column
    private int quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VoucherDiscountType discountType;

    @Column
    private Instant startDate;

    @Column
    private Instant endDate;

    @Column
    private int maxUsesPerUser;

    @Column
    private long maxDiscountAmount;

    @Column
    private long minOrderAmount;

    @Column(nullable = false, unique = true)
    private String code;

    @Column
    private String partnerId;

    @Column
    private String storeId;
}
