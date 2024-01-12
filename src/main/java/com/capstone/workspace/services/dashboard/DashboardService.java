package com.capstone.workspace.services.dashboard;

import com.capstone.workspace.dtos.dashboard.GetDashboardStatisticDto;
import com.capstone.workspace.dtos.shared.PaginationDto;
import com.capstone.workspace.entities.voucher.Voucher;
import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.dashboard.AmountModel;
import com.capstone.workspace.models.dashboard.AmountStoreModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.models.store.StoreReviewStatisticsModel;
import com.capstone.workspace.models.voucher.VoucherModel;
import com.capstone.workspace.repositories.order.OrderRepository;
import com.capstone.workspace.repositories.store.StoreRepository;
import com.capstone.workspace.repositories.voucher.VoucherOrderRepository;
import com.capstone.workspace.repositories.voucher.VoucherRepository;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.store.ReviewService;
import com.capstone.workspace.services.voucher.VoucherService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
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

    @NonNull
    private final StoreRepository storeRepository;

    @NonNull
    private final VoucherService voucherService;

    @NonNull
    private final ReviewService reviewService;

    @NonNull
    private final ModelMapper mapper;

    public Map<String, Object> getBusinessStatistics(GetDashboardStatisticDto dto) throws ExecutionException, InterruptedException {
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

        futures.add(
            executorService.submit(() -> {
                AmountModel model = voucherOrderRepository.sumAmountOfSystemUsedByBusiness(
                    userIdentity.getPartnerId(),
                    storeId,
                    new OrderStatus[]{OrderStatus.ORDERED, OrderStatus.PENDING},
                    dto.getStartDate() != null ? convertToInstant(dto.getStartDate().atStartOfDay()) : null,
                    dto.getEndDate() != null ? convertToInstant(dto.getEndDate().atStartOfDay().plusSeconds(86400L)) : null
                );
                result.put("pendingSystemVoucherAmount", model != null ? model.getAmount() : 0L);
                return null;
            })
        );

        futures.add(
            executorService.submit(() -> {
                AmountModel model = voucherOrderRepository.sumAmountOfSystemUsedByBusiness(
                    userIdentity.getPartnerId(),
                    storeId,
                    new OrderStatus[]{OrderStatus.PROCESSING, OrderStatus.COMPLETED, OrderStatus.RECEIVED, OrderStatus.DECLINED},
                    dto.getStartDate() != null ? convertToInstant(dto.getStartDate().atStartOfDay()) : null,
                    dto.getEndDate() != null ? convertToInstant(dto.getEndDate().atStartOfDay().plusSeconds(86400L)) : null
                );
                result.put("usedSystemVoucherAmount", model != null ? model.getAmount() : 0L);
                return null;
            })
        );

        if (storeId == null) {
            futures.add(
                executorService.submit(() -> {
                    List<AmountStoreModel> amountStoreModels = orderRepository.sumAmountForStore(
                        userIdentity.getPartnerId(),
                        new OrderStatus[]{OrderStatus.RECEIVED, OrderStatus.ORDERED, OrderStatus.PROCESSING, OrderStatus.COMPLETED},
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

    public Map<String, Object> getSystemStatistics(GetDashboardStatisticDto dto) throws ExecutionException, InterruptedException {
        Map<String, Object> result = new HashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<Void>> futures = new ArrayList<>();

        futures.add(
            executorService.submit(() -> {
                AmountModel model = voucherOrderRepository.sumAmountOfSystem(
                    new OrderStatus[]{OrderStatus.PROCESSING, OrderStatus.COMPLETED, OrderStatus.RECEIVED, OrderStatus.DECLINED},
                    dto.getStartDate() != null ? convertToInstant(dto.getStartDate().atStartOfDay()) : null,
                    dto.getEndDate() != null ? convertToInstant(dto.getEndDate().atStartOfDay().plusSeconds(86400L)) : null
                );
                result.put("usedVoucherAmount", model != null ? model.getAmount() : 0L);
                return null;
            })
        );

        futures.add(
            executorService.submit(() -> {
                AmountModel model = voucherOrderRepository.sumAmountOfSystem(
                    new OrderStatus[]{OrderStatus.ORDERED, OrderStatus.PENDING},
                    dto.getStartDate() != null ? convertToInstant(dto.getStartDate().atStartOfDay()) : null,
                    dto.getEndDate() != null ? convertToInstant(dto.getEndDate().atStartOfDay().plusSeconds(86400L)) : null
                );
                result.put("pendingVoucherAmount", model != null ? model.getAmount() : 0L);
                return null;
            })
        );

        for (Future<Void> future: futures) {
            future.get();
        }

        return result;
    }

    public PaginationResponseModel<StoreModel> getHotDealStores(PaginationDto dto) {
        PaginationResponseModel<StoreModel> data = storeRepository.getHotDealStores(dto);

        if (data.getResults() != null) {
            data.getResults().forEach(StoreModel::loadData);
        }

        return data;
    }

    public PaginationResponseModel<StoreModel> getQualityStores(PaginationDto dto) {
        PaginationResponseModel<StoreModel> data = storeRepository.getQualityStores(dto);

        if (data.getResults() != null) {
            data.getResults().forEach(StoreModel::loadData);
        }

        return data;
    }

    public PaginationResponseModel<StoreModel> getStoresByKeywords(PaginationDto dto, String[] keywords) {
        PaginationResponseModel<StoreModel> data = storeRepository.getStoresByKeywords(dto, keywords);

        if (data.getResults() != null) {
            data.getResults().forEach(StoreModel::loadData);
        }

        return data;
    }
}
