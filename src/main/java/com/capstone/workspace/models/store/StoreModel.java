package com.capstone.workspace.models.store;

import com.capstone.workspace.models.shared.BaseModel;
import com.capstone.workspace.models.shared.Period;
import com.capstone.workspace.models.voucher.VoucherModel;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Data
public class StoreModel extends BaseModel {
    private String name;

    private String phone;

    private String email;

    private String address;

    private String profileImage;

    private String coverImage;

    private List<String> categories;

    private Boolean isActive;

    private Boolean isOpen;

    private Map<DayOfWeek, List<Period>> operationalHours;

    private String partnerId;

    private StoreReviewStatisticsModel reviewStatistic;

    private List<VoucherModel> vouchers;
}
