package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseFilterConfig;

public interface FilterConfigListener extends BaseListener {

    void onFilterConfigSuccess(ResponseFilterConfig body);

}
