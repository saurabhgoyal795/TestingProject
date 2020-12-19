package com.atlaaya.evdrecharge.model;


import com.google.gson.annotations.SerializedName;

public class ResponseVoucherPurchaseBulk {

    @SerializedName("RESPONSE")
    private boolean RESPONSE;

    @SerializedName("RESPONSE_MSG")
    private String RESPONSE_MSG;

    @SerializedName("RESPONSE_DATA")
    private ModelVoucherPurchased voucherPurchased;

    public boolean isRESPONSE() {
        return RESPONSE;
    }

    public String getRESPONSE_MSG() {
        return RESPONSE_MSG;
    }

    public ModelVoucherPurchased getVoucherPurchased() {
        return voucherPurchased;
    }
}
