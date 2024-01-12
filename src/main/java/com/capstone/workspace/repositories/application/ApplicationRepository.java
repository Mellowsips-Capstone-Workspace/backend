package com.capstone.workspace.repositories.application;

import com.capstone.workspace.entities.application.Application;
import com.capstone.workspace.enums.application.ApplicationStatus;
import com.capstone.workspace.enums.application.ApplicationType;
import com.capstone.workspace.repositories.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationRepository extends BaseRepository<Application, UUID> {
    Application findByCreatedByAndStatusIsNotAndTypeIs(String username, ApplicationStatus status, ApplicationType type);
}