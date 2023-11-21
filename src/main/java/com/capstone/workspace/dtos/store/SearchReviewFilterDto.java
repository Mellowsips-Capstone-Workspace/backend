package com.capstone.workspace.dtos.store;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SearchReviewFilterDto {
    @Min(1)
    @Max(5)
    private int point;
}
