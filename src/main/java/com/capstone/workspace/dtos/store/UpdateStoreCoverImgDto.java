package com.capstone.workspace.dtos.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStoreCoverImgDto {
    @NotNull
    @NotBlank
    private String coverImage;
}
