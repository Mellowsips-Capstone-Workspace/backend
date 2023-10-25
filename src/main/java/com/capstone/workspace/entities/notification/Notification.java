package com.capstone.workspace.entities.notification;

import com.capstone.workspace.listeners.NotificationListener;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@EntityListeners(NotificationListener.class)
@Table(name = "notification", schema = "public")
@Where(clause = "is_deleted=false")
public class Notification implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private UUID id;

    @Column
    private String receiver;

    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private String subject;

    @Column
    private String shortDescription;

    @Column(nullable = false)
    private String content;

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> metadata;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isSeen = false;

    @Column
    private Instant seenAt;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isDeleted = false;
}
