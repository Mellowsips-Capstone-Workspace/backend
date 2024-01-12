package com.capstone.workspace.dtos.partner;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.Map;

@Data
public class SearchPartnerCriteriaDto {
    @Valid
    private SearchPartnerFilterDto filter;

    private String keyword;

    @Valid
    private Map<String, Sort.Direction> order;
}
