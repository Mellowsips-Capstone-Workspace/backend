package com.capstone.workspace.services.store;

import com.capstone.workspace.dtos.store.CreateMenuDto;
import com.capstone.workspace.dtos.store.CreateMenuSectionDto;
import com.capstone.workspace.entities.store.Menu;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.repositories.store.MenuRepository;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
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

    @NonNull
    private final IdentityService identityService;

    public Menu getStoreMenu(String storeId) {
        storeService.getStoreById(UUID.fromString(storeId));
        Menu entity = repository.findByStoreIdAndIsActiveTrue(storeId);

        if (entity == null) {
            throw new NotFoundException("This store does not have menu");
        }

        return entity;
    }

    @Transactional
    public void create(UUID storeId, CreateMenuDto dto) {
        Menu entity = mapper.map(dto, Menu.class);
        entity.setStoreId(String.valueOf(storeId));

        if (Boolean.TRUE.equals(dto.getIsActive())) {
            Menu activeMenu = repository.findByStoreIdAndIsActiveTrue(String.valueOf(storeId));
            if (activeMenu != null) {
                activeMenu.setIsActive(false);
                repository.save(activeMenu);
            }
        }

        List<CreateMenuSectionDto> menuSections = dto.getMenuSections();
        if (menuSections.size() >= 2) {
            menuSections.sort(Comparator.comparingInt(CreateMenuSectionDto::getPriority));
        }

        repository.save(entity);

        dto.getMenuSections().forEach(section -> menuSectionService.create(entity, section));
    }

    public Menu getMenuById(UUID id) {
        Menu entity = repository.findById(id).orElse(null);

        UserIdentity userIdentity = identityService.getUserIdentity();
        if (entity == null ||
                (userIdentity.getUserType() == UserType.OWNER && !userIdentity.getPartnerId().equals(entity.getPartnerId())) ||
                ((userIdentity.getUserType() == UserType.STORE_MANAGER || userIdentity.getUserType() == UserType.STAFF) && !userIdentity.getStoreId().equals(entity.getStoreId()))) {
            throw new NotFoundException("Menu not found");
        }

        return entity;
    }
}
