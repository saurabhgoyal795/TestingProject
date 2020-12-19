package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseLogin;

public interface UpdateProfileListener extends BaseListener {

    void onSuccess(ResponseLogin body);

}
