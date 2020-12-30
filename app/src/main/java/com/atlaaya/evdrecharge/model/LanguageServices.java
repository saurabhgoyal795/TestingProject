package com.atlaaya.evdrecharge.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LanguageServices {

    @SerializedName("RESPONSE")
    private JSONObject RESPONSE;

    public JSONObject getRESPONSE() {
        return RESPONSE;
    }

}

