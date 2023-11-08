package com.capstone.workspace.repositories.voucher;

import com.capstone.workspace.entities.voucher.Voucher;
import com.capstone.workspace.repositories.shared.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VoucherRepository extends BaseRepository<Voucher, UUID> {
    Voucher findByCode(String code);
}
