package com.capstone.workspace.controllers.product;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.annotations.RequiredPartner;
import com.capstone.workspace.dtos.product.CreateProductDto;
import com.capstone.workspace.dtos.product.SearchProductPurchasesDto;
import com.capstone.workspace.dtos.product.SearchProductDto;
import com.capstone.workspace.dtos.product.UpdateProductDto;
import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.product.ProductDetailsModel;
import com.capstone.workspace.models.product.ProductModel;
import com.capstone.workspace.models.product.ProductPurchaseModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.services.product.ProductService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping( "/api/products")
@RequiredArgsConstructor
public class ProductController {
    @NonNull
    private final ProductService productService;

    @NonNull
    private final ModelMapper mapper;

    @GetMapping("/details/{id}")
    public ResponseModel<ProductDetailsModel> getProductById(@PathVariable UUID id) {
        Product entity = productService.getProductById(id);
        ProductDetailsModel model = mapper.map(entity, ProductDetailsModel.class);
        return ResponseModel.<ProductDetailsModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER})
    @RequiredPartner
    @PostMapping
    public ResponseModel<ProductModel> create(@Valid @RequestBody CreateProductDto dto) {
        Product product = productService.createProduct(dto);
        ProductModel model = mapper.map(product, ProductModel.class);
        return ResponseModel.<ProductModel>builder().data(model).build();
    }

    @PostMapping("/customer/search")
    public ResponseModel<PaginationResponseModel<ProductModel>> customerSearch(@Valid @RequestBody SearchProductDto dto) {
        return searchProduct(dto);
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER, UserType.STAFF})
    @RequiredPartner
    @PostMapping("/search")
    public ResponseModel<PaginationResponseModel<ProductModel>> search(@Valid @RequestBody SearchProductDto dto) {
        return searchProduct(dto);
    }

    private ResponseModel<PaginationResponseModel<ProductModel>> searchProduct(SearchProductDto dto) {
        PaginationResponseModel<ProductModel> data = productService.search(dto);
        return ResponseModel.<PaginationResponseModel<ProductModel>>builder().data(data).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER})
    @RequiredPartner
    @PutMapping("/{id}")
    public ResponseModel<ProductModel> update(@PathVariable UUID id, @Valid @RequestBody UpdateProductDto dto) {
        Product product = productService.updateProduct(id, dto);
        ProductModel model = mapper.map(product, ProductModel.class);
        return ResponseModel.<ProductModel>builder().data(model).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER, UserType.STAFF})
    @RequiredPartner
    @PostMapping("/purchases")
    public ResponseModel<PaginationResponseModel<ProductPurchaseModel>> getProductNumberOfPurchases(@Valid @RequestBody SearchProductPurchasesDto dto) {
        PaginationResponseModel<ProductPurchaseModel> data = productService.getProductNumberOfPurchases(dto);
        return ResponseModel.<PaginationResponseModel<ProductPurchaseModel>>builder().data(data).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER, UserType.STAFF})
    @RequiredPartner
    @PostMapping("/templates/search")
    public ResponseModel<PaginationResponseModel<ProductModel>> searchTemplates(@Valid @RequestBody SearchProductDto dto) {
        PaginationResponseModel<ProductModel> data = productService.searchTemplates(dto);
        return ResponseModel.<PaginationResponseModel<ProductModel>>builder().data(data).build();
    }

    @AllowedUsers(userTypes = {UserType.OWNER, UserType.STORE_MANAGER})
    @DeleteMapping("/{id}")
    public ResponseModel delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseModel.builder().message("Delete product successfully").build();
    }
}
