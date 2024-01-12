package com.capstone.workspace.dtos.order;

import com.capstone.workspace.dtos.shared.PaginationDto;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class SearchOrderDto {
    @Valid
    private PaginationDto pagination;

    @Valid
    private SearchOrderCriteriaDto criteria;
}
