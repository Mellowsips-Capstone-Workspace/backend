package com.capstone.workspace.repositories.application;

import com.capstone.workspace.entities.application.Representative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RepresentativeRepository extends JpaRepository<Representative, UUID> {
    Representative findByPartnerId(String partnerId);
}
