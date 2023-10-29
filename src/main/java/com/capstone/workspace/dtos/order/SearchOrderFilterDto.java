package com.capstone.workspace.dtos.order;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class SearchOrderFilterDto {
    private Object status;
}
