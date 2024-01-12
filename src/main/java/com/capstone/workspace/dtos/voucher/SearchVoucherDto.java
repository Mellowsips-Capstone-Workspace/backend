package com.capstone.workspace.dtos.voucher;

import com.capstone.workspace.dtos.shared.PaginationDto;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class SearchVoucherDto {
    @Valid
    private PaginationDto pagination;

    @Valid
    private SearchVoucherCriteriaDto criteria;
}
