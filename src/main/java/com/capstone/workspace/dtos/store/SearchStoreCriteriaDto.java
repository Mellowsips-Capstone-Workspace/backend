package com.capstone.workspace.dtos.store;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.Map;

@Setter(AccessLevel.NONE)
@Data
public class SearchStoreCriteriaDto {
    @Valid
    private SearchStoreFilterDto filter;

    private String keyword;

    @Valid
    private Map<String, Sort.Direction> order;
}

