package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseVoucherPlan;

public interface VoucherPlanListener extends BaseListener {

    void onSuccess(ResponseVoucherPlan body);

}
