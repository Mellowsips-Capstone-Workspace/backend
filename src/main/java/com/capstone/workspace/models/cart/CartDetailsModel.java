package com.capstone.workspace.models.cart;

import com.capstone.workspace.models.shared.BaseModel;
import com.capstone.workspace.models.store.StoreModel;
import lombok.Data;

import java.util.List;

@Data
public class CartDetailsModel extends BaseModel {
    private StoreModel store;

    private List<CartItemModel> cartItems;

    private long tempPrice;

    private long finalPrice;

    private boolean isChange = false;
}
