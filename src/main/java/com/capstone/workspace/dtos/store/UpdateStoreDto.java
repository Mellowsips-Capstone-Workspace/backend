package com.capstone.workspace.dtos.store;

import com.capstone.workspace.models.shared.Period;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Data
public class UpdateStoreDto {
    private String profileImage;

    private String coverImage;

    private Map<DayOfWeek, List<Period<String>>> operationalHours;

    private List<String> categories;
}
