package com.capstone.workspace.services.product;

import com.capstone.workspace.dtos.product.*;
import com.capstone.workspace.entities.product.Product;
import com.capstone.workspace.entities.product.ProductOptionSection;
import com.capstone.workspace.entities.store.Menu;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.product.ProductModel;
import com.capstone.workspace.models.product.ProductPurchaseModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.repositories.product.ProductRepository;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.store.MenuService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

        Menu menu = null;
        if (dto.getMenuId() != null) {
            menu = BeanHelper.getBean(MenuService.class).getMenuById(UUID.fromString(dto.getMenuId()));
            if (!menu.getPartnerId().equals(userIdentity.getPartnerId()) || (userIdentity.getStoreId() != null && !menu.getStoreId().equals(userIdentity.getStoreId()))) {
                throw new ConflictException("Menu do not belong to your organization");
            }

            if (dto.getParentId() != null) {
                List<Product> existed = repository.findByMenuIdAndParentId(UUID.fromString(dto.getMenuId()), UUID.fromString(dto.getParentId()));
                if (existed != null && !existed.isEmpty()) {
                    throw new ConflictException("Product already added in this menu");
                }
            }

            entity.setMenu(menu);
            entity.setStoreId(menu.getStoreId());
        }

        if (dto.getParentId() != null) {
            Product parent = getProductById(UUID.fromString(dto.getParentId()));
            if (menu != null && (!menu.getPartnerId().equals(parent.getPartnerId()) || (parent.getStoreId() != null && !menu.getStoreId().equals(parent.getStoreId())))) {
                throw new ConflictException("Product and menu do not belong to the same organization");
            }
            if (parent.getMenu() != null) {
                throw new BadRequestException("Parent product must not be in another menu");
            }
            if (parent.getParent() != null) {
                throw new BadRequestException("Parent product must be a template");
            }
            entity.setDescription(parent.getDescription());
            entity.setName(parent.getName());
            entity.setParent(parent);
        }

        Product saved = repository.save(entity);

        if (dto.getProductOptionSections() != null && !dto.getProductOptionSections().isEmpty()) {
            dto.getProductOptionSections().forEach(sectionDto -> productOptionSectionService.create(saved, sectionDto));
        }

        return saved;
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

    public PaginationResponseModel<ProductModel> searchTemplates(SearchProductDto dto) {
        String[] searchableFields = new String[]{"name"};
        Map<String, Object> filterParams = new HashMap<>();

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
        filterParams.put("menu.id", null);
        filterParams.put("parent.id", null);

        PaginationResponseModel result = repository.searchTemplatesBy(
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

        if (dto.getProductOptionSections() != null && !dto.getProductOptionSections().isEmpty()) {
            dto.getProductOptionSections().forEach(sectionDto -> productOptionSectionService.update(entity, sectionDto));
        }

        BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);

        Product parent = entity.getParent();
        if (parent != null) {
            entity.setName(parent.getName());
            entity.setDescription(parent.getDescription());
        }

        return repository.save(entity);
    }

    public PaginationResponseModel<ProductPurchaseModel> getProductNumberOfPurchases(SearchProductPurchasesDto dto) {
        SearchProductPurchasesCriteriaDto criteriaDto = dto.getCriteria() == null ? new SearchProductPurchasesCriteriaDto() : dto.getCriteria();
        SearchProductPurchasesFilterDto filterDto = criteriaDto.getFilter() == null ? new SearchProductPurchasesFilterDto() : criteriaDto.getFilter();

        return repository.searchProductPurchases(
                filterDto,
                criteriaDto.getOrder(),
                dto.getPagination()
        );
    }

    public PaginationResponseModel<ProductModel> getBestSellingProducts(SearchProductPurchasesDto dto) {
        SearchProductPurchasesCriteriaDto criteriaDto = dto.getCriteria() == null
                ? new SearchProductPurchasesCriteriaDto()
                : dto.getCriteria();

        return repository.customerSearchProductPurchases(
                criteriaDto.getOrder(),
                dto.getPagination()
        );
    }

    public List<Product> getMenuProducts(UUID menuId) {
        return repository.findAllByMenuId(menuId);
    }

    public void deleteMenuProducts(Menu menu) {
        List<Product> products = getMenuProducts(menu.getId());
        repository.deleteAll(products);
    }

    @Transactional
    public void delete(UUID id) {
        Product entity = getProductById(id);

        List<Product> childProducts = repository.findAllByParentId(id);
        if (childProducts != null && !childProducts.isEmpty()) {
            repository.deleteAll(childProducts);
        }

        repository.delete(entity);
    }
}
