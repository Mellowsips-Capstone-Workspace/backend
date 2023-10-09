package com.capstone.workspace.entities.order;

import com.capstone.workspace.converters.CartDetailsConverter;
import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.entities.store.IStoreEntity;
import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.models.cart.CartDetailsModel;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Where;
import org.hibernate.dialect.PostgreSQLJsonPGObjectJsonbType;

@Data
@Entity
@Table(name = "order", schema = "public")
@Where(clause = "is_deleted=false")
public class Order extends BaseEntity implements IPartnerEntity, IStoreEntity {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private long finalPrice;

    @Convert(converter = CartDetailsConverter.class)
    @Column(nullable = false)
    @JdbcType(PostgreSQLJsonPGObjectJsonbType.class)
    private CartDetailsModel details;

    @Column
    private String partnerId;

    @Column
    private String storeId;
}
