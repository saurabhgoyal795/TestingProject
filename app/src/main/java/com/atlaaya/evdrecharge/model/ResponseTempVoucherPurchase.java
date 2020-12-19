package com.atlaaya.evdrecharge.model;


import com.google.gson.annotations.SerializedName;

public class ResponseTempVoucherPurchase {

    @SerializedName("RESPONSE")
    private boolean RESPONSE;

    @SerializedName("RESPONSE_MSG")
    private String RESPONSE_MSG;

    @SerializedName("RESPONSE_DATA")
    private int RESPONSE_DATA;


    public boolean isRESPONSE() {
        return RESPONSE;
    }

    public String getRESPONSE_MSG() {
        return RESPONSE_MSG;
    }

    public int getRESPONSE_DATA() {
        return RESPONSE_DATA;
    }
}
