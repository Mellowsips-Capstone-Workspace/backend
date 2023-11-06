package com.capstone.workspace.services.store;

import com.capstone.workspace.dtos.store.CreateMenuDto;
import com.capstone.workspace.entities.store.Menu;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.repositories.store.MenuRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuService {
    @NonNull
    private final MenuRepository repository;

    @NonNull
    private final StoreService storeService;

    @NonNull
    private final MenuSectionService menuSectionService;

    @NonNull
    private final ModelMapper mapper;

    public Menu getStoreMenu(String storeId) {
        storeService.getStoreById(UUID.fromString(storeId));
        Menu entity = repository.findByStoreId(storeId);

        if (entity == null) {
            throw new NotFoundException("This store does not have menu");
        }

        return entity;
    }

    @Transactional
    public Menu create(UUID storeId, CreateMenuDto dto) {
        Menu entity = mapper.map(dto, Menu.class);
        entity.setStoreId(String.valueOf(storeId));

        repository.save(entity);

        if (dto.getMenuSections() != null && !dto.getMenuSections().isEmpty()) {
            dto.getMenuSections().forEach(addonDto -> menuSectionService.create(entity, addonDto));
        }

        return entity;
    }
}
