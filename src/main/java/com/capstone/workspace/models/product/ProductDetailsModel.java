package com.capstone.workspace.models.product;

import lombok.Data;

import java.util.List;

@Data
public class ProductDetailsModel extends ProductModel {
    private List<ProductOptionSectionModel> productOptionSections;
}
