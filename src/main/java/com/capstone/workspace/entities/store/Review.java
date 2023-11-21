package com.capstone.workspace.entities.store;

import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

@Data
@Entity
@Table(name = "review", schema = "public")
@Where(clause = "is_deleted=false")
public class Review extends BaseEntity {
    @Column(nullable = false)
    private int point;

    @Column
    private String comment;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;
}
