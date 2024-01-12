package com.capstone.workspace.models.product;

import lombok.Data;

@Data
public class ProductPurchaseModel {
    private String name;

    private String description;

    private int numberOfPurchases;
}
