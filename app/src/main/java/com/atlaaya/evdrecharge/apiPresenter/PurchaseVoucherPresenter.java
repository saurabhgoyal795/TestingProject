package com.atlaaya.evdrecharge.apiPresenter;

import android.accounts.NetworkErrorException;
import android.content.Context;

import androidx.annotation.NonNull;

import com.atlaaya.evdrecharge.MyApplication;
import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.listener.PurchaseVoucherListener;
import com.atlaaya.evdrecharge.model.ResponseTempVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchase;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseVoucherPresenter extends BasePresenter<PurchaseVoucherListener> {

    public void tempVoucher(final Context context, final HashMap<String, RequestBody> requestBody) {

        getView().enableLoadingBar(context, true, context.getString(R.string.txt_please_wait));

        MyApplication.getInstance().getAPIInterface().android_assign_temp_voucher(requestBody)
                .enqueue(new Callback<ResponseTempVoucherPurchase>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseTempVoucherPurchase> call, @NonNull Response<ResponseTempVoucherPurchase> response) {
                        getView().enableLoadingBar(context, false, "");

                        if (response.code() == 402) {
                            getView().onSuccessTempVoucher(null);
                        } else if (!handleError(response)
                                && response.isSuccessful() && response.code() == 200) {
                            getView().onSuccessTempVoucher(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseTempVoucherPurchase> call, @NonNull Throwable t) {
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
                                tempVoucher(context, requestBody);
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

    public void purchaseVoucher(final Context context, final HashMap<String, RequestBody> requestBody) {

        getView().enableLoadingBar(context, true, context.getString(R.string.txt_please_wait));

        MyApplication.getInstance().getAPIInterface().android_sale_voucher(requestBody)
                .enqueue(new Callback<ResponseVoucherPurchase>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseVoucherPurchase> call, @NonNull Response<ResponseVoucherPurchase> response) {
                        getView().enableLoadingBar(context, false, "");

                        if (response.code() == 402) {
                            getView().onSuccessVoucher(null);
                        } else if (!handleError(response)
                                && response.isSuccessful() && response.code() == 200) {
                            getView().onSuccessVoucher(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseVoucherPurchase> call, @NonNull Throwable t) {
                        try {
                            t.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (t instanceof TimeoutException || t instanceof SocketTimeoutException) {
                            getView().onErrorToast(context.getString(R.string.msg_unable_connect_server));
                            getView().enableLoadingBar(context, false, "");
                        } else {
                            if (t instanceof NetworkErrorException || t instanceof SocketException) {
                                getView().onErrorToast(context.getString(R.string.msg_check_internet_connection));
                            } else {
                                getView().onErrorToast(context.getString(R.string.msg_something_went_wrong));
                            }
                            getView().enableLoadingBar(context, false, "");
                        }
                    }
                });
    }

    public void purchasedVoucherPrintDetail(final Context context, final HashMap<String, RequestBody> requestBody) {

        getView().enableLoadingBar(context, true, context.getString(R.string.txt_please_wait));

        MyApplication.getInstance().getAPIInterface().android_print_vouchers(requestBody)
                .enqueue(new Callback<ResponseVoucherPurchase>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseVoucherPurchase> call, @NonNull Response<ResponseVoucherPurchase> response) {
                        getView().enableLoadingBar(context, false, "");

                        if (response.code() == 402) {
                            getView().onSuccessVoucher(null);
                        } else if (!handleError(response)
                                && response.isSuccessful() && response.code() == 200) {
                            getView().onSuccessVoucher(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseVoucherPurchase> call, @NonNull Throwable t) {
                        try {
                            t.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (t instanceof TimeoutException || t instanceof SocketTimeoutException) {
                            getView().onErrorToast(context.getString(R.string.msg_unable_connect_server));
                            getView().enableLoadingBar(context, false, "");
                        } else {
                            if (t instanceof NetworkErrorException || t instanceof SocketException) {
                                getView().onErrorToast(context.getString(R.string.msg_check_internet_connection));
                            } else {
                                getView().onErrorToast(context.getString(R.string.msg_something_went_wrong));
                            }
                            getView().enableLoadingBar(context, false, "");
                        }
                    }
                });
    }
}
