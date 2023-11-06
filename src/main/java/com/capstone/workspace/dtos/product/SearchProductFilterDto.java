package com.capstone.workspace.dtos.product;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class SearchProductFilterDto {
    private Boolean isSoldOut;
}
