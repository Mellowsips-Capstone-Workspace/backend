package com.capstone.workspace.controllers;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.store.SearchStoreDto;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.services.store.StoreService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/stores")
@RequiredArgsConstructor
public class StoreController {
    @NonNull
    private final StoreService storeService;

    @PostMapping("/customer/search")
    public ResponseModel<PaginationResponseModel<StoreModel>> customerSearch(@Valid @RequestBody SearchStoreDto dto) {
        PaginationResponseModel<StoreModel> data = storeService.search(dto);
        return ResponseModel.<PaginationResponseModel<StoreModel>>builder().data(data).build();
    }

    @AllowedUsers(userTypes = {UserType.EMPLOYEE, UserType.ADMIN})
    @PostMapping("/search")
    public ResponseModel<PaginationResponseModel<StoreModel>> search(@Valid @RequestBody SearchStoreDto dto) {
        PaginationResponseModel<StoreModel> data = storeService.search(dto);
        return ResponseModel.<PaginationResponseModel<StoreModel>>builder().data(data).build();
    }
}
