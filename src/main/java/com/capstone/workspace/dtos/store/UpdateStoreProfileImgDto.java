package com.capstone.workspace.dtos.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class UpdateStoreProfileImgDto {
    @NotNull
    @NotBlank
    private String profileImage;
}
