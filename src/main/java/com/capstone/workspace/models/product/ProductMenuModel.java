package com.capstone.workspace.models.product;

import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

import java.util.List;

@Data
public class ProductMenuModel extends BaseModel {
    private String name;

    private long price;

    private String coverImage;

    private List<String> categories;

    private Boolean isSoldOut;

    private String partnerId;

    private String storeId;
}
