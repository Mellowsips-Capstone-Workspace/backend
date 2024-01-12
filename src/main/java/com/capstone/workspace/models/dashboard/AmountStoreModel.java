package com.capstone.workspace.models.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AmountStoreModel {
    private long amount;

    private String storeId;

    private String name;
}
