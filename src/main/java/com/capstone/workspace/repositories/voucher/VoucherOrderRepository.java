package com.capstone.workspace.repositories.voucher;

import com.capstone.workspace.entities.voucher.VoucherOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VoucherOrderRepository extends JpaRepository<VoucherOrder, UUID> {
}
