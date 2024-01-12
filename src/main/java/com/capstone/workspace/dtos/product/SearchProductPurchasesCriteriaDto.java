package com.capstone.workspace.dtos.product;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class SearchProductPurchasesCriteriaDto {
    @Valid
    private SearchProductPurchasesFilterDto filter;

    private Sort.Direction order;
}
