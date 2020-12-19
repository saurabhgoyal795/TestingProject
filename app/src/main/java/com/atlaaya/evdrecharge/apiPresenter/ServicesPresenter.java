package com.atlaaya.evdrecharge.apiPresenter;

import android.accounts.NetworkErrorException;
import android.content.Context;

import androidx.annotation.NonNull;

import com.atlaaya.evdrecharge.MyApplication;
import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.listener.ServiceListener;
import com.atlaaya.evdrecharge.model.ResponseServices;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicesPresenter extends BasePresenter<ServiceListener> {

    private static int retryCount = 0;

    public void servicesList(final Context context, final HashMap<String, RequestBody> requestBody) {

        getView().enableLoadingBar(context, true, context.getString(R.string.txt_please_wait));

        MyApplication.getInstance().getAPIInterface().androidServices(requestBody)
                .enqueue(new Callback<ResponseServices>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseServices> call, @NonNull Response<ResponseServices> response) {
                        getView().enableLoadingBar(context, false, "");

                        retryCount = 0;

                        if (response.code() == 402) {
                            getView().onSuccess(null);
                        } else if (!handleError(response)
                                && response.isSuccessful() && response.code() == 200) {
                            getView().onSuccess(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseServices> call, @NonNull Throwable t) {
                        try {
                            t.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (t instanceof TimeoutException || t instanceof SocketTimeoutException) {

                            getView().onErrorToast(context.getString(R.string.msg_unable_connect_server));
                            getView().enableLoadingBar(context, false, "");

                       /*     retryCount++;
                            if (retryCount < 3) {
                                getView().onErrorToast(context.getString(R.string.msg_internet_seems_slow));
                                servicesList(context, requestBody);
                            } else {
                                getView().onErrorToast(context.getString(R.string.msg_unable_connect_server));
                                getView().enableLoadingBar(context, false, "");
                            }*/
                        } else {
                            if (t instanceof NetworkErrorException || t instanceof SocketException) {
                                getView().onErrorToast(context.getString(R.string.msg_check_internet_connection));
                            } else {
                                getView().onErrorToast(context.getString(R.string.msg_something_went_wrong));
                            }
//                            retryCount = 3;
                            getView().enableLoadingBar(context, false, "");
                        }
                    }
                });
    }
}
