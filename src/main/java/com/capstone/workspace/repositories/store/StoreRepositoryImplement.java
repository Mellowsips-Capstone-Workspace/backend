package com.capstone.workspace.repositories.store;

import com.capstone.workspace.dtos.shared.PaginationDto;
import com.capstone.workspace.entities.product.ProductPurchaseView;
import com.capstone.workspace.entities.store.Store;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.ForbiddenException;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.product.ProductPurchaseModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.repositories.shared.BaseRepositoryImplement;
import com.capstone.workspace.services.auth.IdentityService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
            "WHERE v.start_date < NOW() AND (v.end_date > NOW() OR v.end_date IS NULL) AND v.quantity > 0 " +
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
}
