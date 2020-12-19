package com.atlaaya.evdrecharge.listener;

import android.content.Context;

public interface BaseListener {

    Context getContext();

    void enableLoadingBar(Context context, boolean enableLoadingBar, String s);

    void onError(String reason);

    void onErrorToast(String reason);

    void dialogAccountDeactivate(String reason);
}
