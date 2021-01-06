package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseDefault;

public interface MobileRechargeListner extends BaseListener {

    void onSuccess(ResponseDefault body);
    void onSuccessVoucherPrint(ResponseDefault body);

}
