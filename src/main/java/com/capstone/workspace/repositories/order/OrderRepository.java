package com.capstone.workspace.repositories.order;

import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.enums.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCreatedByAndStatusIn(String createdBy, OrderStatus[] status);
}
