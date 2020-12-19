package com.atlaaya.evdrecharge.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternetConnection {

    public static boolean isInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null)
            activeNetwork = cm.getActiveNetworkInfo();

//        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static boolean isInternetConnection2(Context mContext) {
        final ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connMgr != null;
        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return wifi.isAvailable() && wifi.getState() == NetworkInfo.State.CONNECTED || mobile.isAvailable() && mobile.getState() == NetworkInfo.State.CONNECTED;
    }

    public static boolean isWifi(Context mContext) {
        final ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connMgr != null;
        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return wifi.isAvailable();
    }

    public static boolean isOtherNetwork(Context mContext) {
        final ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connMgr != null;
        final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return mobile.isAvailable();
    }
}
