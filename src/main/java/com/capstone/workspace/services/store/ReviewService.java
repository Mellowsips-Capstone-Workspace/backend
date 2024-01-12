package com.capstone.workspace.services.store;

import com.capstone.workspace.dtos.store.AddReviewDto;
import com.capstone.workspace.dtos.store.SearchReviewCriteriaDto;
import com.capstone.workspace.dtos.store.SearchReviewDto;
import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.entities.store.Review;
import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.GoneException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.store.ReviewModel;
import com.capstone.workspace.models.store.StoreReviewStatisticsModel;
import com.capstone.workspace.repositories.store.ReviewRepository;
import com.capstone.workspace.services.order.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewService {
    @NonNull
    private final ReviewRepository repository;

    @NonNull
    private final OrderService orderService;

    @NonNull
    private final ModelMapper mapper;

    public Review create(AddReviewDto dto) {
        Review entity = mapper.map(dto, Review.class);

        Order order = orderService.getOneById(dto.getOrderId());
        if (order.getStatus() != OrderStatus.RECEIVED) {
            throw new BadRequestException("Customer has not received the order");
        }
        if (order.getUpdatedAt().isBefore(Instant.now().minus(30, ChronoUnit.DAYS))) {
            throw new GoneException("Expired time to evaluate order");
        }

        Review reviewed = repository.findByOrderId(dto.getOrderId());
        if (reviewed != null) {
            throw new ConflictException("Order has been reviewed");
        }

        entity.setOrder(order);
        return repository.save(entity);
    }

    public PaginationResponseModel<ReviewModel> getStoreReviews(String id, SearchReviewDto dto) {
        String[] searchableFields = new String[]{};
        Map<String, Object> filterParams = new HashMap<>(){{
            put("order.storeId", id);
        }};

        SearchReviewCriteriaDto criteria = dto.getCriteria();
        Map orderCriteria = null;

        if (criteria != null) {
            if (criteria.getFilter() != null) {
                filterParams = AppHelper.copyPropertiesToMap(criteria.getFilter());
            }
            orderCriteria = criteria.getOrder();
        }

        PaginationResponseModel result = repository.searchBy(
                "",
                searchableFields,
                filterParams,
                orderCriteria,
                dto.getPagination()
        );

        List<ReviewModel> reviewModels = mapper.map(
                result.getResults(),
                new TypeToken<List<ReviewModel>>() {}.getType()
        );
        result.setResults(reviewModels);

        return result;
    }

    public StoreReviewStatisticsModel getStoreReviewStatistics(String storeId) {
        return repository.getStoreReviewStatistics(storeId);
    }

    public ReviewModel getOrderReview(UUID orderId) {
        Review entity = repository.findByOrderId(orderId);
        return entity == null ? null : mapper.map(entity, ReviewModel.class);
    }
}
