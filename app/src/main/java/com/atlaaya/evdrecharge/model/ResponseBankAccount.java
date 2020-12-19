package com.atlaaya.evdrecharge.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResponseBankAccount {

    @SerializedName("RESPONSE")
    private boolean RESPONSE;

    @SerializedName("RESPONSE_MSG")
    private String RESPONSE_MSG;

    @SerializedName("RESPONSE_DATA")
    private List<ModelBankAccount> bankAccountList;

    public boolean isRESPONSE() {
        return RESPONSE;
    }

    public String getRESPONSE_MSG() {
        return RESPONSE_MSG;
    }

    public List<ModelBankAccount> getBankAccountList() {
        return bankAccountList == null ? new ArrayList<>() : bankAccountList;
    }
}
