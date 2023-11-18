package com.capstone.workspace.services.product;

import com.capstone.workspace.dtos.product.CreateProductOptionSectionDto;
import com.capstone.workspace.dtos.product.UpdateProductAddonDto;
import com.capstone.workspace.dtos.product.UpdateProductOptionSectionDto;
import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.entities.product.ProductAddon;
import com.capstone.workspace.entities.product.ProductOptionSection;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.repositories.product.ProductOptionSectionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductOptionSectionService {
    @NonNull
    private final ProductOptionSectionRepository repository;

    @NonNull
    private final ProductAddonService productAddonService;

    @NonNull
    private final ModelMapper mapper;

    @Transactional
    public ProductOptionSection create(Product product, CreateProductOptionSectionDto dto) {
        ProductOptionSection entity = mapper.map(dto, ProductOptionSection.class);
        entity.setProduct(product);

        ProductOptionSection saved = repository.save(entity);
        dto.getProductAddons().forEach(addonDto -> productAddonService.create(saved, addonDto));

        return saved;
    }

    public ProductOptionSection getOneById(UUID id) {
        ProductOptionSection entity = repository.findById(id).orElse(null);

        if (entity == null) {
            throw new NotFoundException("Product section not found");
        }

        return entity;
    }

    @Transactional
    public ProductOptionSection update(Product product, UpdateProductOptionSectionDto dto) {
        if (dto.getId() == null) {
            return create(product, mapper.map(dto, CreateProductOptionSectionDto.class));
        }

        ProductOptionSection entity = getOneById(dto.getId());

        List<UUID> currentAddonIds = dto.getProductAddons().stream().map(UpdateProductAddonDto::getId).toList();
        List<ProductAddon> removedAddons = entity.getProductAddons().stream()
                .filter(item -> !currentAddonIds.contains(item.getId()))
                .toList();
        productAddonService.deleteBulk(removedAddons);

        BeanUtils.copyProperties(dto, entity, "id");
        ProductOptionSection saved = repository.save(entity);
        dto.getProductAddons().forEach(addonDto -> productAddonService.update(saved, addonDto));

        return saved;
    }

    @Transactional
    public void deleteBulk(List<ProductOptionSection> optionSections) {
        repository.deleteAll(optionSections);
    }
}
