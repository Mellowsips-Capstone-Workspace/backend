package com.capstone.workspace.repositories.notification;

import com.capstone.workspace.entities.notification.Notification;
import com.capstone.workspace.repositories.shared.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends BaseRepository<Notification, UUID> {
}
