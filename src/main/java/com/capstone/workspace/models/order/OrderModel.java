package com.capstone.workspace.models.order;

import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.models.cart.CartDetailsModel;
import com.capstone.workspace.models.shared.BaseModel;
import com.capstone.workspace.models.store.QrCodeModel;
import lombok.Data;

@Data
public class OrderModel extends BaseModel {
    private OrderStatus status;

    private long finalPrice;

    private CartDetailsModel details;

    private QrCodeModel qrCode;
}
