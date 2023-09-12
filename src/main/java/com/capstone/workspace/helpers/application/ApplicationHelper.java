package com.capstone.workspace.helpers.application;

import com.capstone.workspace.dtos.application.CreateApplicationDto;
import com.capstone.workspace.enums.organization.BusinessType;
import com.capstone.workspace.enums.organization.IdentityType;
import com.capstone.workspace.exceptions.BadRequestException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.*;

@Component
@Validated
@RequiredArgsConstructor
public class ApplicationHelper {
    @NonNull
    private final ModelMapper mapper;

    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private final Validator validator;

    private Logger logger = LoggerFactory.getLogger(ApplicationHelper.class);

    public void validate(CreateApplicationDto dto) {
        switch (dto.getType()) {
            case CREATE_ORGANIZATION:
                Map jsonData = dto.getJsonData();

                Organization organization = validateOrganization(jsonData.get("organization"));
                validateMerchant(jsonData.get("merchant"), organization);
                validateController(jsonData.get("controller"));
                validateBankAccount(jsonData.get("bankAccount"));

                break;
        }
    }

    private Organization validateOrganization(Object organizationData) {
        if (organizationData == null) {
            throw new BadRequestException("Missing organization information");
        }

        LinkedHashMap organization = (LinkedHashMap) organizationData;

        String businessType = (String) organization.get("businessType");
        if (businessType == null) {
            throw new BadRequestException("Missing business type");
        }

        BusinessType businessTypeValue;
        try {
            businessTypeValue = BusinessType.valueOf(businessType);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid business type");
        }

        Class organizationClass = businessTypeValue == BusinessType.PERSONAL
                ? PersonalOrganization.class
                : (businessTypeValue == BusinessType.HOUSEHOLD ? HouseholdOrganization.class : EnterpriseOrganization.class);
        Organization data = objectMapper.convertValue(organizationData, (Class<? extends Organization>) organizationClass);

        Set<ConstraintViolation<Organization>> constraintViolations = validator.validate(data);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }

        return data;
    }

    private List<Merchant> validateMerchant(Object merchantData, Organization organizationData) {
        if (merchantData == null) {
            throw new BadRequestException("Missing merchant information");
        }

        List<Merchant> merchantList = new ArrayList<>();
        for (Object merchant: (ArrayList) merchantData) {
            merchantList.add(mapper.map(merchant, Merchant.class));
        }

        if (merchantList.isEmpty() || (organizationData.getBusinessType() != BusinessType.ENTERPRISE && merchantList.size() > 1)) {
            throw new BadRequestException("Your business should have one merchant");
        }

        for (Merchant merchant: merchantList) {
            Set<ConstraintViolation<Merchant>> constraintViolations = validator.validate(merchant);
            if (!constraintViolations.isEmpty()) {
                throw new ConstraintViolationException(constraintViolations);
            }
        }

        return merchantList;
    }

    private Controller validateController(Object controllerData) {
        if (controllerData != null) {
            Controller controller = mapper.map(controllerData, Controller.class);

            Set<ConstraintViolation<Controller>> constraintViolations = validator.validate(controller);
            if (!constraintViolations.isEmpty()) {
                throw new ConstraintViolationException(constraintViolations);
            }

            return controller;
        }

        return null;
    }

    private BankAccount validateBankAccount(Object bankAccountData) {
        if (bankAccountData == null) {
            throw new BadRequestException("Missing bank account information");
        }

        BankAccount bankAccount = mapper.map(bankAccountData, BankAccount.class);

        Set<ConstraintViolation<BankAccount>> constraintViolations = validator.validate(bankAccount);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }

        return bankAccount;
    }
}

@Data
@NoArgsConstructor
abstract class Organization {
    @NotNull
    protected BusinessType businessType;

    @NotNull
    @NotBlank
    protected String name;

    @NotNull
    protected IdentityType identityType;

    @NotNull
    @NotBlank
    protected String identityNumber;

    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    protected LocalDate identityIssueDate;

    @NotNull
    @NotBlank
    protected String address;

    @NotNull
    @NotBlank
    @Pattern(regexp = "(84[3|5|7|8|9])+(\\d{8})\\b")
    protected String phone;

    @NotNull
    @NotBlank
    @Email
    protected String email;

    @AssertTrue(
        message = "identityIssueDate must be in the past"
    )
    public boolean isValidIdentityIssueDate() {
        try {
            return !identityIssueDate.isAfter(LocalDate.now());
        }catch (NullPointerException nullPointerException){
            return false;
        }
    }
}

@Data
@NoArgsConstructor
class PersonalOrganization extends Organization {
    @NotNull
    @NotBlank
    private String identityFrontImage;

    @NotNull
    @NotBlank
    private String identityBackImage;
}

@Data
@NoArgsConstructor
class HouseholdOrganization extends Organization {
    @NotNull
    @NotBlank
    protected String businessName;

    @NotNull
    @NotBlank
    protected String taxCode;

    @NotNull
    @NotBlank
    protected String businessCode;

    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    protected LocalDate businessIdentityIssueDate;

    @NotNull
    @NotEmpty
    protected Set<String> businessIdentityImages;

    @AssertTrue(
            message = "businessIdentityIssueDate must be in the past"
    )
    public boolean isValidBusinessIdentityIssueDate() {
        try {
            return !businessIdentityIssueDate.isAfter(LocalDate.now());
        }catch (NullPointerException nullPointerException){
            return false;
        }
    }
}

@Data
@NoArgsConstructor
class EnterpriseOrganization extends HouseholdOrganization {
}

@Data
@NoArgsConstructor
class Merchant {
    @NotNull
    @NotBlank
    protected String name;

    @Pattern(regexp = "(84[3|5|7|8|9])+(\\d{8})\\b")
    protected String phone;

    @Email
    protected String email;

    @NotNull
    @NotBlank
    protected String address;

    @NotNull
    @NotEmpty
    protected Set<String> merchantImages;

    @NotNull
    @NotEmpty
    protected Set<String> menuImages;
}

@Data
@NoArgsConstructor
class Controller {
    private String name;

    @Pattern(regexp = "(84[3|5|7|8|9])+(\\d{8})\\b")
    private String phone;

    @Email
    private String email;
}

@Data
@NoArgsConstructor
class BankAccount {
    @NotNull
    @NotBlank
    private String bankName;

    @NotNull
    @NotBlank
    private String bankBranch;

    @NotNull
    @NotBlank
    private String accountName;

    @NotNull
    @NotBlank
    private String accountNumber;

    @NotNull
    @NotEmpty
    private Set<String> identityImages;
}