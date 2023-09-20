package com.capstone.workspace.dtos.application;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class SearchApplicationDto {
    private int page;

    private int itemsPerPage;

    private SearchApplicationCriteriaDto criteria;
}
