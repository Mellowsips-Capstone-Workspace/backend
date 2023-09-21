package com.capstone.workspace.dtos.application;

import com.capstone.workspace.dtos.shared.PaginationDto;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class SearchApplicationDto {
    @Valid
    private PaginationDto pagination;

    @Valid
    private SearchApplicationCriteriaDto criteria;
}
