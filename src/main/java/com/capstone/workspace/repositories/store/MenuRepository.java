package com.capstone.workspace.repositories.store;

import com.capstone.workspace.entities.store.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {
    Menu findByStoreIdAndIsActiveTrue(String storeId);
}
