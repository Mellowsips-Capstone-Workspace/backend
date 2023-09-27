package com.capstone.workspace.models.store;

import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

import java.util.List;

@Data
public class StoreModel extends BaseModel {
    private String name;

    private String phone;

    private String email;

    private String address;

    private String profileImage;

    private String coverImage;

    private List<String> categories;

    private Boolean isActive;

    private Boolean isOpen;

    private String partnerId;
}
