package com.capstone.workspace.models.order;

import lombok.Data;

@Data
public class ZaloPayCallbackData {
    private int app_id;

    private String app_trans_id;

    private long app_time;

    private String app_user;

    private long amount;

    private String embed_data;

    private String item;

    private long zp_trans_id;

    private long server_time;

    private int channel;

    private String merchant_user_id;

    private String zp_user_id;

    private long user_fee_amount;

    private long discount_amount;
}