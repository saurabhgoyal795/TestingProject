package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseLogin;

public interface LoginListener extends BaseListener {

    void onSuccess(ResponseLogin body);

}
