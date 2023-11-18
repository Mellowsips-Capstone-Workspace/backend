package com.capstone.workspace.dtos.store;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.Map;

@Data
public class SearchMenuCriteriaDto {
    @Valid
    private SearchMenuFilterDto filter;

    private String keyword;

    @Valid
    private Map<String, Sort.Direction> order;
}

