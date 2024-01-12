package com.capstone.workspace.services.partner;

import com.capstone.workspace.dtos.partner.SearchPartnerCriteriaDto;
import com.capstone.workspace.dtos.partner.SearchPartnerDto;
import com.capstone.workspace.entities.partner.Partner;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.partner.PartnerModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.repositories.partner.PartnerRepository;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartnerService {
    @NonNull
    private final PartnerRepository repository;

    @NonNull
    private final ModelMapper mapper;

    @NonNull
    private final IdentityService identityService;

    public Partner create(PartnerModel model) {
        Partner entity = upsert(null, model);
        return repository.save(entity);
    }

    private Partner upsert(UUID id, Object dto) {
        if (id != null) {
            Partner entity = getOneById(id);
            BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);
            return entity;
        }

        return mapper.map(dto, Partner.class);
    }

    public Partner getOneById(UUID id) {
        Partner partner = repository.findById(id).orElse(null);

        UserIdentity userIdentity = identityService.getUserIdentity();
        if (partner == null || (userIdentity.getUserType() != UserType.ADMIN && !userIdentity.getPartnerId().equals(String.valueOf(partner.getId())))) {
            throw new NotFoundException("Partner not found");
        }

        return partner;
    }

    public PaginationResponseModel<PartnerModel> search(SearchPartnerDto dto) {
        String[] searchableFields = new String[]{"name", "businessCode", "taxCode"};
        Map<String, Object> filterParams = Collections.emptyMap();

        SearchPartnerCriteriaDto criteria = dto.getCriteria();
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

        List<PartnerModel> partnerModels = mapper.map(
                result.getResults(),
                new TypeToken<List<PartnerModel>>() {}.getType()
        );
        result.setResults(partnerModels);

        return result;
    }

    public Partner getByBusinessCode(String businessCode) {
        Partner entity = repository.findByBusinessCode(businessCode);

        if (entity == null) {
            throw new NotFoundException("Partner not found");
        }

        return entity;
    }

    public Partner getByTaxCode(String taxCode) {
        Partner entity = repository.findByTaxCode(taxCode);

        if (entity == null) {
            throw new NotFoundException("Partner not found");
        }

        return entity;
    }
}
