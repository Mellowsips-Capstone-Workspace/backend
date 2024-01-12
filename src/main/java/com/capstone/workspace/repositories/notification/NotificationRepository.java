package com.capstone.workspace.repositories.notification;

import com.capstone.workspace.entities.notification.Notification;
import com.capstone.workspace.repositories.shared.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface NotificationRepository extends BaseRepository<Notification, UUID> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE Notification n SET n.isSeen = TRUE, n.seenAt = INSTANT WHERE n.receiver = :username AND (n.isSeen = FALSE OR n.isSeen IS NULL)")
    void markAllAsReadByUsername(@Param("username") String username);
}
