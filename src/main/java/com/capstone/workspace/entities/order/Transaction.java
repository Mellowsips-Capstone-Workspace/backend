package com.capstone.workspace.entities.order;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.entities.store.IStoreEntity;
import com.capstone.workspace.enums.order.TransactionMethod;
import com.capstone.workspace.enums.order.TransactionStatus;
import com.capstone.workspace.enums.order.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Data
@Entity
@Table(name = "transaction", schema = "public")
@Where(clause = "is_deleted=false")
public class Transaction extends BaseEntity implements IPartnerEntity, IStoreEntity {
    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionMethod method;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> externalPaymentInfo;

    @Column
    private String partnerId;

    @Column
    private String storeId;
}
