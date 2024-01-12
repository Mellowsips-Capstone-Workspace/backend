package com.capstone.workspace.entities.product;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

@Data
@Entity
@Immutable
public class ProductPurchaseView {
    @Id
    private UUID id;

    private String name;

    private String description;

    private int numberOfPurchases;
}
