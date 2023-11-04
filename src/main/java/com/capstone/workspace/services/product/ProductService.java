package com.capstone.workspace.services.product;

import com.capstone.workspace.dtos.product.CreateProductAddonDto;
import com.capstone.workspace.dtos.product.CreateProductDto;
import com.capstone.workspace.dtos.product.CreateProductOptionSectionDto;
import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.entities.product.ProductOptionSection;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.repositories.product.ProductRepository;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    @NonNull
    private final ProductRepository repository;

    @NonNull
    private final ProductOptionSectionService productOptionSectionService;

    @NonNull
    private final ProductAddonService productAddonService;

    @NonNull
    private final ModelMapper mapper;

    @NonNull
    private final IdentityService identityService;

    public Product getProductById(UUID id) {
        Product entity = repository.findById(id).orElse(null);

        if (entity == null) {
            throw new NotFoundException("Product not found");
        }
        return entity;
    }
    public Product createProduct(CreateProductDto dto) {
        UserIdentity userIdentity = identityService.getUserIdentity();
        Product entity = mapper.map(dto, Product.class);
        entity.setPartnerId(userIdentity.getPartnerId());

        repository.save(entity);

        if (dto.getProductOptionSections() != null && !dto.getProductOptionSections().isEmpty()) {
            dto.getProductOptionSections().forEach(sectionDto -> {
                ProductOptionSection optionSection = productOptionSectionService.create(entity, sectionDto);

                if (sectionDto.getProductAddons() != null && !sectionDto.getProductAddons().isEmpty()) {
                    sectionDto.getProductAddons().forEach(addonDto -> {
                        productAddonService.create(optionSection, addonDto);
                    });
                }
            });
        }

        return entity;
    }

}
