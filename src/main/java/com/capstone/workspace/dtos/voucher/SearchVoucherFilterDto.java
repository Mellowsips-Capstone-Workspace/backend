package com.capstone.workspace.dtos.voucher;

import com.capstone.workspace.enums.voucher.VoucherDiscountType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class SearchVoucherFilterDto {
    private VoucherDiscountType discountType;
}
