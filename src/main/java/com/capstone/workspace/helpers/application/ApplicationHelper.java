package com.capstone.workspace.helpers.application;

import com.capstone.workspace.entities.application.Application;
import com.capstone.workspace.enums.partner.BusinessType;
import com.capstone.workspace.enums.partner.IdentityType;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.helpers.shared.LocalDateHelper;
import com.capstone.workspace.models.application.RepresentativeModel;
import com.capstone.workspace.models.partner.PartnerModel;
import com.capstone.workspace.models.store.StoreModel;
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

    private static Logger logger = LoggerFactory.getLogger(ApplicationHelper.class);

    public void validate(Application data) {
        switch (data.getType()) {
            case CREATE_ORGANIZATION:
                Map jsonData = data.getJsonData();

                Partner partner = validatePartner(jsonData.get("organization"));
                validateStore(jsonData.get("merchant"), partner);
                validateController(jsonData.get("controller"));
                validateBankAccount(jsonData.get("bankAccount"));

                break;
        }
    }

    private Partner validatePartner(Object partnerData) {
        Partner partner = getPartner(partnerData);

        Set<ConstraintViolation<Partner>> constraintViolations = validator.validate(partner);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }

        return partner;
    }

    private Partner getPartner(Object partnerData) {
        if (partnerData == null) {
            throw new BadRequestException("Missing organization information");
        }

        LinkedHashMap partner = (LinkedHashMap) partnerData;

        String businessType = (String) partner.get("businessType");
        if (businessType == null) {
            throw new BadRequestException("Missing business type");
        }

        BusinessType businessTypeValue;
        try {
            businessTypeValue = BusinessType.valueOf(businessType);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid business type");
        }

        Class partnerClass = businessTypeValue == BusinessType.PERSONAL
                ? PersonalPartner.class
                : (businessTypeValue == BusinessType.HOUSEHOLD ? HouseholdPartner.class : EnterprisePartner.class);

        return objectMapper.convertValue(partnerData, (Class<? extends Partner>) partnerClass);
    }

    private List<Store> validateStore(Object storeData, Partner partner) {
        if (storeData == null) {
            throw new BadRequestException("Missing store information");
        }

        List<Store> storeList = new ArrayList<>();
        for (Object store: (ArrayList) storeData) {
            storeList.add(mapper.map(store, Store.class));
        }

        if (storeList.isEmpty() || (partner.getBusinessType() != BusinessType.ENTERPRISE && storeList.size() > 1)) {
            throw new BadRequestException("Your business should have one store");
        }

        for (Store store: storeList) {
            Set<ConstraintViolation<Store>> constraintViolations = validator.validate(store);
            if (!constraintViolations.isEmpty()) {
                throw new ConstraintViolationException(constraintViolations);
            }
        }

        return storeList;
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
        BankAccount bankAccount = getBankAccount(bankAccountData);

        Set<ConstraintViolation<BankAccount>> constraintViolations = validator.validate(bankAccount);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }

        return bankAccount;
    }

    public PartnerModel getPartnerFromOrg(Object partnerData) {
        Partner partner = getPartner(partnerData);

        String businessName;
        try {
            businessName = ((HouseholdPartner) partner).getBusinessName();
        } catch (Exception e) {
            businessName = null;
        }

        PartnerModel model = objectMapper.convertValue(partner, PartnerModel.class);
        model.setName(businessName);
        model.setType(partner.getBusinessType());

        return model;
    }

    public RepresentativeModel getRepresentativeFromOrg(Object partnerData) {
        Partner partner = getPartner(partnerData);
        return mapper.map(partner, RepresentativeModel.class);
    }

    public BankAccount getBankAccount(Object bankAccountData) {
        if (bankAccountData == null) {
            throw new BadRequestException("Missing bank account information");
        }

        return mapper.map(bankAccountData, BankAccount.class);
    }

    public Controller getController(Object controllerData) {
        if (controllerData != null) {
            return mapper.map(controllerData, Controller.class);
        }

        return new Controller();
    }

    public List<StoreModel> getStores(Object storeData) {
        if (storeData == null) {
            throw new BadRequestException("Missing store information");
        }

        List<StoreModel> storeList = new ArrayList<>();
        for (Object store: (ArrayList) storeData) {
            storeList.add(mapper.map(store, StoreModel.class));
        }

        return storeList;
    }
}

@Data
@NoArgsConstructor
abstract class Partner {
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
            return !identityIssueDate.isAfter(BeanHelper.getBean(LocalDateHelper.class).getLocalDateAtZoneRequest());
        }catch (NullPointerException nullPointerException){
            return false;
        }
    }
}

@Data
@NoArgsConstructor
class PersonalPartner extends Partner {
    @NotNull
    @NotBlank
    private String identityFrontImage;

    @NotNull
    @NotBlank
    private String identityBackImage;
}

@Data
@NoArgsConstructor
class HouseholdPartner extends Partner {
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
    protected List<String> businessIdentityImages;

    @AssertTrue(
            message = "businessIdentityIssueDate must be in the past"
    )
    public boolean isValidBusinessIdentityIssueDate() {
        try {
            return !businessIdentityIssueDate.isAfter(BeanHelper.getBean(LocalDateHelper.class).getLocalDateAtZoneRequest());
        }catch (NullPointerException nullPointerException){
            return false;
        }
    }
}

@Data
@NoArgsConstructor
class EnterprisePartner extends HouseholdPartner {
}

@Data
@NoArgsConstructor
class Store {
    @NotNull
    @NotBlank
    protected String name;

    @Pattern(regexp = "^\\d+$")
    protected String phone;

    @Email
    protected String email;

    @NotNull
    @NotBlank
    protected String address;

    @NotNull
    @NotEmpty
    protected List<String> merchantImages;

    @NotNull
    @NotEmpty
    protected List<String> menuImages;
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
    private List<String> identityImages;
}