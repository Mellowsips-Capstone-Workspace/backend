package com.capstone.workspace.dtos.product;

import com.capstone.workspace.dtos.shared.PaginationDto;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class SearchProductDto {
    @Valid
    private PaginationDto pagination;

    @Valid
    private SearchProductCriteriaDto criteria;
}
