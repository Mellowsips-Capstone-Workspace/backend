package com.capstone.workspace.dtos.order;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.UUID;

@Setter(AccessLevel.NONE)
@Data
public class CreateOrderDto {
    private UUID cartId;
}
