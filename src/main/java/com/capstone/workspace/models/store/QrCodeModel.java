package com.capstone.workspace.models.store;

import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

@Data
public class QrCodeModel extends BaseModel {
    private String name;

    private String storeId;

    private String code;
}
