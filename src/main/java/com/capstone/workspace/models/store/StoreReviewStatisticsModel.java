package com.capstone.workspace.models.store;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StoreReviewStatisticsModel {
    private double averagePoint;
    private long numberOfReviews;
}
