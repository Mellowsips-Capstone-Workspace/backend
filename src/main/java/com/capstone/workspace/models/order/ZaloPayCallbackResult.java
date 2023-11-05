package com.capstone.workspace.models.order;

import lombok.Data;

@Data
public class ZaloPayCallbackResult {
    private String data;

    private String mac;

    private int type;
}