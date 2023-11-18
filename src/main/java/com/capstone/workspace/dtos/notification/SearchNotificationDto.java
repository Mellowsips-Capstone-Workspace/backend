package com.capstone.workspace.dtos.notification;

import com.capstone.workspace.dtos.shared.PaginationDto;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class SearchNotificationDto {
    @Valid
    private PaginationDto pagination;
}
