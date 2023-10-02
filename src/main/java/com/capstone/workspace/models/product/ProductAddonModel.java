package com.capstone.workspace.models.product;

import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

@Data
public class ProductAddonModel extends BaseModel {
    private String name;

    private long price;

    private Boolean isSoldOut;
}
