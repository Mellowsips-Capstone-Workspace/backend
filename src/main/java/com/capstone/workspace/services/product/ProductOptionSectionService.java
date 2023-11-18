package com.capstone.workspace.services.product;

import com.capstone.workspace.dtos.product.CreateProductOptionSectionDto;
import com.capstone.workspace.dtos.product.UpdateProductOptionSectionDto;
import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.entities.product.ProductOptionSection;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.repositories.product.ProductOptionSectionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductOptionSectionService {
    @NonNull
    private final ProductOptionSectionRepository repository;

    @NonNull
    private final ModelMapper mapper;

    public ProductOptionSection create(Product product, CreateProductOptionSectionDto dto) {
        ProductOptionSection entity = mapper.map(dto, ProductOptionSection.class);
        entity.setProduct(product);
        return repository.save(entity);
    }

    public ProductOptionSection getOneById(UUID id) {
        ProductOptionSection entity = repository.findById(id).orElse(null);

        if (entity == null) {
            throw new NotFoundException("Product section not found");
        }

        return entity;
    }

    public ProductOptionSection update(Product product, UpdateProductOptionSectionDto dto) {
        if (dto.getId() == null) {
            ProductOptionSection entity = mapper.map(dto, ProductOptionSection.class);
            entity.setProduct(product);
            return repository.save(entity);
        }

        ProductOptionSection entity = getOneById(dto.getId());
        BeanUtils.copyProperties(dto, entity);

        return repository.save(entity);
    }
}
