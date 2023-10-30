package com.capstone.workspace.entities.voucher;

import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

@Data
@Entity
@Table(name = "voucher_order", schema = "public")
@Where(clause = "is_deleted=false")
public class VoucherOrder extends BaseEntity {
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private long discountAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
