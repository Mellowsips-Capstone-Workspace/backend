package com.capstone.workspace.dtos.shared;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class PaginationDto {
    @Min(1)
    private int page;

    @Min(1)
    private int itemsPerPage;
}
