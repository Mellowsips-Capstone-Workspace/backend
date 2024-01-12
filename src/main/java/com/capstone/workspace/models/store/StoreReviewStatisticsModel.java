package com.capstone.workspace.models.store;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StoreReviewStatisticsModel {
    private double averagePoint;
    private long numberOfReviews;

    public double getAveragePoint() {
        return Math.ceil(averagePoint * 10) / 10;
    }
}
