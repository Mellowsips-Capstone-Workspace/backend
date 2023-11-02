package com.capstone.workspace.dtos.notification;

import com.capstone.workspace.dtos.shared.PaginationDto;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class SearchNotificationDto {
    @Valid
    private PaginationDto pagination;
}
