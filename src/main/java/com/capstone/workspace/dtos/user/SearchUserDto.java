package com.capstone.workspace.dtos.user;

import com.capstone.workspace.dtos.shared.PaginationDto;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class SearchUserDto {
    @Valid
    private SearchUserCriteriaDto criteria;

    @Valid
    private PaginationDto pagination;
}
