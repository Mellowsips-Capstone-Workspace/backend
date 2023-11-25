package com.capstone.workspace.entities.voucher;

import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.enums.voucher.VoucherOrderSource;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
@Entity
@Table(name = "voucher_order", schema = "public")
@SQLDelete(sql = "UPDATE voucher_order SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted=false")
public class VoucherOrder extends BaseEntity {
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private long discountAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VoucherOrderSource source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
