package com.capstone.workspace.models.cart;

import com.capstone.workspace.models.shared.BaseModel;
import com.capstone.workspace.models.store.StoreModel;
import lombok.Data;

@Data
public class CartModel extends BaseModel {
    private StoreModel store;
    private int numberOfItems;
}
