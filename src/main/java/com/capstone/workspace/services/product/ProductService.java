package com.capstone.workspace.services.product;

import com.capstone.workspace.dtos.product.*;
import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.entities.product.ProductOptionSection;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.product.ProductModel;
import com.capstone.workspace.models.product.ProductPurchaseModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.repositories.product.ProductRepository;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    @Autowired
    @Qualifier("productRepositoryImplement")
    private final ProductRepository repository;

    @NonNull
    private final ProductOptionSectionService productOptionSectionService;

    @NonNull
    private final ModelMapper mapper;

    @NonNull
    private final IdentityService identityService;

    public Product getProductById(UUID id) {
        Product entity = repository.findById(id).orElse(null);

        if (entity == null) {
            throw new NotFoundException("Product not found");
        }

        return entity;
    }

    @Transactional
    public Product createProduct(CreateProductDto dto) {
        Product entity = mapper.map(dto, Product.class);

        UserIdentity userIdentity = identityService.getUserIdentity();
        if (userIdentity.getUserType() != UserType.OWNER) {
            entity.setStoreId(null);
        }

        List<CreateProductOptionSectionDto> productOptionSections = dto.getProductOptionSections();
        if (productOptionSections != null && productOptionSections.size() >= 2) {
            productOptionSections.sort(Comparator.comparingInt(CreateProductOptionSectionDto::getPriority));
        }

        repository.save(entity);

        if (dto.getProductOptionSections() != null && !dto.getProductOptionSections().isEmpty()) {
            dto.getProductOptionSections().forEach(sectionDto -> productOptionSectionService.create(entity, sectionDto));
        }

        return entity;
    }

    public PaginationResponseModel<ProductModel> search(SearchProductDto dto) {
        String[] searchableFields = new String[]{"name"};
        Map<String, Object> filterParams = Collections.emptyMap();

        SearchProductCriteriaDto criteria = dto.getCriteria();
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

        List<ProductModel> productModels = mapper.map(
                result.getResults(),
                new TypeToken<List<ProductModel>>() {}.getType()
        );
        result.setResults(productModels);

        return result;
    }

    @Transactional
    public Product updateProduct(UUID id, UpdateProductDto dto) {
        Product entity = getProductById(id);

        List<UUID> currentAddonIds = dto.getProductOptionSections().stream().map(UpdateProductOptionSectionDto::getId).toList();
        List<ProductOptionSection> removedSections = entity.getProductOptionSections().stream()
                .filter(item -> !currentAddonIds.contains(item.getId()))
                .toList();
        productOptionSectionService.deleteBulk(removedSections);

        List<UpdateProductOptionSectionDto> productOptionSections = dto.getProductOptionSections();
        if (productOptionSections != null && productOptionSections.size() >= 2) {
            productOptionSections.sort(Comparator.comparingInt(UpdateProductOptionSectionDto::getPriority));
        }

        if (dto.getProductOptionSections() != null && !dto.getProductOptionSections().isEmpty()) {
            dto.getProductOptionSections().forEach(sectionDto -> productOptionSectionService.update(entity, sectionDto));
        }

        BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);
        return repository.save(entity);
    }

    public PaginationResponseModel<ProductPurchaseModel> getBestSellingProducts(SearchProductPurchasesDto dto) {
        SearchProductPurchasesCriteriaDto criteriaDto = dto.getCriteria() == null ? new SearchProductPurchasesCriteriaDto() : dto.getCriteria();
        SearchProductPurchasesFilterDto filterDto = criteriaDto.getFilter() == null ? new SearchProductPurchasesFilterDto() : criteriaDto.getFilter();

        return repository.searchProductPurchases(
                filterDto,
                criteriaDto.getOrder(),
                dto.getPagination()
        );
    }
}
