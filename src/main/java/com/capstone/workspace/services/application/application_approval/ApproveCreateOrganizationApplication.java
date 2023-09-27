package com.capstone.workspace.services.application.application_approval;

import com.capstone.workspace.dtos.user.CreateRoleDto;
import com.capstone.workspace.entities.application.Application;
import com.capstone.workspace.entities.application.BankAccount;
import com.capstone.workspace.entities.application.Controller;
import com.capstone.workspace.entities.application.Representative;
import com.capstone.workspace.entities.partner.Partner;
import com.capstone.workspace.entities.store.Store;
import com.capstone.workspace.entities.user.Permission;
import com.capstone.workspace.entities.user.Role;
import com.capstone.workspace.enums.application.ApplicationStatus;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.helpers.application.ApplicationHelper;
import com.capstone.workspace.models.application.RepresentativeModel;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.partner.PartnerModel;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.repositories.application.ApplicationRepository;
import com.capstone.workspace.services.application.bank_account.BankAccountService;
import com.capstone.workspace.services.application.controller.ControllerService;
import com.capstone.workspace.services.application.representative.RepresentativeService;
import com.capstone.workspace.services.auth.AuthService;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.partner.PartnerService;
import com.capstone.workspace.services.store.StoreService;
import com.capstone.workspace.services.user.PermissionService;
import com.capstone.workspace.services.user.RoleService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApproveCreateOrganizationApplication extends BaseApproveApplication {
    private static final Logger logger = LoggerFactory.getLogger(ApproveCreateOrganizationApplication.class);

    @NonNull
    private final ApplicationRepository applicationRepository;

    @NonNull
    private final IdentityService identityService;

    @NonNull
    private final ApplicationHelper applicationHelper;

    @NonNull
    private final PartnerService partnerService;

    @NonNull
    private final PermissionService permissionService;

    @NonNull
    private final RoleService roleService;

    @NonNull
    private final AuthService authService;

    @NonNull
    private final BankAccountService bankAccountService;

    @NonNull
    private final RepresentativeService representativeService;

    @NonNull
    private final ControllerService controllerService;

    @NonNull
    private final StoreService storeService;

    @Override
    @Transactional
    public void execute(Application application) {
        UserIdentity userIdentity = identityService.getUserIdentity();
        String approver = userIdentity.getUsername();

        String creator = application.getCreatedBy();
        userIdentity.setUsername(creator);

        Map jsonData = application.getJsonData();
        Partner partner = createPartner(jsonData.get("organization"));

        userIdentity.setPartnerId(String.valueOf(partner.getId()));

        // Create bank account, representative, invoice controller, stores
        createBankAccount(jsonData.get("bankAccount"));
        createRepresentative(jsonData.get("organization"));
        createInvoiceController(jsonData.get("controller"));
        createStores(jsonData.get("merchant"));

        userIdentity.setUsername(approver);

        // Create "owner" role
        List<Permission> permissions = permissionService.getAll();
        CreateRoleDto createRoleDto = CreateRoleDto.builder()
                .isAllowedEdit(false)
                .name("Owner")
                .description("Default owner role with full permissions")
                .permissions(permissions.stream().map(p -> p.getId()).toList())
                .build();
        Role ownerRole = roleService.create(createRoleDto);

        // Assign application creator to owner role
        if (creator == null) {
            throw new BadRequestException("Application missing creator");
        }
        roleService.assignUserToRole(ownerRole.getId(), creator);

        // Application
        application.setStatus(ApplicationStatus.APPROVED);
        application.setPartnerId(String.valueOf(partner.getId()));
        application.setApprovedBy(approver);
        application.setApprovedAt(Instant.now());
        applicationRepository.save(application);

        // Add user to group
        authService.addUserToGroup(String.valueOf(partner.getId()), creator);
    }

    private Partner createPartner(Object data) {
        PartnerModel model = applicationHelper.getPartnerFromOrg(data);
        return partnerService.create(model);
    }

    private BankAccount createBankAccount(Object data) {
        Object model = applicationHelper.getBankAccount(data);
        return bankAccountService.create(model);
    }

    private Representative createRepresentative(Object data) {
        RepresentativeModel model = applicationHelper.getRepresentativeFromOrg(data);
        return representativeService.create(model);
    }

    private Controller createInvoiceController(Object data) {
        Object model = applicationHelper.getController(data);
        return controllerService.create(model);
    }

    private List<Store> createStores(Object data) {
        List<StoreModel> stores = applicationHelper.getStores(data);
        return storeService.createBulk(stores);
    }
}
