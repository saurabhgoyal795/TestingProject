package com.atlaaya.evdrecharge.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResponseVoucherPlan {

    @SerializedName("RESPONSE")
    private boolean RESPONSE;

    @SerializedName("RESPONSE_MSG")
    private String RESPONSE_MSG;

    @SerializedName("RESPONSE_DATA")
    private List<ModelVoucherPlan> voucherPlanList;

    public boolean isRESPONSE() {
        return RESPONSE;
    }

    public String getRESPONSE_MSG() {
        return RESPONSE_MSG;
    }

    public List<ModelVoucherPlan> getVoucherPlanList() {
        return voucherPlanList == null ? new ArrayList<>() : voucherPlanList;
    }
}
