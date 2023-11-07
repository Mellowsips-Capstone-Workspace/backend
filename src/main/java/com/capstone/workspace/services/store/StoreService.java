package com.capstone.workspace.services.store;

import com.capstone.workspace.dtos.store.SearchStoreCriteriaDto;
import com.capstone.workspace.dtos.store.SearchStoreDto;
import com.capstone.workspace.dtos.store.UpdateStoreDto;
import com.capstone.workspace.entities.partner.Partner;
import com.capstone.workspace.entities.store.Store;
import com.capstone.workspace.enums.partner.BusinessType;
import com.capstone.workspace.exceptions.*;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.shared.Period;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.repositories.store.StoreRepository;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.document.DocumentService;
import com.capstone.workspace.services.partner.PartnerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;

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

    @NonNull
    private final DocumentService documentService;

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
        String[] searchableFields = new String[]{"name"};
        Map<String, Object> filterParams = Collections.emptyMap();

        SearchStoreCriteriaDto criteria = dto.getCriteria();
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

        List<StoreModel> storeModels = mapper.map(
            result.getResults(),
            new TypeToken<List<StoreModel>>() {}.getType()
        );
        result.setResults(storeModels);

        return result;
    }

    public Store getStoreById(UUID id) {
        Store entity = repository.findById(id).orElse(null);

        if (entity == null) {
            throw new NotFoundException("Store not found");
        }

        return entity;
    }

    public Store updateStore(UUID id, UpdateStoreDto dto) throws ParseException {
        Store store = getStoreById(id);
        checkDocumentExist(dto.getProfileImage());
        checkDocumentExist(dto.getCoverImage());

        if (dto.getOperationalHours() != null && !dto.getOperationalHours().isEmpty()) {
            DateFormat formatter = new SimpleDateFormat("HH:mm");

            for (DayOfWeek day: dto.getOperationalHours().keySet()) {
                List<Period<String>> periods = dto.getOperationalHours().get(day);

                for (Period<String> period: periods) {
                    if (period.getStart() == null || period.getEnd() == null) {
                        throw new BadRequestException("Period data must have both start time and end time");
                    }

                    Time start = new Time(formatter.parse(period.getStart()).getTime());
                    Time end = new Time(formatter.parse(period.getEnd()).getTime());

                    if (start.after(end)) {
                        throw new BadRequestException("Start time must be before end time");
                    }
                }

                Collections.sort(periods);
                for (int i = 0; i < periods.size() - 1; i++) {
                    Period<String> current = periods.get(i);
                    Period<String> next = periods.get(i + 1);

                    Time nextStart = new Time(formatter.parse(next.getStart()).getTime());
                    Time currentEnd = new Time(formatter.parse(current.getEnd()).getTime());

                    if (nextStart.after(currentEnd)) continue;

                    Time nextEnd = new Time(formatter.parse(next.getEnd()).getTime());
                    if (currentEnd.before(nextEnd)) {
                        current.setEnd(next.getEnd());
                    }

                    periods.remove(i + 1);
                    i = i - 1;
                }
            }
        }

        BeanUtils.copyProperties(dto, store, AppHelper.commonProperties);
        return repository.save(store);
    }

    private void checkDocumentExist(String s) {
        if (s != null && !s.isBlank()) {
            String[] data = s.split("\\|");
            String documentId = data[data.length - 1];
            documentService.getDocumentById(UUID.fromString(documentId));
        }
    }
}
