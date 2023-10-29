package com.capstone.workspace.dtos.order;

import com.capstone.workspace.dtos.shared.PaginationDto;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class SearchOrderDto {
    @Valid
    private PaginationDto pagination;

    @Valid
    private SearchOrderCriteriaDto criteria;
}
