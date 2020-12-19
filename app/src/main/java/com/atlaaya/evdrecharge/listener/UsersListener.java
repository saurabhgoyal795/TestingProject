package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseUsers;

public interface UsersListener extends BaseListener {

    void onSuccess(ResponseUsers body);

}
