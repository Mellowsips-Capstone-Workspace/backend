package com.capstone.workspace.repositories;

import com.capstone.workspace.entities.Application;
import com.capstone.workspace.enums.ApplicationStatus;
import com.capstone.workspace.enums.ApplicationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    Application findByCreatedByAndStatusIsNotAndTypeIs(String username, ApplicationStatus status, ApplicationType type);
}