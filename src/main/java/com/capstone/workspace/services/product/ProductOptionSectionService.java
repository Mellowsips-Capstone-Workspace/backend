package com.capstone.workspace.services.product;

import com.capstone.workspace.dtos.product.CreateProductOptionSectionDto;
import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.entities.product.ProductOptionSection;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.repositories.product.ProductOptionSectionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductOptionSectionService {
    @NonNull
    private final ProductOptionSectionRepository repository;

    @NonNull
    private final ModelMapper mapper;

    public ProductOptionSection create(Product product, CreateProductOptionSectionDto dto){
        ProductOptionSection entity = mapper.map(dto, ProductOptionSection.class);
        entity.setProduct(product);
        return repository.save(entity);
    }
}
