package com.capstone.workspace.services.store;

import com.capstone.workspace.dtos.store.CreateMenuDto;
import com.capstone.workspace.dtos.store.CreateMenuSectionDto;
import com.capstone.workspace.dtos.store.SearchMenuCriteriaDto;
import com.capstone.workspace.dtos.store.SearchMenuDto;
import com.capstone.workspace.entities.store.Menu;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.store.MenuModel;
import com.capstone.workspace.repositories.store.MenuRepository;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    public Menu create(CreateMenuDto dto) {
        Menu entity = mapper.map(dto, Menu.class);

        UserIdentity userIdentity = identityService.getUserIdentity();
        if (userIdentity.getUserType() == UserType.OWNER && (dto.getStoreId() == null || dto.getStoreId().isBlank())) {
            throw new BadRequestException("Missing store id");
        }

        String storeId = userIdentity.getUserType() == UserType.OWNER ? dto.getStoreId() : userIdentity.getStoreId();
        entity.setStoreId(storeId);

        if (Boolean.TRUE.equals(dto.getIsActive())) {
            Menu activeMenu = repository.findByStoreIdAndIsActiveTrue(storeId);
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
        return entity;
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

    public PaginationResponseModel<MenuModel> search(SearchMenuDto dto) {
        String[] searchableFields = new String[]{"name"};
        Map<String, Object> filterParams = Collections.emptyMap();

        SearchMenuCriteriaDto criteria = dto.getCriteria();
        String keyword = null;
        Map orderCriteria = null;

        if (criteria != null) {
            if (criteria.getFilter() != null) {
                filterParams = AppHelper.copyPropertiesToMap(criteria.getFilter());
            }
            keyword = criteria.getKeyword();
            orderCriteria = criteria.getOrder();
        }

        PaginationResponseModel result = repository.searchBy(
                keyword,
                searchableFields,
                filterParams,
                orderCriteria,
                dto.getPagination()
        );

        List<MenuModel> menuModels = mapper.map(
                result.getResults(),
                new TypeToken<List<MenuModel>>() {}.getType()
        );
        result.setResults(menuModels);

        return result;
    }
}
