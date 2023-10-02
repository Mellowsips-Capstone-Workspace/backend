package com.capstone.workspace.models.store;

import com.capstone.workspace.models.product.ProductMenuModel;
import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

import java.util.List;

@Data
public class MenuSectionModel extends BaseModel {
    private String name;

    private int order;

    private List<ProductMenuModel> products;
}
