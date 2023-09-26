package com.capstone.workspace.repositories.application;

import com.capstone.workspace.entities.application.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    BankAccount findByPartnerId(String partnerId);
}
