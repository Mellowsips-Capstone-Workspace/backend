package com.capstone.workspace.repositories.store;

import com.capstone.workspace.dtos.shared.PaginationDto;
import com.capstone.workspace.entities.store.Store;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.repositories.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StoreRepository extends BaseRepository<Store, UUID> {
    Store findByPartnerId(String partnerId);

    PaginationResponseModel<StoreModel> getHotDealStores(PaginationDto dto);

    PaginationResponseModel<StoreModel> getQualityStores(PaginationDto dto);
}
