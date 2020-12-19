package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseBankAccount;
import com.atlaaya.evdrecharge.model.ResponseServices;

public interface BankAccountListener extends BaseListener {

    void onSuccess(ResponseBankAccount body);

}
