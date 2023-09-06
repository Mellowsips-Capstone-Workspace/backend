package com.capstone.workspace.repositories;

import com.capstone.workspace.entities.ReceiverProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReceiverProfileRepository extends JpaRepository<ReceiverProfile, UUID> {
    List<ReceiverProfile> findByUser_Id(UUID userId);

    ReceiverProfile findByUser_IdAndIsDefault(UUID userId, boolean isDefault);
}