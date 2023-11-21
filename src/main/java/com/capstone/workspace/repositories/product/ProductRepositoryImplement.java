package com.capstone.workspace.repositories.product;

import com.capstone.workspace.dtos.product.SearchProductPurchasesFilterDto;
import com.capstone.workspace.dtos.shared.PaginationDto;
import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.entities.product.ProductPurchaseView;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.ForbiddenException;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.product.ProductPurchaseModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.repositories.shared.BaseRepositoryImplement;
import com.capstone.workspace.services.auth.IdentityService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
public class ProductRepositoryImplement extends BaseRepositoryImplement<Product, UUID> implements ProductRepository {
    public ProductRepositoryImplement(JpaEntityInformation<Product, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public ProductRepositoryImplement(Class<Product> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    @Override
    public PaginationResponseModel<ProductPurchaseModel> searchProductPurchases(
            SearchProductPurchasesFilterDto filterParams,
            Sort.Direction orderCriteria,
            PaginationDto pagination
    ) {
        StringBuilder queryString = new StringBuilder(
                "SELECT p.*, SUM(ci.quantity) AS number_of_purchases FROM product p RIGHT JOIN cart_item ci ON p.id = ci.product_id WHERE ci.is_bought = TRUE"
        );

        IdentityService identityService = BeanHelper.getBean(IdentityService.class);
        UserIdentity userIdentity = identityService.getUserIdentity();

        if (userIdentity.getPartnerId() == null) {
            throw new ForbiddenException("You must be in an organization");
        }

        queryString.append(" AND p.partner_id = ").append("'").append(userIdentity.getPartnerId()).append("'");

        String storeId = userIdentity.getUserType() == UserType.OWNER
                ? (filterParams.getStoreId() != null ? filterParams.getStoreId() : null)
                : userIdentity.getStoreId();
        if (storeId != null) {
            queryString.append(" AND p.store_id = ").append("'").append(storeId).append("'");
        }

        if (filterParams.getStartDate() != null) {
            queryString.append(" AND ci.updated_at >= ").append("'").append(filterParams.getStartDate()).append("'");
        }

        if (filterParams.getEndDate() != null) {
            queryString.append(" AND ci.updated_at <= ").append("'").append(filterParams.getEndDate()).append("'");
        }
        queryString.append(" GROUP BY p.id ORDER BY number_of_purchases ").append(orderCriteria == null ? Sort.Direction.DESC : orderCriteria);

        int page = 1;
        int itemsPerPage = 10;
        if (pagination != null) {
            page = pagination.getPage();
            itemsPerPage = pagination.getItemsPerPage();
        }

        Query query = this.entityManager.createNativeQuery(queryString.toString(), ProductPurchaseView.class);
        List<ProductPurchaseModel> resultList = BeanHelper.getBean(ModelMapper.class).map(
                query.getResultList(),
                new TypeToken<List<ProductPurchaseModel>>() {}.getType()
        );

        int totalItems = resultList.size();

        List<ProductPurchaseModel> data = resultList.stream()
                .skip((long) itemsPerPage * (page - 1))
                .limit(itemsPerPage)
                .toList();

        return PaginationResponseModel.<ProductPurchaseModel>builder()
                .results(data)
                .page(page)
                .itemsPerPage(itemsPerPage)
                .totalItems(totalItems)
                .build();
    }
}
