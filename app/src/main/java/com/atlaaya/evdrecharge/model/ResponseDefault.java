package com.atlaaya.evdrecharge.model;


import com.google.gson.annotations.SerializedName;

public class ResponseDefault {

    @SerializedName("RESPONSE")
    private boolean RESPONSE;

    @SerializedName("RESPONSE_MSG")
    private String RESPONSE_MSG;


    public boolean isRESPONSE() {
        return RESPONSE;
    }

    public String getRESPONSE_MSG() {
        return RESPONSE_MSG;
    }

}
