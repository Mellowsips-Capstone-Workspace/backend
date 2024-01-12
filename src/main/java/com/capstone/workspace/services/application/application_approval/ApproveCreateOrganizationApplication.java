package com.capstone.workspace.services.application.application_approval;

import com.capstone.workspace.dtos.notification.PushNotificationDto;
import com.capstone.workspace.entities.application.Application;
import com.capstone.workspace.entities.application.BankAccount;
import com.capstone.workspace.entities.application.Controller;
import com.capstone.workspace.entities.application.Representative;
import com.capstone.workspace.entities.partner.Partner;
import com.capstone.workspace.entities.store.Store;
import com.capstone.workspace.enums.application.ApplicationStatus;
import com.capstone.workspace.enums.notification.NotificationKey;
import com.capstone.workspace.enums.user.UserType;
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
import com.capstone.workspace.services.notification.NotificationService;
import com.capstone.workspace.services.partner.PartnerService;
import com.capstone.workspace.services.store.StoreService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApproveCreateOrganizationApplication extends BaseApproveApplication {
    @NonNull
    private final ApplicationRepository applicationRepository;

    @NonNull
    private final IdentityService identityService;

    @NonNull
    private final ApplicationHelper applicationHelper;

    @NonNull
    private final PartnerService partnerService;

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

    @NonNull
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void execute(Application application) {
        try {
            UserIdentity userIdentity = identityService.getUserIdentity();
            String approver = userIdentity.getUsername();

            String creator = application.getCreatedBy();
            userIdentity.setUsername(creator);
            userIdentity.setUserType(UserType.OWNER);

            Map jsonData = application.getJsonData();
            Partner partner = createPartner(jsonData.get("organization"));

            userIdentity.setPartnerId(String.valueOf(partner.getId()));

            // Create bank account, representative, invoice controller, stores
            createBankAccount(jsonData.get("bankAccount"));
            createRepresentative(jsonData.get("organization"));
            createInvoiceController(jsonData.get("controller"));
            createStores(jsonData.get("merchant"));

            userIdentity.setUsername(approver);
            userIdentity.setUserType(UserType.ADMIN);

            // Application
            application.setStatus(ApplicationStatus.APPROVED);
            application.setPartnerId(String.valueOf(partner.getId()));
            application.setApprovedBy(approver);
            application.setApprovedAt(Instant.now());
            applicationRepository.save(application);

            // Add user to group
            authService.addUserToGroup(String.valueOf(partner.getId()), creator);

            pushNotification(application, null);
        } catch (Exception e) {
            pushNotification(application, e);
            throw e;
        }
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

    private void pushNotification(Application application, Exception e) {
        PushNotificationDto dto;
        UserIdentity userIdentity = identityService.getUserIdentity();
        String approver = userIdentity.getUsername();

        if (e == null) {
            dto = PushNotificationDto.builder()
                .key(String.valueOf(NotificationKey.APPROVE_CREATE_ORGANIZATION_APPLICATION_SUCCESS))
                .receivers(List.of(application.getCreatedBy(), approver))
                .subject("Yêu cầu tạo doanh nghiệp đã được chấp nhận")
                .shortDescription("Bây giờ bạn có thế thiết lập cho các cửa hàng")
                .content("Yêu cầu tạo doanh nghiệp của bạn đã được chấp thuận. Bây giờ bạn có thế thiết lập cho các cửa hàng.")
                .metadata(new HashMap<>(){{
                    put("applicationId", application.getId());
                }})
                .build();
        } else  {
            dto = PushNotificationDto.builder()
                .key(String.valueOf(NotificationKey.APPROVE_CREATE_ORGANIZATION_APPLICATION_FAILED))
                .receivers(List.of(approver))
                .subject("Tạo doanh nghiệp thất bại")
                .shortDescription("Xảy ra lỗi trong quá trình tạo doanh nghiệp")
                .content("Xảy ra lỗi trong quá trình tạo doanh nghiệp")
                .metadata(new HashMap<>(){{
                    put("error", e.getMessage());
                }})
                .build();
        }
        notificationService.createPrivateNotification(dto);
    }
}
