package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseDefault;

public interface AddBankAccountListener extends BaseListener {

    void onSuccess(ResponseDefault body);

}
