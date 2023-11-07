package com.capstone.workspace.dtos.store;

import com.capstone.workspace.dtos.shared.PaginationDto;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class SearchMenuDto {
    @Valid
    private PaginationDto pagination;

    @Valid
    private SearchMenuCriteriaDto criteria;
}
