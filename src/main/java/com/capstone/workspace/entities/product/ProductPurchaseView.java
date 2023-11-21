package com.capstone.workspace.entities.product;

import com.capstone.workspace.entities.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.util.List;

@Data
@Entity
@Immutable
public class ProductPurchaseView extends BaseEntity {
    private String name;

    private long price;

    private String coverImage;

    private String description;

    @Convert(attributeName = "categories")
    private List<String> categories;

    private Boolean isSoldOut = false;

    private String partnerId;

    private String storeId;

    private int numberOfPurchases;
}
