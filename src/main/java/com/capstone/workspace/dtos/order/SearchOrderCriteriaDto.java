package com.capstone.workspace.dtos.order;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.Map;

@Data
public class SearchOrderCriteriaDto {
    @Valid
    private SearchOrderFilterDto filter;

    private String keyword;

    @Valid
    private Map<String, Sort.Direction> order;
}

