package com.capstone.workspace.repositories.store;

import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.entities.store.Review;
import com.capstone.workspace.models.store.StoreReviewStatisticsModel;
import com.capstone.workspace.repositories.shared.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends BaseRepository<Review, UUID> {
    Review findByOrder(Order order);

    @Query(value = "SELECT new com.capstone.workspace.models.store.StoreReviewStatisticsModel(AVG(r.point), COUNT(r.id)) FROM Review r WHERE r.order.storeId = :storeId")
    StoreReviewStatisticsModel getStoreReviewStatistics(@Param("storeId") String storeId);
}
