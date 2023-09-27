package com.capstone.workspace.services.store;

import com.capstone.workspace.dtos.store.SearchStoreCriteriaDto;
import com.capstone.workspace.dtos.store.SearchStoreDto;
import com.capstone.workspace.entities.partner.Partner;
import com.capstone.workspace.entities.store.Store;
import com.capstone.workspace.enums.partner.BusinessType;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.UnauthorizedException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.repositories.store.StoreRepository;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.partner.PartnerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StoreService {
    @NonNull
    private final StoreRepository repository;

    @NonNull
    private final IdentityService identityService;

    @NonNull
    private final PartnerService partnerService;

    @NonNull
    private final ModelMapper mapper;

    public List<Store> createBulk(List<StoreModel> data) {
        if (data == null || data.isEmpty()) {
            throw new BadRequestException("Missing store data");
        }

        UserIdentity userIdentity = identityService.getUserIdentity();
        if (userIdentity == null) {
            throw new UnauthorizedException("Missing identity information");
        }

        String partnerId = userIdentity.getPartnerId();
        Partner partner = partnerService.getOneById(UUID.fromString(partnerId));

        BusinessType businessType = partner.getType();
        if (businessType == BusinessType.PERSONAL || businessType == BusinessType.HOUSEHOLD) {
            Store exist = repository.findByPartnerId(partnerId);
            if (exist != null || data.size() > 1) {
                throw new ConflictException("Not allow to have multiple stores");
            }
        }

        List<Store> entities = data.stream().map(item -> {
            Store entity = new Store();
            BeanUtils.copyProperties(item, entity, AppHelper.commonProperties);
            return entity;
        }).toList();

        return repository.saveAll(entities);
    }

    public PaginationResponseModel<StoreModel> search(SearchStoreDto dto) {
        String[] searchableFields = new String[]{};
        Map<String, Object> filterParams = new HashMap<>();

        SearchStoreCriteriaDto criteria = dto.getCriteria();
        String keyword = null;
        Map orderCriteria = null;

        if (criteria != null) {
            if (criteria.getFilter() != null) {
                BeanWrapper wrapper = new BeanWrapperImpl(criteria.getFilter());
                Stream.of(wrapper.getPropertyDescriptors())
                    .forEach(pd -> {
                        if (!pd.getName().equalsIgnoreCase("class")) {
                            filterParams.put(pd.getName(), wrapper.getPropertyValue(pd.getName()));
                        }
                    });
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

        List<StoreModel> storeModels = mapper.map(
            result.getResults(),
            new TypeToken<List<StoreModel>>() {}.getType()
        );
        result.setResults(storeModels);

        return result;
    }
}
