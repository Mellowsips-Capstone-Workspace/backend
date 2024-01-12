package com.capstone.workspace.models.voucher;

import com.capstone.workspace.enums.voucher.VoucherDiscountType;
import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
public class VoucherModel extends BaseModel {
    private int quantity;

    private int originalQuantity;

    private VoucherDiscountType discountType;

    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    private long value;

    private int maxUsesPerUser;

    private long maxDiscountAmount;

    private long minOrderAmount;

    private String code;

    private String partnerId;

    private String storeId;

    private int version;

    private Boolean isHidden;

    public void setStartDate(Instant startDate) {
        this.startDate = startDate == null ? null : startDate.atZone(ZoneId.of("Asia/Saigon"));
    }
    public void setEndDate(Instant endDate) {
        this.endDate = endDate == null ? null : endDate.atZone(ZoneId.of("Asia/Saigon"));
    }
}
