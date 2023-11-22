package com.capstone.workspace.dtos.user;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.Map;

@Data
public class SearchUserCriteriaDto {
    @Valid
    private SearchUserFilterDto filter;

    private String keyword;

    @Valid
    private Map<String, Sort.Direction> order;
}
