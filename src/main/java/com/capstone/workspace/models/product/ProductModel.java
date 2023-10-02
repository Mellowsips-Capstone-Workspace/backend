package com.capstone.workspace.models.product;

import lombok.Data;

import java.util.List;

@Data
public class ProductModel extends ProductMenuModel {
    private List<ProductOptionSectionModel> productOptionSections;
}
