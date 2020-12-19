package com.atlaaya.evdrecharge.model;

import com.google.gson.annotations.SerializedName;

public class ResponseBalance {

    @SerializedName("RESPONSE")
    private boolean RESPONSE;

    @SerializedName("RESPONSE_MSG")
    private String RESPONSE_MSG;

    @SerializedName("balanceinfo")
    private ModelBalance balanceInfo;

    public boolean isRESPONSE() {
        return RESPONSE;
    }

    public String getRESPONSE_MSG() {
        return RESPONSE_MSG;
    }

    public ModelBalance getBalanceInfo() {
        return balanceInfo;
    }
}
