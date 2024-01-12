package com.capstone.workspace.dtos.store;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.Map;

@Data
public class SearchReviewCriteriaDto {
    @Valid
    private SearchReviewFilterDto filter;

    @Valid
    private Map<String, Sort.Direction> order;
}
