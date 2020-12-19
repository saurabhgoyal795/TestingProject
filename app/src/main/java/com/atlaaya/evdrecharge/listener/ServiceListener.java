package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseServices;

public interface ServiceListener extends BaseListener {

    void onSuccess(ResponseServices body);

}
