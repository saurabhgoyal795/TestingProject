package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseBank;

public interface BankListener extends BaseListener {

    void onSuccess(ResponseBank body);

}
