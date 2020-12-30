package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.LanguageServices;
import com.atlaaya.evdrecharge.model.ResponseServices;

public interface LanguageListener extends BaseListener {

    void onSuccess(LanguageServices body);

}
