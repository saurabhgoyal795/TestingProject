package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseFundRequests;

public interface FundRequestsListener extends BaseListener {

    void onSuccess(ResponseFundRequests body);

}
