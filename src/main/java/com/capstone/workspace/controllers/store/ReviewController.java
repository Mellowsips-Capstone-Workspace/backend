package com.capstone.workspace.controllers.store;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.store.AddReviewDto;
import com.capstone.workspace.entities.store.Review;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.models.store.ReviewModel;
import com.capstone.workspace.services.store.ReviewService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    @NonNull
    private final ReviewService reviewService;

    @NonNull
    private final ModelMapper mapper;

    @AllowedUsers(userTypes = {UserType.CUSTOMER})
    @PostMapping
    public ResponseModel<ReviewModel> create(@Valid @RequestBody AddReviewDto dto) {
        Review entity = reviewService.create(dto);
        ReviewModel model = mapper.map(entity, ReviewModel.class);
        return ResponseModel.<ReviewModel>builder().data(model).build();
    }
}
