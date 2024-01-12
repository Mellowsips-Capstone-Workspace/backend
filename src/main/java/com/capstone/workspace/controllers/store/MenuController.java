package com.capstone.workspace.controllers.store;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.annotations.RequiredPartner;
import com.capstone.workspace.dtos.store.*;
import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.entities.store.Menu;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.product.ProductModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.models.store.MenuDetailsModel;
import com.capstone.workspace.models.store.MenuModel;
import com.capstone.workspace.services.product.ProductService;
import com.capstone.workspace.services.store.MenuService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping( "/api/menus")
@RequiredArgsConstructor
public class MenuController {
    @NonNull
    private final MenuService menuService;

    @NonNull
    private final ModelMapper mapper;

    @NonNull
    private final ProductService productService;

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER})
    @RequiredPartner
    @PostMapping
    public ResponseModel<MenuModel> create(@Valid @RequestBody CreateMenuDto dto) {
        Menu entity = menuService.create(dto);
        MenuModel model = mapper.map(entity, MenuModel.class);
        return ResponseModel.<MenuModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER, UserType.STAFF})
    @RequiredPartner
    @GetMapping("/{id}")
    public ResponseModel<MenuDetailsModel> getMenuById(@PathVariable UUID id) {
        Menu entity = menuService.getMenuById(id);
        MenuDetailsModel model = mapper.map(entity, MenuDetailsModel.class);
        return ResponseModel.<MenuDetailsModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER, UserType.STAFF})
    @RequiredPartner
    @PostMapping("/search")
    public ResponseModel<PaginationResponseModel<MenuModel>> search(@Valid @RequestBody SearchMenuDto dto) {
        PaginationResponseModel<MenuModel> data = menuService.search(dto);
        return ResponseModel.<PaginationResponseModel<MenuModel>>builder().data(data).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER})
    @RequiredPartner
    @PutMapping("/{id}")
    public ResponseModel<MenuModel> update(@PathVariable UUID id, @Valid @RequestBody UpdateMenuDto dto) {
        Menu menu = menuService.update(id, dto);
        MenuModel model = mapper.map(menu, MenuModel.class);
        return ResponseModel.<MenuModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER})
    @RequiredPartner
    @DeleteMapping("/{id}")
    public ResponseModel delete(@PathVariable UUID id) {
        menuService.delete(id);
        return ResponseModel.<MenuModel>builder().message("Delete menu successfully").build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER})
    @RequiredPartner
    @GetMapping("/{id}/products")
    public ResponseModel<List<ProductModel>> getMenuProducts(@PathVariable UUID id) {
        List<Product> entities = productService.getMenuProducts(id);
        List<ProductModel> models = mapper.map(entities, new TypeToken<List<ProductModel>>() {}.getType());
        return ResponseModel.<List<ProductModel>>builder().data(models).build();
    }
}
