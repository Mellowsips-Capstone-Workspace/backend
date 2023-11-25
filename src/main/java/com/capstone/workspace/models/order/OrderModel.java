package com.capstone.workspace.models.order;

import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.models.cart.CartDetailsModel;
import com.capstone.workspace.models.shared.BaseModel;
import com.capstone.workspace.models.store.QrCodeModel;
import com.capstone.workspace.models.voucher.VoucherOrderModel;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderModel extends BaseModel {
    private OrderStatus status;

    private long finalPrice;

    private CartDetailsModel details;

    private QrCodeModel qrCode;

    private TransactionModel latestTransaction;

    private List<VoucherOrderModel> voucherOrders;

    private String rejectReason;

    private String partnerId;

    private String storeId;
}
