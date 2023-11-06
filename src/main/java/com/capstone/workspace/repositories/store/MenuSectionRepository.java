package com.capstone.workspace.repositories.store;

import com.capstone.workspace.entities.store.MenuSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MenuSectionRepository extends JpaRepository<MenuSection, UUID> {
}
