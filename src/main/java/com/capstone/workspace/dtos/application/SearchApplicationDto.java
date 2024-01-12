package com.capstone.workspace.dtos.application;

import com.capstone.workspace.dtos.shared.PaginationDto;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class SearchApplicationDto {
    @Valid
    private PaginationDto pagination;

    @Valid
    private SearchApplicationCriteriaDto criteria;
}
