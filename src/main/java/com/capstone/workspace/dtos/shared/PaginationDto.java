package com.capstone.workspace.dtos.shared;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PaginationDto {
    @Min(1)
    private int page;

    @Min(1)
    private int itemsPerPage;
}
