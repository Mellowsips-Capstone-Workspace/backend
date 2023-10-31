package com.capstone.workspace.dtos.store;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class UpdateStoreProfileImgDto {
    private String profileImage;
}
