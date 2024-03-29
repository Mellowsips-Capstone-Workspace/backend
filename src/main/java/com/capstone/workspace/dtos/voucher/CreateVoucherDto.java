package com.capstone.workspace.dtos.voucher;

import com.capstone.workspace.enums.voucher.VoucherDiscountType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
public class CreateVoucherDto {
    @NotNull
    @Min(1)
    private Long value;

    @NotNull
    @Min(1)
    private int quantity;

    private VoucherDiscountType discountType;

    private Instant startDate;

    private Instant endDate;

    @NotNull
    @Min(1)
    private int maxUsesPerUser;

    private Long maxDiscountAmount;

    @NotNull
    @Min(0)
    private Long minOrderAmount;

    @NotNull
    @Size(max = 9, min = 5)
    private String code;

    private Boolean isHidden;

    private String storeId;
}
