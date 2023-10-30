package com.capstone.workspace.dtos.voucher;

import com.capstone.workspace.enums.voucher.VoucherDiscountType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.Instant;

@Setter(AccessLevel.NONE)
@Data
public class UpdateVoucherDto {
    @Min(1)
    private int quantity;

    private VoucherDiscountType discountType;

    private Instant startDate;

    private Instant endDate;

    @Min(1)
    private int maxUsesPerUser;

    @Min(1000)
    private Long maxDiscountAmount;

    @Min(0)
    private Long minOrderAmount;

    private Boolean isHidden;
}
