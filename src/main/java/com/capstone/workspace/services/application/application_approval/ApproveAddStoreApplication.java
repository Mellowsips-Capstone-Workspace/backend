package com.capstone.workspace.services.application.application_approval;

import com.capstone.workspace.dtos.notification.PushNotificationDto;
import com.capstone.workspace.entities.application.Application;
import com.capstone.workspace.entities.store.Store;
import com.capstone.workspace.enums.application.ApplicationStatus;
import com.capstone.workspace.enums.notification.NotificationKey;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.helpers.application.ApplicationHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.repositories.application.ApplicationRepository;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.notification.NotificationService;
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
public class ApproveAddStoreApplication extends BaseApproveApplication {
    @NonNull
    private final ApplicationRepository applicationRepository;

    @NonNull
    private final IdentityService identityService;

    @NonNull
    private final ApplicationHelper applicationHelper;

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
            userIdentity.setPartnerId(application.getPartnerId());
            createStores(jsonData.get("merchant"));

            userIdentity.setUsername(approver);
            userIdentity.setUserType(UserType.ADMIN);

            // Application
            application.setStatus(ApplicationStatus.APPROVED);
            application.setPartnerId(String.valueOf(userIdentity.getPartnerId()));
            application.setApprovedBy(approver);
            application.setApprovedAt(Instant.now());
            applicationRepository.save(application);

            pushNotification(application, null);
        } catch (Exception e) {
            pushNotification(application, e);
            throw e;
        }
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
                .key(String.valueOf(NotificationKey.APPROVE_ADD_STORE_APPLICATION_SUCCESS))
                .receivers(List.of(application.getCreatedBy(), approver))
                .subject("Yêu cầu tạo mới cửa hàng đã được chấp nhận")
                .shortDescription("Bây giờ bạn có thế thiết lập cho các cửa hàng mới")
                .content("Yêu cầu tạo mới cửa hàng của bạn đã được chấp thuận. Bây giờ bạn có thế thiết lập cho các cửa hàng.")
                .metadata(new HashMap<>(){{
                    put("applicationId", application.getId());
                }})
                .build();
        } else  {
            dto = PushNotificationDto.builder()
                .key(String.valueOf(NotificationKey.APPROVE_ADD_STORE_APPLICATION_FAILED))
                .receivers(List.of(approver))
                .subject("Tạo cửa hàng thất bại")
                .shortDescription("Xảy ra lỗi trong quá trình tạo mới cửa hàng")
                .content("Xảy ra lỗi trong quá trình tạo mới cửa hàng")
                .metadata(new HashMap<>(){{
                    put("error", e.getMessage());
                }})
                .build();
        }
        notificationService.createPrivateNotification(dto);
    }
}
