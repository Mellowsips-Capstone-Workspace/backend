package com.capstone.workspace.repositories.product;

import com.capstone.workspace.dtos.product.SearchProductPurchasesFilterDto;
import com.capstone.workspace.dtos.shared.PaginationDto;
import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.models.product.ProductModel;
import com.capstone.workspace.models.product.ProductPurchaseModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.repositories.shared.BaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface ProductRepository extends BaseRepository<Product, UUID> {
    PaginationResponseModel<ProductPurchaseModel> searchProductPurchases(
            SearchProductPurchasesFilterDto filterParams,
            Sort.Direction orderCriteria,
            PaginationDto pagination
    );

    List<Product> findAllByMenuId(UUID menuId);

    List<Product> findByMenuIdAndParentId(UUID menuId, UUID parentId);

    List<Product> findAllByParentId(UUID parentId);

    PaginationResponseModel<ProductModel> customerSearchProductPurchases(
            Sort.Direction orderCriteria,
            PaginationDto pagination
    );

    PaginationResponseModel<Product> searchTemplatesBy(
            String keyword,
            String[] searchFields,
            Map<String, Object> filterParams,
            Map<String, Sort.Direction> orderParams,
            PaginationDto pagination
    );
}
