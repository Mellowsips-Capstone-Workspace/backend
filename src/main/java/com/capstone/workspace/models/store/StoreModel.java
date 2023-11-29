package com.capstone.workspace.models.store;

import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.helpers.store.StoreHelper;
import com.capstone.workspace.models.shared.BaseModel;
import com.capstone.workspace.models.shared.Period;
import com.capstone.workspace.models.voucher.VoucherModel;
import com.capstone.workspace.services.store.ReviewService;
import com.capstone.workspace.services.voucher.VoucherService;
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

    public void loadData() {
        List<VoucherModel> voucherModels = BeanHelper.getBean(VoucherService.class).getBusinessVouchersOfStore(getPartnerId(), String.valueOf(getId()));
        setVouchers(voucherModels);

        StoreReviewStatisticsModel reviewStatistics = BeanHelper.getBean(ReviewService.class).getStoreReviewStatistics(String.valueOf(getId()));
        setReviewStatistic(reviewStatistics);

        Boolean isOpen = BeanHelper.getBean(StoreHelper.class).isStoreOpening(getOperationalHours());
        setIsOpen(isOpen);
    }
}
