package com.capstone.workspace.repositories.store;

import com.capstone.workspace.entities.store.Menu;
import com.capstone.workspace.repositories.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MenuRepository extends BaseRepository<Menu, UUID> {
    Menu findByStoreIdAndIsActiveTrue(String storeId);
}
