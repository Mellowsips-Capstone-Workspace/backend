package com.capstone.workspace.models.product;

import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

import java.util.List;

@Data
public class ProductOptionSectionModel extends BaseModel {
    private String name;

    private int order;

    private Boolean isRequired;

    private int maxAllowedChoices;

    private List<ProductAddonModel> productAddons;
}
