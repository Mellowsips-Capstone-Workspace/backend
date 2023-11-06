package com.capstone.workspace.dtos.store;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Setter(AccessLevel.NONE)
@Data
public class CreateMenuDto {
    @Valid
    @NotEmpty
    private List<CreateMenuSectionDto> menuSections;
}
