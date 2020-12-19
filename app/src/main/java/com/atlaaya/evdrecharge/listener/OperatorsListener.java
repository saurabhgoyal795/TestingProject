package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseOperators;

public interface OperatorsListener extends BaseListener {

    void onSuccess(ResponseOperators body);

}
