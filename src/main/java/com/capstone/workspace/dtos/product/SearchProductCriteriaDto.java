package com.capstone.workspace.dtos.product;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.Map;

@Data
public class SearchProductCriteriaDto {
    @Valid
    private SearchProductFilterDto filter;

    private String keyword;

    @Valid
    private Map<String, Sort.Direction> order;
}

