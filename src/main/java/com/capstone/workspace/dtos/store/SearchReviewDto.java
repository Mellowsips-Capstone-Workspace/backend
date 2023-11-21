package com.capstone.workspace.dtos.store;

import com.capstone.workspace.dtos.shared.PaginationDto;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class SearchReviewDto {
    @Valid
    private PaginationDto pagination;

    @Valid
    private SearchReviewCriteriaDto criteria;
}
