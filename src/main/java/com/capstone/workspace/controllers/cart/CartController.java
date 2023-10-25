package com.capstone.workspace.controllers.cart;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.cart.AddProductToCartDto;
import com.capstone.workspace.dtos.cart.UpdateCartItemDto;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.cart.CartDetailsModel;
import com.capstone.workspace.models.cart.CartItemModel;
import com.capstone.workspace.models.cart.CartModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.services.cart.CartService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping( "/api/carts")
@RequiredArgsConstructor
public class CartController {
    @NonNull
    private final CartService cartService;

    @AllowedUsers(userTypes = {UserType.CUSTOMER})
    @PostMapping
    public ResponseModel<CartItemModel> addProductToCart(@Valid @RequestBody AddProductToCartDto dto) {
        CartItemModel model = cartService.addProductToCart(dto);
        return ResponseModel.<CartItemModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.CUSTOMER})
    @GetMapping
    public ResponseModel<List<CartModel>> getAllCarts() {
        List<CartModel> data = cartService.getAllCartsByUser();
        return ResponseModel.<List<CartModel>>builder().data(data).build();
    }

    @AllowedUsers(userTypes = {UserType.CUSTOMER})
    @GetMapping("/{id}")
    public ResponseModel<CartDetailsModel> getCartDetails(@PathVariable UUID id) {
        CartDetailsModel model = cartService.getCartDetails(id);
        return ResponseModel.<CartDetailsModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.CUSTOMER})
    @PutMapping("/items/{id}")
    public ResponseModel<CartItemModel> updateCartItem(@PathVariable UUID id, @Valid @RequestBody UpdateCartItemDto dto) {
        CartItemModel model = cartService.updateCartItem(id, dto);
        return ResponseModel.<CartItemModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.CUSTOMER})
    @DeleteMapping("/items/{id}")
    public ResponseModel deleteCartItem(@PathVariable UUID id) {
        cartService.deleteCartItem(id);
        return ResponseModel.builder().message("Delete successfully").build();
    }

    @AllowedUsers(userTypes = {UserType.CUSTOMER})
    @DeleteMapping("/{id}")
    public ResponseModel deleteCart(@PathVariable UUID id) {
        cartService.deleteCart(id);
        return ResponseModel.builder().message("Delete successfully").build();
    }
}
