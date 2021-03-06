package com.atlaaya.evdrecharge.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.atlaaya.evdrecharge.MyApplication;
import com.atlaaya.evdrecharge.model.ModelBalance;
import com.atlaaya.evdrecharge.model.ModelFilterConfig;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


public class SessionManager {

    private static SharedPreferences mSharedPreferences;
    private Context mContext;
    public static String KEY_EMAIL  = "email";
    public static String KEY_PASSWORD = "password";
    public static String KEY_URL  = "url";
    public static String KEY_LANGUAGE_DATA = "{}";
    public static String KEY_PRINTER  = "printer";
    public static String KEY_LANGUAGE  = "langauge";

    public SessionManager(Context context) {
        this.mContext = context;
    }

    public static SharedPreferences getSharedPref(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences("Highlight EVD", Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    public static void saveLoginDetail(Context context, String email, String password) {
        getSharedPref(context).edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .apply();
    }
    public static void saveUrl(Context context, String url) {
        getSharedPref(context).edit()
                .putString(KEY_URL, url)
                .apply();
    }

    public static void saveLanguageData(Context context, String data) {
        getSharedPref(context).edit()
                .putString(KEY_LANGUAGE_DATA, data)
                .apply();
    }

    public static String getLanguageData(Context context) {
        String json = getSharedPref(context).getString(KEY_LANGUAGE_DATA, "{}");
        return json;
    }

    public static void savePrinter(Context context, String url) {
        getSharedPref(context).edit()
                .putString(KEY_PRINTER, url)
                .apply();
    }



    public static String getPrinter(Context context) {
        String json = getSharedPref(context).getString(KEY_PRINTER, null);
        return json;
    }


    public static void saveLanguage(Context context, String url) {
        getSharedPref(context).edit()
                .putString(KEY_LANGUAGE, url)
                .apply();
    }



    public static String getLangauge(Context context) {
        String json = getSharedPref(context).getString(KEY_LANGUAGE, null);
        return json;
    }





    public static String getUrl(Context context) {
        String json = getSharedPref(context).getString(KEY_URL, MyApplication.BASE_URL);
        return json;
    }


    public static void saveUserDetail(Context context, ModelUserInfo userInfo) {
        Gson gson = new Gson();
        String json = gson.toJson(userInfo);
        getSharedPref(context).edit().putString("userInfo", json).apply();
    }

    public static ModelUserInfo getUserDetail(Context context) {
        Gson gson = new Gson();
        String json = getSharedPref(context).getString("userInfo", null);
        Type type = new TypeToken<ModelUserInfo>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void saveBalance(Context context, ModelBalance balance) {
        Gson gson = new Gson();
        String json = gson.toJson(balance);
        getSharedPref(context).edit().putString("balanceInfo", json).apply();
    }

    public static ModelBalance getBalance(Context context) {
        Gson gson = new Gson();
        String json = getSharedPref(context).getString("balanceInfo", null);
        Type type = new TypeToken<ModelBalance>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void saveFilterConfig(Context context, ModelFilterConfig filterConfig) {
        Gson gson = new Gson();
        String json = gson.toJson(filterConfig);
        getSharedPref(context).edit().putString("filterConfig", json).apply();
    }

    public static ModelFilterConfig getFilterConfig(Context context) {
        Gson gson = new Gson();
        String json = getSharedPref(context).getString("filterConfig", null);
        Type type = new TypeToken<ModelFilterConfig>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static String getString(Context context, String key) {
        return getSharedPref(context).getString(key, "");
    }

    public static int getInt(Context context, String key) {
        return getSharedPref(context).getInt(key, 0);
    }

    public static void clearAll(Context context) {
        getSharedPref(context).edit()
                .clear()
                .apply();
    }

}
