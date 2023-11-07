package com.capstone.workspace.controllers.store;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.store.*;
import com.capstone.workspace.entities.store.Menu;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.models.store.MenuDetailsModel;
import com.capstone.workspace.models.store.MenuModel;
import com.capstone.workspace.services.store.MenuService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping( "/api/menus")
@RequiredArgsConstructor
public class MenuController {
    @NonNull
    private final MenuService menuService;

    @NonNull
    private final ModelMapper mapper;

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER, UserType.STAFF})
    @PostMapping
    public ResponseModel<MenuModel> create(@Valid @RequestBody CreateMenuDto dto){
        Menu entity = menuService.create(dto);
        MenuModel model = mapper.map(entity, MenuModel.class);
        return ResponseModel.<MenuModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER, UserType.STAFF})
    @GetMapping("/{id}")
    public ResponseModel<MenuDetailsModel> getMenuById(@PathVariable UUID id){
        Menu entity = menuService.getMenuById(id);
        MenuDetailsModel model = mapper.map(entity, MenuDetailsModel.class);
        return ResponseModel.<MenuDetailsModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER, UserType.STAFF})
    @PostMapping("/search")
    public ResponseModel<PaginationResponseModel<MenuModel>> search(@Valid @RequestBody SearchMenuDto dto){
        PaginationResponseModel<MenuModel> data = menuService.search(dto);
        return ResponseModel.<PaginationResponseModel<MenuModel>>builder().data(data).build();
    }
}
