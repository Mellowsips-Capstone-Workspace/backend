package com.capstone.workspace.dtos.store;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddReviewDto {
    @NotNull
    @Min(1)
    @Max(5)
    private int point;

    @NotNull
    private UUID orderId;

    private String comment;
}
