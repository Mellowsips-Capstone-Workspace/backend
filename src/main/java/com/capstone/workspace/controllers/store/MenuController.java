package com.capstone.workspace.controllers.store;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.store.CreateMenuDto;
import com.capstone.workspace.entities.store.Menu;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.models.store.MenuModel;
import com.capstone.workspace.services.store.MenuService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/menus")
@RequiredArgsConstructor
public class MenuController {
    @NonNull
    private final MenuService service;

    @NonNull
    private ModelMapper mapper;

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER, UserType.STAFF})
    @PostMapping
    public ResponseModel<MenuModel> create(@Valid @RequestBody CreateMenuDto dto){
        Menu menu = service.create(dto);
        MenuModel model = mapper.map(menu, MenuModel.class);
        return ResponseModel.<MenuModel>builder().data(model).build();
    }

}
