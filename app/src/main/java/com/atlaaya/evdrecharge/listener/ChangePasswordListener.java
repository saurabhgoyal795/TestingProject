package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseDefault;

public interface ChangePasswordListener extends BaseListener {

    void onSuccess(ResponseDefault body);

}
