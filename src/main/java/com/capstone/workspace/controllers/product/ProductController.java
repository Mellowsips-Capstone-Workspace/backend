package com.capstone.workspace.controllers.product;

import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.models.product.ProductDetailsModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.services.product.ProductService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping( "/api/products")
@RequiredArgsConstructor
public class ProductController {
    @NonNull
    private final ProductService productService;

    @NonNull
    private final ModelMapper mapper;

    @GetMapping("/{id}")
    public ResponseModel<ProductDetailsModel> getProductById(@PathVariable UUID id) {
        Product entity = productService.getProductById(id);
        ProductDetailsModel model = mapper.map(entity, ProductDetailsModel.class);
        return ResponseModel.<ProductDetailsModel>builder().data(model).build();
    }
}
