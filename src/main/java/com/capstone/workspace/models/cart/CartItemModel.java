package com.capstone.workspace.models.cart;

import com.capstone.workspace.models.product.ProductAddonModel;
import com.capstone.workspace.models.product.ProductModel;
import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

import java.util.List;

@Data
public class CartItemModel extends BaseModel {
    private int quantity;

    private String note;

    private ProductModel product;

    private List<ProductAddonModel> addons;

    private long tempPrice;

    private long finalPrice;

    private boolean isChange = false;
}
