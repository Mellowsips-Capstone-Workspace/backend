package com.capstone.workspace.repositories.store;

import com.capstone.workspace.entities.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {
    Store findByPartnerId(String partnerId);
}
