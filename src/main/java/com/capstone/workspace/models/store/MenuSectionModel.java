package com.capstone.workspace.models.store;

import com.capstone.workspace.models.product.ProductModel;
import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

import java.util.List;

@Data
public class MenuSectionModel extends BaseModel {
    private String name;

    private int priority;

    private List<ProductModel> products;
}
