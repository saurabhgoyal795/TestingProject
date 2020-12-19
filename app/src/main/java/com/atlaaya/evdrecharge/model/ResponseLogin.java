package com.atlaaya.evdrecharge.model;

import com.google.gson.annotations.SerializedName;

public class ResponseLogin {

    @SerializedName("RESPONSE")
    private boolean RESPONSE;

    @SerializedName("RESPONSE_MSG")
    private String RESPONSE_MSG;

    @SerializedName("userInfo")
    private ModelUserInfo userInfo;

    public boolean isRESPONSE() {
        return RESPONSE;
    }

    public String getRESPONSE_MSG() {
        return RESPONSE_MSG;
    }

    public ModelUserInfo getUserInfo() {
        return userInfo;
    }
}
