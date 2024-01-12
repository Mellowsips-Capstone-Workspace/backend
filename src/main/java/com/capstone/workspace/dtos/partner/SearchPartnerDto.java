package com.capstone.workspace.dtos.partner;

import com.capstone.workspace.dtos.shared.PaginationDto;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class SearchPartnerDto {
    @Valid
    private PaginationDto pagination;

    @Valid
    private SearchPartnerCriteriaDto criteria;
}
