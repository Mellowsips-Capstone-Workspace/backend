package com.capstone.workspace.controllers.dashboard;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.dashboard.GetDashboardStatisticDto;
import com.capstone.workspace.dtos.shared.PaginationDto;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.services.dashboard.DashboardService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping( "/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    @NonNull
    private final DashboardService dashboardService;

    @PostMapping("/business/statistics")
    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER})
    public ResponseModel<Map<String, Object>> getBusinessStatistics(@Valid @RequestBody GetDashboardStatisticDto dto) throws ExecutionException, InterruptedException {
        Map<String, Object> data = dashboardService.getBusinessStatistics(dto);
        return ResponseModel.<Map<String, Object>>builder().data(data).build();
    }

    @PostMapping("/system/statistics")
    @AllowedUsers(userTypes = {UserType.ADMIN})
    public ResponseModel<Map<String, Object>> getSystemStatistics(@Valid @RequestBody GetDashboardStatisticDto dto) throws ExecutionException, InterruptedException {
        Map<String, Object> data = dashboardService.getSystemStatistics(dto);
        return ResponseModel.<Map<String, Object>>builder().data(data).build();
    }

    @PostMapping("/customer/hot-deals")
    public ResponseModel<PaginationResponseModel<StoreModel>> getHotDealStores(@Valid @RequestBody PaginationDto dto) {
        PaginationResponseModel<StoreModel> data = dashboardService.getHotDealStores(dto);
        return ResponseModel.<PaginationResponseModel<StoreModel>>builder().data(data).build();
    }

    @PostMapping("/customer/quality-stores")
    public ResponseModel<PaginationResponseModel<StoreModel>> getQualityStores(@Valid @RequestBody PaginationDto dto) {
        PaginationResponseModel<StoreModel> data = dashboardService.getQualityStores(dto);
        return ResponseModel.<PaginationResponseModel<StoreModel>>builder().data(data).build();
    }

    @PostMapping("/customer/milk-tea")
    public ResponseModel<PaginationResponseModel<StoreModel>> getMilkTeaStores(@Valid @RequestBody PaginationDto dto) {
        PaginationResponseModel<StoreModel> data = dashboardService.getMilkTeaStores(dto);
        return ResponseModel.<PaginationResponseModel<StoreModel>>builder().data(data).build();
    }
}
