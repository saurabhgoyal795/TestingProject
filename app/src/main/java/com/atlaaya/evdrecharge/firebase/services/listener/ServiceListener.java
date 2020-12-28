package com.atlaaya.evdrecharge.firebase.services.listener;

public interface ServiceListener<R> {

    void onSuccess(R successResult);

    void onError(Exception exception);

}
