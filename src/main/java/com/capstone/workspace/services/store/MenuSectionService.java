package com.capstone.workspace.services.store;

import com.capstone.workspace.dtos.store.CreateMenuSectionDto;
import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.entities.store.Menu;
import com.capstone.workspace.entities.store.MenuSection;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.repositories.store.MenuSectionRepository;
import com.capstone.workspace.services.product.ProductService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuSectionService {
    @NonNull
    private final MenuSectionRepository repository;

    @NonNull
    private final ProductService productService;

    @NonNull
    private final ModelMapper mapper;

    public MenuSection create(Menu menu, CreateMenuSectionDto dto) {
        MenuSection entity = mapper.map(dto, MenuSection.class);
        entity.setMenu(menu);

        List<Product> products = dto.getProductIds().stream()
            .map(productId -> {
                Product product = productService.getProductById(UUID.fromString(productId));
                if (!menu.getStoreId().equals(product.getStoreId())) {
                    throw new BadRequestException("Product with id " + productId + " does not belong to the store");
                }
                return product;
            })
            .toList();
        entity.setProducts(products);

        return repository.save(entity);
    }
}
