package com.capstone.workspace.jobs.handlers;

import com.capstone.workspace.dtos.notification.PushNotificationDto;
import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.entities.user.User;
import com.capstone.workspace.enums.notification.NotificationKey;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.jobs.requests.PushNotificationOrderChangesJobRequest;
import com.capstone.workspace.repositories.user.UserRepository;
import com.capstone.workspace.services.notification.NotificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class PushNotificationOrderChangesJobRequestHandler implements JobRequestHandler<PushNotificationOrderChangesJobRequest> {
    private final static Logger logger = LoggerFactory.getLogger(PushNotificationOrderChangesJobRequestHandler.class);

    @NonNull
    private final NotificationService notificationService;

    @NonNull
    private final UserRepository userRepository;

    @Override
    @Job(name = "Push notification job when order changes")
    public void run(PushNotificationOrderChangesJobRequest request) {
        Order order = request.getOrder();
        PushNotificationDto dto = null;

        switch (order.getStatus()) {
            case ORDERED:
                dto = PushNotificationDto.builder()
                    .key(String.valueOf(NotificationKey.HAVING_NEW_ORDER))
                    .receivers(getStoreReceivers(order))
                    .subject("Bạn có đơn hàng mới")
                    .metadata(new HashMap<>(){{
                        put("orderId", order.getId());
                    }})
                    .build();
                break;
            case PROCESSING:
                dto = PushNotificationDto.builder()
                    .key(String.valueOf(NotificationKey.ORDER_PROCESSING))
                    .receivers(List.of(order.getCreatedBy()))
                    .subject("Đơn hàng của bạn đang được thực hiện")
                    .metadata(new HashMap<>(){{
                        put("orderId", order.getId());
                    }})
                    .build();
                break;
            case COMPLETED:
                dto = PushNotificationDto.builder()
                    .key(String.valueOf(NotificationKey.ORDER_COMPLETED))
                    .receivers(List.of(order.getCreatedBy()))
                    .subject("Đơn hàng của bạn đã xong")
                    .metadata(new HashMap<>(){{
                        put("orderId", order.getId());
                    }})
                    .build();
                break;
            case CANCELED:
                dto = PushNotificationDto.builder()
                    .key(String.valueOf(NotificationKey.ORDER_CANCELED))
                    .receivers(getStoreReceivers(order))
                    .subject("Đơn hàng đã bị hủy")
                    .metadata(new HashMap<>(){{
                        put("orderId", order.getId());
                    }})
                    .build();
                break;
            case REJECTED:
                dto = PushNotificationDto.builder()
                    .key(String.valueOf(NotificationKey.ORDER_REJECTED))
                    .receivers(List.of(order.getCreatedBy()))
                    .subject("Đơn hàng của bạn đã bị từ chối")
                    .content("Xin lỗi bạn vì sự bất tiện này. Vì một số lí do nên chúng tôi không thể xử lí đơn hàng này. Bạn vui lòng gọi món tại quầy.")
                    .metadata(new HashMap<>(){{
                        put("orderId", order.getId());
                    }})
                    .build();
                break;
            default:
                break;
        }

        if (dto != null) {
            logger.info("Start push notification job");
            notificationService.createPrivateNotification(dto);
            logger.info("Complete push notification job");
        }
    }

    private List<String> getStoreReceivers(Order order) {
        List<User> businessOwners = userRepository.findByPartnerIdAndType(order.getPartnerId(), UserType.OWNER);
        List<User> storeManagersAndStaffs = userRepository.findByStoreId(order.getStoreId());
        return Stream.concat(businessOwners.stream(), storeManagersAndStaffs.stream()).map(User::getUsername).toList();
    }
}
