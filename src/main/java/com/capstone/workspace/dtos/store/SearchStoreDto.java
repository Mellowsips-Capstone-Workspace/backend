package com.capstone.workspace.dtos.store;

import com.capstone.workspace.dtos.shared.PaginationDto;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class SearchStoreDto {
    @Valid
    private PaginationDto pagination;

    @Valid
    private SearchStoreCriteriaDto criteria;
}
