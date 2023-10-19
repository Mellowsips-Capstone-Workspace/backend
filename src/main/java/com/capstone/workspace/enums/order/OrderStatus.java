package com.capstone.workspace.enums.order;

public enum OrderStatus {
    // Initial state
    INITIAL,

    // Real states
    PENDING,
    ORDERED,
    CANCELED,
    REJECTED,
    PROCESSING,
    COMPLETED,
    RECEIVED,
    DECLINED
}
