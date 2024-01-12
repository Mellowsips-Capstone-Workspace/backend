package com.capstone.workspace.repositories.product;

import com.capstone.workspace.dtos.product.SearchProductPurchasesFilterDto;
import com.capstone.workspace.dtos.shared.PaginationDto;
import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.entities.product.ProductPurchaseView;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.entities.store.IStoreEntity;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.ForbiddenException;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.product.ProductModel;
import com.capstone.workspace.models.product.ProductPurchaseModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
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
                "SELECT CASE WHEN p1.parent_id IS NOT NULL THEN p1.parent_id ELSE p1.id END AS id, p1.name, p1.description, SUM(ci.quantity) AS number_of_purchases FROM product p RIGHT JOIN product p1 ON p.id = p1.parent_id INNER JOIN cart_item ci ON p1.id = ci.product_id WHERE ci.is_bought = TRUE"
        );

        IdentityService identityService = BeanHelper.getBean(IdentityService.class);
        UserIdentity userIdentity = identityService.getUserIdentity();

        queryString.append(" AND p1.partner_id = ").append("'").append(userIdentity.getPartnerId()).append("'");

        String storeId = userIdentity.getUserType() == UserType.OWNER
                ? (filterParams.getStoreId() != null ? filterParams.getStoreId() : null)
                : userIdentity.getStoreId();
        if (storeId != null) {
            queryString.append(" AND p1.store_id = ").append("'").append(storeId).append("'");
        }

        if (filterParams.getStartDate() != null) {
            queryString.append(" AND ci.updated_at >= ").append("'").append(filterParams.getStartDate()).append("'");
        }

        if (filterParams.getEndDate() != null) {
            queryString.append(" AND ci.updated_at <= ").append("'").append(filterParams.getEndDate()).append("'");
        }
        queryString.append(" GROUP BY CASE WHEN p1.parent_id IS NOT NULL THEN p1.parent_id ELSE p1.id END, p1.name, p1.description ORDER BY number_of_purchases ").append(orderCriteria == null ? Sort.Direction.DESC : orderCriteria);

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

    @Override
    public List<Product> findAllByMenuId(UUID menuId) {
        String query = "SELECT p FROM Product p WHERE p.menu.id = :menuId";

        return entityManager.createQuery(query, Product.class)
                .setParameter("menuId", menuId)
                .getResultList();
    }

    @Override
    public List<Product> findByMenuIdAndParentId(UUID menuId, UUID parentId) {
        String query = "SELECT p FROM Product p WHERE p.menu.id = :menuId AND p.parent.id = :parentId";

        return entityManager.createQuery(query, Product.class)
                .setParameter("menuId", menuId)
                .setParameter("parentId", parentId)
                .getResultList();
    }

    @Override
    public List<Product> findAllByParentId(UUID parentId) {
        String query = "SELECT p FROM Product p WHERE p.parent.id = :parentId";

        return entityManager.createQuery(query, Product.class)
                .setParameter("parentId", parentId)
                .getResultList();
    }

    @Override
    public PaginationResponseModel<ProductModel> customerSearchProductPurchases(
            Sort.Direction orderCriteria,
            PaginationDto pagination
    ) {
        StringBuilder queryString = new StringBuilder(
                "SELECT p.* FROM product p INNER JOIN cart_item ci ON p.id = ci.product_id LEFT JOIN menu m ON m.id = p.menu_id WHERE ci.is_bought = TRUE AND m.is_active = TRUE"
        );

        queryString.append(" GROUP BY p.id ORDER BY SUM(ci.quantity) ").append(orderCriteria == null ? Sort.Direction.DESC : orderCriteria);

        int page = 1;
        int itemsPerPage = 10;
        if (pagination != null) {
            page = pagination.getPage();
            itemsPerPage = pagination.getItemsPerPage();
        }

        Query query = this.entityManager.createNativeQuery(queryString.toString(), Product.class);
        List<ProductModel> resultList = BeanHelper.getBean(ModelMapper.class).map(
                query.getResultList(),
                new TypeToken<List<ProductModel>>() {}.getType()
        );

        int totalItems = resultList.size();

        List<ProductModel> data = resultList.stream()
                .skip((long) itemsPerPage * (page - 1))
                .limit(itemsPerPage)
                .toList();

        return PaginationResponseModel.<ProductModel>builder()
                .results(data)
                .page(page)
                .itemsPerPage(itemsPerPage)
                .totalItems(totalItems)
                .build();
    }

    @Override
    public PaginationResponseModel<Product> searchTemplatesBy(String keyword, String[] searchFields, Map<String, Object> filterParams, Map<String, Sort.Direction> orderParams, PaginationDto pagination) {
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

                String storeId = userIdentity.getStoreId() != null ? userIdentity.getStoreId() : (String) filterParams.get("storeId");
                if (IStoreEntity.class.isAssignableFrom(getDomainClass()) && storeId != null) {
                    queryString.append(" AND (storeId = ").append("'").append(storeId).append("' OR storeId IS NULL)");
                }
            }
        }

        if (filterParams != null && !filterParams.isEmpty()) {
            for (Map.Entry<String, Object> entry: filterParams.entrySet()) {
                if (entry.getKey().equals("storeId")) continue;

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

        if (keyword != null && !keyword.isBlank() && searchFields != null && searchFields.length > 0) {
            queryString.append(" AND (");
            for (int i = 0; i < searchFields.length; i++) {
                if (i != 0) {
                    queryString.append(" OR ");
                }
                queryString.append(searchFields[i]).append(" ILIKE ").append("'%").append(keyword).append("%'");
            }
            queryString.append(")");
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

        TypedQuery<Product> typedQuery = this.entityManager.createQuery(queryString.toString(), getDomainClass());
        List<Product> resultList = typedQuery.getResultList();
        int totalItems = resultList.size();

        List<Product> data = resultList.stream()
                .skip((long) itemsPerPage * (page - 1))
                .limit(itemsPerPage)
                .toList();

        return PaginationResponseModel.<Product>builder()
                .results(data)
                .page(page)
                .itemsPerPage(itemsPerPage)
                .totalItems(totalItems)
                .build();
    }
}
