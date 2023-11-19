package com.capstone.workspace.dtos.voucher;

import com.capstone.workspace.enums.voucher.VoucherOrderSource;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class CreateVoucherOrderDto {
    private String description;

    private long discountAmount;

    private VoucherOrderSource source;

    private UUID voucherId;
}
