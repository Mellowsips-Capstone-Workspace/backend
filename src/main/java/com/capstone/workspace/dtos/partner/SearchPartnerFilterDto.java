package com.capstone.workspace.dtos.partner;

import com.capstone.workspace.enums.partner.BusinessType;
import lombok.Data;

@Data
public class SearchPartnerFilterDto {
    private BusinessType type;
}
