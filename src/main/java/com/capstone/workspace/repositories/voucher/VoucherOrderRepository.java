package com.capstone.workspace.repositories.voucher;

import com.capstone.workspace.entities.voucher.VoucherOrder;
import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.models.dashboard.AmountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface VoucherOrderRepository extends JpaRepository<VoucherOrder, UUID> {
    @Query(
        value = "SELECT new com.capstone.workspace.models.dashboard.AmountModel(SUM(vo.discountAmount)) FROM VoucherOrder vo " +
                "WHERE vo.voucher.partnerId = :partnerId " +
                "AND (:storeId IS NULL OR vo.voucher.storeId = :storeId) " +
                "AND vo.order.status IN :statuses " +
                "AND (cast(:startDate as timestamp) IS NULL OR vo.createdAt >= :startDate) " +
                "AND (cast(:endDate as timestamp) IS NULL OR vo.createdAt <= :endDate) " +
                "GROUP BY vo.voucher.partnerId"
    )
    AmountModel sumAmountOfBusiness(
        @Param("partnerId") String partnerId,
        @Param("storeId") String storeId,
        @Param("statuses") OrderStatus[] statuses,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate
    );

    @Query(
            value = "SELECT new com.capstone.workspace.models.dashboard.AmountModel(SUM(vo.discountAmount)) FROM VoucherOrder vo " +
                    "WHERE vo.source = 'SYSTEM' AND vo.order.partnerId = :partnerId " +
                    "AND (:storeId IS NULL OR vo.order.storeId = :storeId) " +
                    "AND vo.order.status IN :statuses " +
                    "AND (cast(:startDate as timestamp) IS NULL OR vo.createdAt >= :startDate) " +
                    "AND (cast(:endDate as timestamp) IS NULL OR vo.createdAt <= :endDate) " +
                    "GROUP BY vo.order.partnerId"
    )
    AmountModel sumAmountOfSystemUsedByBusiness(
            @Param("partnerId") String partnerId,
            @Param("storeId") String storeId,
            @Param("statuses") OrderStatus[] statuses,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query(
            value = "SELECT new com.capstone.workspace.models.dashboard.AmountModel(SUM(vo.discountAmount)) FROM VoucherOrder vo " +
                    "WHERE vo.source = 'SYSTEM' " +
                    "AND vo.order.status IN :statuses " +
                    "AND (cast(:startDate as timestamp) IS NULL OR vo.createdAt >= :startDate) " +
                    "AND (cast(:endDate as timestamp) IS NULL OR vo.createdAt <= :endDate) " +
                    "GROUP BY vo.source"
    )
    AmountModel sumAmountOfSystem(
            @Param("statuses") OrderStatus[] statuses,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );
}
