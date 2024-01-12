package com.capstone.workspace.models.order;

import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.enums.order.TransactionMethod;
import com.capstone.workspace.enums.order.TransactionStatus;
import com.capstone.workspace.enums.order.TransactionType;
import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

import java.util.Map;

@Data
public class TransactionModel extends BaseModel {
    private long amount;

    private TransactionMethod method;

    private TransactionType type;

    private TransactionStatus status;

    private Map<String, Object> externalPaymentInfo;

    private String partnerId;

    private String storeId;
}
