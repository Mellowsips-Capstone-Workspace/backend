package com.capstone.workspace.models.store;

import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

import java.util.List;

@Data
public class MenuModel extends BaseModel {
    private String storeId;
    private List<MenuSectionModel> menuSections;
}
