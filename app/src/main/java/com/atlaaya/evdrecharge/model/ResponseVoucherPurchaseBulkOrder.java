package com.atlaaya.evdrecharge.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseVoucherPurchaseBulkOrder {

    @SerializedName("RESPONSE")
    private boolean RESPONSE;

    @SerializedName("RESPONSE_MSG")
    private String RESPONSE_MSG;

    @SerializedName("RESPONSE_DATA")
    private ArrayList<ModelVoucherPurchased> voucherPurchasedList;

    public boolean isRESPONSE() {
        return RESPONSE;
    }

    public String getRESPONSE_MSG() {
        return RESPONSE_MSG;
    }

    public ArrayList<ModelVoucherPurchased> getVoucherPurchasedList() {
        return voucherPurchasedList == null ? new ArrayList<>() : voucherPurchasedList;
    }
}
