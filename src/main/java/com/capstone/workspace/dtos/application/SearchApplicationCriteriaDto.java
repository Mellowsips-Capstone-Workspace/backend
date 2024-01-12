package com.capstone.workspace.dtos.application;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.Map;

@Data
public class SearchApplicationCriteriaDto {
    @Valid
    private SearchApplicationFilterDto filter;

    private String keyword;

    @Valid
    private Map<String, Sort.Direction> order;
}

