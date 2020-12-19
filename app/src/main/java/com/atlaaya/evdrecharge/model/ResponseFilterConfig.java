package com.atlaaya.evdrecharge.model;

import com.google.gson.annotations.SerializedName;

public class ResponseFilterConfig {

    @SerializedName("RESPONSE")
    private boolean RESPONSE;

    @SerializedName("RESPONSE_MSG")
    private String RESPONSE_MSG;

    @SerializedName("RESPONSE_DATA")
    private ModelFilterConfig filterConfig;

    public boolean isRESPONSE() {
        return RESPONSE;
    }

    public String getRESPONSE_MSG() {
        return RESPONSE_MSG;
    }

    public ModelFilterConfig getFilterConfig() {
        return filterConfig;
    }
}
