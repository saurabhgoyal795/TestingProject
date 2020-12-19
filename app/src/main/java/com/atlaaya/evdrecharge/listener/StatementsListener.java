package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseStatements;

public interface StatementsListener extends BaseListener {

    void onStatementListSuccess(ResponseStatements body);

}
