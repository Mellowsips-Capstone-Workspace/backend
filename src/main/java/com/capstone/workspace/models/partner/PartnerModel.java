package com.capstone.workspace.models.partner;

import com.capstone.workspace.enums.partner.BusinessType;
import com.capstone.workspace.models.shared.BaseModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PartnerModel extends BaseModel {
    private String name;

    private String logo;

    private String businessCode;

    private String taxCode;

    private BusinessType type;

    @JsonFormat(pattern="dd/MM/yyyy")
    private LocalDate businessIdentityIssueDate;

    private List<String> businessIdentityImages;
}
