package com.capstone.workspace.dtos.application;

import com.capstone.workspace.enums.application.ApplicationType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class SearchApplicationFilterDto {
    private ApplicationType type;
}
