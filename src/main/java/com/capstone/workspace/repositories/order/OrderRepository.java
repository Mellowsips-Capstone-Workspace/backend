package com.capstone.workspace.repositories.order;

import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.models.dashboard.AmountModel;
import com.capstone.workspace.models.dashboard.AmountStoreModel;
import com.capstone.workspace.repositories.shared.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends BaseRepository<Order, UUID> {
    List<Order> findByCreatedByAndStatusIn(String createdBy, OrderStatus[] status);

    @Query(
        value = "SELECT new com.capstone.workspace.models.dashboard.AmountModel(SUM(o.finalPrice)) FROM Order o " +
                "WHERE o.partnerId = :partnerId " +
                "AND (:storeId IS NULL OR o.storeId = :storeId) " +
                "AND o.status IN :statuses " +
                "AND (cast(:startDate as timestamp) IS NULL OR o.createdAt >= :startDate) " +
                "AND (cast(:endDate as timestamp) IS NULL OR o.createdAt <= :endDate) " +
                "GROUP BY o.partnerId"
    )
    AmountModel sumAmountByPartnerIdAndStoreIdAndInStatusesWithPeriod(
            @Param("partnerId") String partnerId,
            @Param("storeId") String storeId,
            @Param("statuses") OrderStatus[] statuses,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query(
            value = "SELECT new com.capstone.workspace.models.dashboard.AmountStoreModel(SUM(o.finalPrice), o.storeId, s.name) FROM Order o " +
                    "LEFT JOIN Store s ON o.storeId = cast(s.id as string) " +
                    "WHERE o.partnerId = :partnerId " +
                    "AND o.status IN :statuses " +
                    "AND (cast(:startDate as timestamp) IS NULL OR o.createdAt >= :startDate) " +
                    "AND (cast(:endDate as timestamp) IS NULL OR o.createdAt <= :endDate) " +
                    "GROUP BY o.storeId, s.name"
    )
    List<AmountStoreModel> sumAmountForStore(
        @Param("partnerId") String partnerId,
        @Param("statuses") OrderStatus[] statuses,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate
    );
}
