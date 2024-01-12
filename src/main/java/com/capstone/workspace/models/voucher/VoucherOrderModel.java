package com.capstone.workspace.models.voucher;

import com.capstone.workspace.enums.voucher.VoucherOrderSource;
import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

@Data
public class VoucherOrderModel extends BaseModel {
    private String description;

    private long discountAmount;

    private VoucherOrderSource source;
}
