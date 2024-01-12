package com.capstone.workspace.dtos.voucher;

import com.capstone.workspace.enums.voucher.VoucherDiscountType;
import lombok.Data;

@Data
public class SearchVoucherFilterDto {
    private VoucherDiscountType discountType;

    private String storeId;

    private Long value;
}
