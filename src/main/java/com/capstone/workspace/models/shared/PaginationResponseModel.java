package com.capstone.workspace.models.shared;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@Builder
public class PaginationResponseModel<T> {
    @Nullable
    private List<T> results;

    @NonNull
    private int page;

    @NonNull
    private int itemsPerPage;

    @NonNull
    private int totalItems;
}
