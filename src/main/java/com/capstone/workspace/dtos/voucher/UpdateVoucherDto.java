package com.capstone.workspace.dtos.voucher;

import com.capstone.workspace.enums.voucher.VoucherDiscountType;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.Instant;

@Data
public class UpdateVoucherDto {
    @Min(1)
    private Long value;

    @Min(1)
    private int quantity;

    private VoucherDiscountType discountType;

    private Instant startDate;

    private Instant endDate;

    @Min(1)
    private int maxUsesPerUser;

    private Long maxDiscountAmount;

    @Min(0)
    private Long minOrderAmount;

    private Boolean isHidden;
}
