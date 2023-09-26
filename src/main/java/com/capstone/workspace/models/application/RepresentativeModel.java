package com.capstone.workspace.models.application;

import com.capstone.workspace.enums.partner.IdentityType;
import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RepresentativeModel extends BaseModel {
    private String name;

    private IdentityType identityType;

    private String identityNumber;

    private LocalDate identityIssueDate;

    private String address;

    private String phone;

    private String email;

    private String identityFrontImage;

    private String identityBackImage;

    private String partnerId;
}
