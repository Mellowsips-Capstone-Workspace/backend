package com.capstone.workspace.repositories.shared;

import com.capstone.workspace.dtos.shared.PaginationDto;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Map;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID extends Serializable> extends JpaRepository<T, ID> {
    PaginationResponseModel<T> searchBy(
        String keyword,
        String[] searchFields,
        Map<String, String> filterParams,
        Map<String, Sort.Direction> orderParams,
        PaginationDto pagination
    );
}
