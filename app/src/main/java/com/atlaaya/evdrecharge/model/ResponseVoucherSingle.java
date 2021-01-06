package com.atlaaya.evdrecharge.model;


import com.google.gson.annotations.SerializedName;

public class ResponseVoucherSingle  {

    @SerializedName("RESPONSE")
    private boolean RESPONSE;

    @SerializedName("RESPONSE_MSG")
    private String RESPONSE_MSG;


    @SerializedName("RESPONSE_DATA")
    private ModelVoucherSingle RESPONSE_DATA;
    public ResponseVoucherSingle() {
    }


    public boolean isRESPONSE() {
        return RESPONSE;
    }

    public String getRESPONSE_MSG() {
        return RESPONSE_MSG;
    }

    public ModelVoucherSingle getRESPONSE_DATA() {
        return RESPONSE_DATA;
    }
}
