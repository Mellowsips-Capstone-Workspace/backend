package com.capstone.workspace.controllers.dashboard;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.annotations.RequiredPartner;
import com.capstone.workspace.dtos.dashboard.GetDashboardStatisticDto;
import com.capstone.workspace.dtos.product.SearchProductPurchasesDto;
import com.capstone.workspace.dtos.shared.PaginationDto;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.product.ProductModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.services.dashboard.DashboardService;
import com.capstone.workspace.services.product.ProductService;
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

    @NonNull
    private final ProductService productService;

    @PostMapping("/business/statistics")
    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER})
    @RequiredPartner
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
        PaginationResponseModel<StoreModel> data = dashboardService.getStoresByKeywords(dto, new String[]{
                "milk", "tea", "trà", "sữa", "tra", "sua"
        });
        return ResponseModel.<PaginationResponseModel<StoreModel>>builder().data(data).build();
    }

    @PostMapping("/customer/coffee")
    public ResponseModel<PaginationResponseModel<StoreModel>> getCoffeeStores(@Valid @RequestBody PaginationDto dto) {
        PaginationResponseModel<StoreModel> data = dashboardService.getStoresByKeywords(dto, new String[]{
                "coffee", "cafe", "cà phê", "ca phe"
        });
        return ResponseModel.<PaginationResponseModel<StoreModel>>builder().data(data).build();
    }

    @PostMapping("/customer/favorite")
    public ResponseModel<PaginationResponseModel<ProductModel>> bestSellingProducts(@Valid @RequestBody SearchProductPurchasesDto dto) {
        PaginationResponseModel<ProductModel> data = productService.getBestSellingProducts(dto);
        return ResponseModel.<PaginationResponseModel<ProductModel>>builder().data(data).build();
    }
}
