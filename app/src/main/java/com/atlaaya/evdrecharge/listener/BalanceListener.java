package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseBalance;

public interface BalanceListener extends BaseListener {

    void onBalanceSuccess(ResponseBalance body);

}
