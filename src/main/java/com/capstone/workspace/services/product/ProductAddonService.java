package com.capstone.workspace.services.product;

import com.capstone.workspace.dtos.product.CreateProductAddonDto;
import com.capstone.workspace.dtos.product.UpdateProductAddonDto;
import com.capstone.workspace.entities.product.ProductAddon;
import com.capstone.workspace.entities.product.ProductOptionSection;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.repositories.product.ProductAddonRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductAddonService {
    @NonNull
    private final ProductAddonRepository repository;

    @NonNull
    private final ModelMapper mapper;

    public ProductAddon getOneById(UUID id) {
        ProductAddon entity = repository.findById(id).orElse(null);

        if (entity == null) {
            throw new NotFoundException("Addon not found");
        }

        return entity;
    }

    public List<ProductAddon> getBulk(List<UUID> ids) {
        return repository.findAllById(ids);
    }

    public ProductAddon checkIfAddonBelongToProduct(UUID addonId, UUID productId) {
        ProductAddon addon = getOneById(addonId);

        if (!productId.equals(addon.getProductOptionSection().getProduct().getId())) {
            throw new BadRequestException("Product does not have this addon item");
        }

        return addon;
    }

    public ProductAddon create(ProductOptionSection optionSection, CreateProductAddonDto dto) {
        ProductAddon entity = mapper.map(dto, ProductAddon.class);
        entity.setProductOptionSection(optionSection);
        return repository.save(entity);
    }

    public ProductAddon update(ProductOptionSection optionSection, UpdateProductAddonDto dto) {
        if (dto.getId() == null) {
            ProductAddon entity = mapper.map(dto, ProductAddon.class);
            entity.setProductOptionSection(optionSection);
            return repository.save(entity);
        }

        ProductAddon entity = getOneById(dto.getId());
        BeanUtils.copyProperties(dto, entity);

        return repository.save(entity);
    }
}
