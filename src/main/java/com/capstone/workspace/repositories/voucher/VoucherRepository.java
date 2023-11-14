package com.capstone.workspace.repositories.voucher;

import com.capstone.workspace.entities.voucher.Voucher;
import com.capstone.workspace.repositories.shared.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VoucherRepository extends BaseRepository<Voucher, UUID> {
    Voucher findByCode(String code);

    @Query(
        value = "SELECT v FROM Voucher v " +
                "WHERE v.isHidden = FALSE " +
                "AND v.quantity > 0 " +
                "AND v.startDate < CURRENT_TIMESTAMP AND v.endDate > CURRENT_TIMESTAMP " +
                "AND (v.partnerId IS NULL OR (v.partnerId = :partnerId AND (v.storeId IS NULL OR v.storeId = :storeId)))" +
                "AND v.maxUsesPerUser > (SELECT COUNT(*) FROM VoucherOrder vo WHERE vo.voucher.id = v.id AND vo.createdBy = :username)"
    )
    List<Voucher> customerCartGetVouchers(@Param("username") String username, @Param("partnerId") String partnerId, @Param("storeId") String storeId);
}
