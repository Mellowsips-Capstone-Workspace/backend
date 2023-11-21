package com.capstone.workspace.dtos.product;

import lombok.Data;

import java.time.Instant;

@Data
public class SearchProductPurchasesFilterDto {
    private String storeId;

    private Instant startDate;

    private Instant endDate;
}
