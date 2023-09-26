package com.capstone.workspace.repositories.application;

import com.capstone.workspace.entities.application.Controller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ControllerRepository extends JpaRepository<Controller, UUID> {
    Controller findByPartnerId(String partnerId);
}
