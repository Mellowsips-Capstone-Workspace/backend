package com.capstone.workspace.dtos.store;

import com.capstone.workspace.models.shared.Period;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Setter(AccessLevel.NONE)
@Data
public class UpdateStoreOperationalHoursDto {
    @NotNull
    @NotEmpty
    private Map<DayOfWeek, List<Period<String>>> operationalHours;
}
