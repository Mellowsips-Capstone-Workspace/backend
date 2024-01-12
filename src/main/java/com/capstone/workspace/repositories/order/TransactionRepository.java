package com.capstone.workspace.repositories.order;

import com.capstone.workspace.entities.order.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query(value = "SELECT t FROM Transaction t WHERE t.isDeleted=FALSE AND jsonb_extract_path_text(t.externalPaymentInfo,:key)=:value")
    Transaction getByExternalPaymentInfo(@Param("key") String key, @Param("value") String value);

    Transaction findByOrder_IdOrderByCreatedAtDesc(UUID orderId);
}
