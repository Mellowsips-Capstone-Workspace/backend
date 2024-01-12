package com.capstone.workspace.repositories.store;

import com.capstone.workspace.dtos.shared.PaginationDto;
import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.entities.store.IStoreEntity;
import com.capstone.workspace.entities.store.Store;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.repositories.shared.BaseRepositoryImplement;
import com.capstone.workspace.services.auth.IdentityService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Transactional
public class StoreRepositoryImplement extends BaseRepositoryImplement<Store, UUID> implements StoreRepository {
    public StoreRepositoryImplement(JpaEntityInformation<Store, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public StoreRepositoryImplement(Class<Store> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    @Override
    public Store findByPartnerId(String partnerId) {
        return null;
    }

    @Override
    public PaginationResponseModel<StoreModel> getHotDealStores(PaginationDto dto) {
        StringBuilder queryString = new StringBuilder(
            "SELECT s.* FROM store s INNER JOIN voucher v " +
            "ON s.partner_id = v.partner_id AND (CAST(s.id as text) = v.store_id OR v.store_id IS NULL) " +
            "WHERE v.start_date < NOW() AND (v.end_date > NOW() OR v.end_date IS NULL) AND v.quantity > 0 AND s.is_active = TRUE " +
            "GROUP BY s.id"
        );

        int page = 1;
        int itemsPerPage = 10;
        if (dto != null) {
            page = dto.getPage();
            itemsPerPage = dto.getItemsPerPage();
        }

        Query query = this.entityManager.createNativeQuery(queryString.toString(), Store.class);
        List<Store> resultList = query.getResultList();

        List<Store> data = resultList.stream()
                .skip((long) itemsPerPage * (page - 1))
                .limit(itemsPerPage)
                .toList();

        List<StoreModel> models = BeanHelper.getBean(ModelMapper.class).map(
                data,
                new TypeToken<List<StoreModel>>() {}.getType()
        );

        int totalItems = resultList.size();
        return PaginationResponseModel.<StoreModel>builder()
                .results(models)
                .page(page)
                .itemsPerPage(itemsPerPage)
                .totalItems(totalItems)
                .build();
    }

    @Override
    public PaginationResponseModel<StoreModel> getQualityStores(PaginationDto dto) {
        StringBuilder queryString = new StringBuilder(
            "SELECT s FROM Store s INNER JOIN Order o " +
            "ON CAST(s.id as text) = o.storeId " +
            "INNER JOIN Review r ON r.order.id = o.id " +
            "WHERE s.isActive = TRUE " +
            "GROUP BY s.id HAVING AVG(r.point) >= 4 " +
            "ORDER BY COUNT(r.id) DESC, AVG(r.point) DESC"
        );

        int page = 1;
        int itemsPerPage = 10;
        if (dto != null) {
            page = dto.getPage();
            itemsPerPage = dto.getItemsPerPage();
        }

        TypedQuery<Store> typedQuery = this.entityManager.createQuery(queryString.toString(), getDomainClass());
        List<Store> resultList = typedQuery.getResultList();

        List<Store> data = resultList.stream()
                .skip((long) itemsPerPage * (page - 1))
                .limit(itemsPerPage)
                .toList();

        List<StoreModel> models = BeanHelper.getBean(ModelMapper.class).map(
                data,
                new TypeToken<List<StoreModel>>() {}.getType()
        );

        int totalItems = resultList.size();
        return PaginationResponseModel.<StoreModel>builder()
                .results(models)
                .page(page)
                .itemsPerPage(itemsPerPage)
                .totalItems(totalItems)
                .build();
    }

    @Override
    public PaginationResponseModel<StoreModel> getStoresByKeywords(PaginationDto dto, String[] keywords) {
        StringBuilder queryString = new StringBuilder(
                "SELECT s FROM Store s INNER JOIN Product p " +
                "ON CAST(s.id as text) = p.storeId " +
                "WHERE s.isActive = TRUE AND p.menu.isActive = TRUE"
        );

        for (int i = 0; i < keywords.length; i++) {
            if (i == 0) {
                queryString.append(" AND (");
            } else {
                queryString.append(" OR ");
            }

            queryString
                    .append("ARRAY_TO_STRING(s.categories, ',') ILIKE '%")
                    .append(keywords[i])
                    .append("%' OR ARRAY_TO_STRING(p.categories, ',') ILIKE '%")
                    .append(keywords[i])
                    .append("%' OR s.name ILIKE '%")
                    .append(keywords[i])
                    .append("%' OR p.name ILIKE '%")
                    .append(keywords[i])
                    .append("%' OR p.description ILIKE '%")
                    .append(keywords[i])
                    .append("%'");

            if (i == keywords.length - 1) {
                queryString.append(")");
            }
        }
        queryString.append(" GROUP BY s.id");

        int page = 1;
        int itemsPerPage = 10;
        if (dto != null) {
            page = dto.getPage();
            itemsPerPage = dto.getItemsPerPage();
        }

        TypedQuery<Store> typedQuery = this.entityManager.createQuery(queryString.toString(), getDomainClass());
        List<Store> resultList = typedQuery.getResultList();

        List<Store> data = resultList.stream()
                .skip((long) itemsPerPage * (page - 1))
                .limit(itemsPerPage)
                .toList();

        List<StoreModel> models = BeanHelper.getBean(ModelMapper.class).map(
                data,
                new TypeToken<List<StoreModel>>() {}.getType()
        );

        int totalItems = resultList.size();
        return PaginationResponseModel.<StoreModel>builder()
                .results(models)
                .page(page)
                .itemsPerPage(itemsPerPage)
                .totalItems(totalItems)
                .build();
    }

    @Override
    public PaginationResponseModel<Store> searchBy(String keyword, String[] searchFields, Map<String, Object> filterParams, Map<String, Sort.Direction> orderParams, PaginationDto pagination) {
        StringBuilder queryString = new StringBuilder("SELECT e FROM " + getDomainClass().getName() + " e WHERE isDeleted = FALSE");

        IdentityService identityService = BeanHelper.getBean(IdentityService.class);
        UserIdentity userIdentity = identityService.getUserIdentity();

        if (userIdentity != null) {
            if (userIdentity.getPartnerId() == null) {
                if (userIdentity.getUsername() != null && userIdentity.getUserType() != UserType.ADMIN && BaseEntity.class.isAssignableFrom(getDomainClass())) {
                    queryString.append(" AND createdBy = ").append("'").append(userIdentity.getUsername()).append("'");
                }
            } else {
                if (IPartnerEntity.class.isAssignableFrom(getDomainClass())) {
                    queryString.append(" AND partnerId = ").append("'").append(userIdentity.getPartnerId()).append("'");
                }
                if (IStoreEntity.class.isAssignableFrom(getDomainClass()) && userIdentity.getStoreId() != null) {
                    queryString.append(" AND storeId = ").append("'").append(userIdentity.getStoreId()).append("'");
                }
            }
        }

        if (filterParams != null && !filterParams.isEmpty()) {
            for (Map.Entry<String, Object> entry: filterParams.entrySet()) {
                if (entry.getValue() == null) {
                    queryString.append(" AND ").append(entry.getKey()).append(" IS NULL");
                    continue;
                }

                try {
                    ArrayList convertedValue = (ArrayList) entry.getValue();

                    if (convertedValue.isEmpty()) {
                        continue;
                    }

                    queryString.append(" AND ").append(entry.getKey()).append(" IN (");
                    for (Object item: convertedValue) {
                        if (item instanceof String) {
                            queryString.append("'").append(item).append("',");
                        } else {
                            queryString.append(item).append(",");
                        }
                    }
                    queryString.deleteCharAt(queryString.length() - 1);
                    queryString.append(")");
                } catch (Exception e) {
                    if (entry.getValue() instanceof String) {
                        queryString.append(" AND ").append(entry.getKey()).append(" = '").append(entry.getValue()).append("'");
                    } else {
                        queryString.append(" AND ").append(entry.getKey()).append(" = ").append(entry.getValue());
                    }
                }
            }
        }

        if (keyword != null && !keyword.isBlank()) {
            queryString
                    .append(" AND (ARRAY_TO_STRING(e.categories, ',') ILIKE '%")
                    .append(keyword)
                    .append("%' OR e.name ILIKE '%")
                    .append(keyword)
                    .append("%')");
        }

        if (orderParams != null && !orderParams.isEmpty()) {
            Map.Entry firstEntry = orderParams.entrySet().stream().findFirst().get();
            queryString.append(" ORDER BY ").append(firstEntry.getKey()).append(" ").append(firstEntry.getValue());
        }

        int page = 1;
        int itemsPerPage = 10;
        if (pagination != null) {
            page = pagination.getPage();
            itemsPerPage = pagination.getItemsPerPage();
        }

        TypedQuery<Store> typedQuery = this.entityManager.createQuery(queryString.toString(), getDomainClass());
        List<Store> resultList = typedQuery.getResultList();
        int totalItems = resultList.size();

        List<Store> data = resultList.stream()
                .skip((long) itemsPerPage * (page - 1))
                .limit(itemsPerPage)
                .toList();

        return PaginationResponseModel.<Store>builder()
                .results(data)
                .page(page)
                .itemsPerPage(itemsPerPage)
                .totalItems(totalItems)
                .build();
    }
}
