package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseTransactionHistory;

public interface TransactionListener extends BaseListener {

    void onTransactionListSuccess(ResponseTransactionHistory body);

}
