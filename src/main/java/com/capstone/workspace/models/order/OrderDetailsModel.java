package com.capstone.workspace.models.order;

import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.enums.order.TransactionMethod;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.cart.CartDetailsModel;
import com.capstone.workspace.models.shared.BaseModel;
import com.capstone.workspace.models.store.QrCodeModel;
import com.capstone.workspace.models.store.ReviewModel;
import com.capstone.workspace.models.voucher.VoucherOrderModel;
import com.capstone.workspace.services.store.ReviewService;
import lombok.Data;

import java.util.List;

@Data
public class OrderDetailsModel extends BaseModel {
    private OrderStatus status;

    private long finalPrice;

    private CartDetailsModel details;

    private QrCodeModel qrCode;

    private TransactionModel latestTransaction;

    private TransactionMethod initialTransactionMethod;

    private List<VoucherOrderModel> voucherOrders;

    private ReviewModel review;

    public void loadData() {
        ReviewModel reviewModel = BeanHelper.getBean(ReviewService.class).getOrderReview(id);
        setReview(reviewModel);
    }
}
