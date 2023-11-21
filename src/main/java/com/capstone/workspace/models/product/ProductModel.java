package com.capstone.workspace.models.product;

import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

import java.util.List;

@Data
public class ProductModel extends BaseModel {
    protected String name;

    protected long price;

    protected String coverImage;

    protected String description;

    protected List<String> categories;

    protected Boolean isSoldOut;

    protected String partnerId;

    protected String storeId;
}
