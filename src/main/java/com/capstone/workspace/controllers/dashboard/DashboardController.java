package com.capstone.workspace.controllers.dashboard;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.dashboard.GetDashboardStatisticDto;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.shared.ResponseModel;
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
    public ResponseModel<Map<String, Object>> getStatistics(@Valid @RequestBody GetDashboardStatisticDto dto) throws ExecutionException, InterruptedException {
        Map<String, Object> data = dashboardService.getStatistics(dto);
        return ResponseModel.<Map<String, Object>>builder().data(data).build();
    }
}
