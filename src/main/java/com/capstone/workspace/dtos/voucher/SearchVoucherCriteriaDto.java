package com.capstone.workspace.dtos.voucher;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.Map;

@Data
public class SearchVoucherCriteriaDto {
    @Valid
    private SearchVoucherFilterDto filter;

    private String keyword;

    @Valid
    private Map<String, Sort.Direction> order;
}

