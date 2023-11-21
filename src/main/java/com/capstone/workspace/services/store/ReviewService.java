package com.capstone.workspace.services.store;

import com.capstone.workspace.dtos.store.AddReviewDto;
import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.entities.store.Review;
import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.repositories.store.ReviewRepository;
import com.capstone.workspace.services.order.OrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
            throw new BadRequestException("Customer has not received the order yet");
        }

        Review reviewed = repository.findByOrder(order);
        if (reviewed != null) {
            throw new ConflictException("Order has been reviewed");
        }

        entity.setOrder(order);
        return repository.save(entity);
    }
}
