package com.capstone.workspace.models.order;

import com.capstone.workspace.entities.store.Review;
import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.enums.order.TransactionMethod;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.cart.CartDetailsModel;
import com.capstone.workspace.models.shared.BaseModel;
import com.capstone.workspace.models.store.QrCodeModel;
import com.capstone.workspace.models.store.ReviewModel;
import com.capstone.workspace.models.voucher.VoucherOrderModel;
import com.capstone.workspace.services.store.ReviewService;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderModel extends BaseModel implements Serializable {
    private OrderStatus status;

    private long finalPrice;

    private CartDetailsModel details;

    private QrCodeModel qrCode;

    private TransactionModel latestTransaction;

    private List<VoucherOrderModel> voucherOrders;

    private String rejectReason;

    private TransactionMethod initialTransactionMethod;

    private String partnerId;

    private String storeId;

    private ReviewModel review;

    public void loadData() {
        ReviewModel reviewModel = BeanHelper.getBean(ReviewService.class).getOrderReview(id);
        setReview(reviewModel);
    }
}
