package com.capstone.workspace.entities.user;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "permission", schema = "public")
public class Permission implements Serializable {
    @Id
    @Column
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;
}
