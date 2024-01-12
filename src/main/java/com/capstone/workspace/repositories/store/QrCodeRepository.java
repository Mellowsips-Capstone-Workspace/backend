package com.capstone.workspace.repositories.store;

import com.capstone.workspace.entities.store.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, UUID> {
    List<QrCode> findAllByStoreId(String storeId);

    @Query(value = "SELECT nextval('qr_code_code_seq')", nativeQuery = true)
    int getCodeSequenceNextValue();

    QrCode findByCode(String code);
}
