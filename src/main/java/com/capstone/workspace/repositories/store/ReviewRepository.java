package com.capstone.workspace.repositories.store;

import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.entities.store.Review;
import com.capstone.workspace.repositories.shared.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends BaseRepository<Review, UUID> {
    Page<Review> searchByOrderStoreIdOrderByCreatedAtDesc(String storeId, Pageable pageable);

    Page<Review> searchByOrderStoreIdAndPointOrderByCreatedAtDesc(String storeId, int point, Pageable pageable);

    Review findByOrder(Order order);
}
