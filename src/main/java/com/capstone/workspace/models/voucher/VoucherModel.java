package com.capstone.workspace.models.voucher;

import com.capstone.workspace.enums.voucher.VoucherDiscountType;
import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

import java.time.Instant;

@Data
public class VoucherModel extends BaseModel {
    private int quantity;

    private VoucherDiscountType discountType;

    private Instant startDate;

    private Instant endDate;

    private int maxUsesPerUser;

    private long maxDiscountAmount;

    private long minOrderAmount;

    private String code;

    private String partnerId;

    private String storeId;
}
