package com.atlaaya.evdrecharge.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResponseVoucherOrderHistory {

    @SerializedName("RESPONSE")
    private boolean RESPONSE;

    @SerializedName("RESPONSE_MSG")
    private String RESPONSE_MSG;

    @SerializedName("RESPONSE_DATA")
    private List<ModelVoucherOrder> voucherOrderList;

    public boolean isRESPONSE() {
        return RESPONSE;
    }

    public String getRESPONSE_MSG() {
        return RESPONSE_MSG;
    }

    public List<ModelVoucherOrder> getVoucherOrderList() {
        return voucherOrderList == null ? new ArrayList<>() : voucherOrderList;
    }
}
