package com.capstone.workspace.services.dashboard;

import com.capstone.workspace.dtos.dashboard.GetDashboardStatisticDto;
import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.dashboard.AmountModel;
import com.capstone.workspace.models.dashboard.AmountStoreModel;
import com.capstone.workspace.repositories.order.OrderRepository;
import com.capstone.workspace.repositories.voucher.VoucherOrderRepository;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class DashboardService {
    @NonNull
    private final IdentityService identityService;

    @NonNull
    private final OrderRepository orderRepository;

    @NonNull
    private final VoucherOrderRepository voucherOrderRepository;

    public Map<String, Object> getStatistics(GetDashboardStatisticDto dto) throws ExecutionException, InterruptedException {
        Map<String, Object> result = new HashMap<>();
        UserIdentity userIdentity = identityService.getUserIdentity();
        String storeId = userIdentity.getUserType() == UserType.OWNER && dto.getStoreId() != null ? dto.getStoreId() : userIdentity.getStoreId();


        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<Void>> futures = new ArrayList<>();

        futures.add(
            executorService.submit(
                getOrderStatistic(
                    result,
                    dto,
                    userIdentity.getPartnerId(),
                    storeId,
                    new OrderStatus[]{OrderStatus.RECEIVED},
                    "successOrderAmount"
                )
            )
        );
        futures.add(
            executorService.submit(
                getOrderStatistic(
                    result,
                    dto,
                    userIdentity.getPartnerId(),
                    storeId,
                    new OrderStatus[]{OrderStatus.DECLINED},
                    "flakedOrderAmount"
                )
            )
        );
        futures.add(
            executorService.submit(
                getOrderStatistic(
                    result,
                    dto,
                    userIdentity.getPartnerId(),
                    storeId,
                    new OrderStatus[]{OrderStatus.ORDERED, OrderStatus.PROCESSING, OrderStatus.COMPLETED},
                    "pendingOrderAmount"
                )
            )
        );

        futures.add(
            executorService.submit(
                getVoucherStatistic(
                    result,
                    dto,
                    userIdentity.getPartnerId(),
                    storeId,
                    new OrderStatus[]{OrderStatus.ORDERED, OrderStatus.PENDING},
                    "pendingVoucherAmount"
                )
            )
        );

        futures.add(
            executorService.submit(
                getVoucherStatistic(
                    result,
                    dto,
                    userIdentity.getPartnerId(),
                    storeId,
                    new OrderStatus[]{OrderStatus.PROCESSING, OrderStatus.COMPLETED, OrderStatus.RECEIVED, OrderStatus.DECLINED},
                    "usedVoucherAmount"
                )
            )
        );

        if (storeId == null) {
            futures.add(
                executorService.submit(() -> {
                    List<AmountStoreModel> amountStoreModels = orderRepository.sumAmountForStore(
                        userIdentity.getPartnerId(),
                        new OrderStatus[]{OrderStatus.RECEIVED},
                        dto.getStartDate() != null ? convertToInstant(dto.getStartDate().atStartOfDay()) : null,
                        dto.getEndDate() != null ? convertToInstant(dto.getEndDate().atStartOfDay().plusSeconds(86400L)) : null
                    );
                    result.put("amountForStores", amountStoreModels);
                    return null;
                })
            );
        }

        for (Future<Void> future: futures) {
            future.get();
        }

        return result;
    }

    private Callable<Void> getOrderStatistic(
            Map<String, Object> result,
            GetDashboardStatisticDto dto,
            String partnerId,
            String storeId,
            OrderStatus[] statuses,
            String key
    ) {
        return () -> {
            AmountModel model = orderRepository.sumAmountByPartnerIdAndStoreIdAndInStatusesWithPeriod(
                    partnerId,
                    storeId,
                    statuses,
                    dto.getStartDate() != null ? convertToInstant(dto.getStartDate().atStartOfDay()) : null,
                    dto.getEndDate() != null ? convertToInstant(dto.getEndDate().atStartOfDay().plusSeconds(86400L)) : null
            );
            result.put(key, model != null ? model.getAmount() : 0L);
            return null;
        };
    }

    private Callable<Void> getVoucherStatistic(
            Map<String, Object> result,
            GetDashboardStatisticDto dto,
            String partnerId,
            String storeId,
            OrderStatus[] statuses,
            String key
    ) {
        return () -> {
            AmountModel model = voucherOrderRepository.sumAmountOfBusiness(
                    partnerId,
                    storeId,
                    statuses,
                    dto.getStartDate() != null ? convertToInstant(dto.getStartDate().atStartOfDay()) : null,
                    dto.getEndDate() != null ? convertToInstant(dto.getEndDate().atStartOfDay().plusSeconds(86400L)) : null
            );
            result.put(key, model != null ? model.getAmount() : 0L);
            return null;
        };
    }

    private Instant convertToInstant(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.of("+07:00"));
    }
}
