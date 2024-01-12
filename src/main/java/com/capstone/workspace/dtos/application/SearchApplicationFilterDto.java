package com.capstone.workspace.dtos.application;

import com.capstone.workspace.enums.application.ApplicationType;
import lombok.Data;

@Data
public class SearchApplicationFilterDto {
    private ApplicationType type;
    private Object status;
}
