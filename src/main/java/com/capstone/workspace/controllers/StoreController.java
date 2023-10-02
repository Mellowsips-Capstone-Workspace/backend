package com.capstone.workspace.controllers;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.store.SearchStoreDto;
import com.capstone.workspace.entities.store.Menu;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.models.store.MenuModel;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.services.store.MenuService;
import com.capstone.workspace.services.store.StoreService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping( "/api/stores")
@RequiredArgsConstructor
public class StoreController {
    @NonNull
    private final StoreService storeService;

    @NonNull
    private final MenuService menuService;

    @NonNull
    private final ModelMapper mapper;

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

    @GetMapping("/{id}/menu")
    public ResponseModel<MenuModel> getStoreMenu(@PathVariable(name = "id") String storeId) {
        Menu entity = menuService.getStoreMenu(storeId);
        MenuModel model = mapper.map(entity, MenuModel.class);
        return ResponseModel.<MenuModel>builder().data(model).build();
    }
}
