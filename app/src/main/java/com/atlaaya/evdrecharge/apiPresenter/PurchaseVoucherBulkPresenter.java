package com.atlaaya.evdrecharge.apiPresenter;

import android.accounts.NetworkErrorException;
import android.content.Context;

import androidx.annotation.NonNull;

import com.atlaaya.evdrecharge.MyApplication;
import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.listener.PurchaseVoucherBulkListener;
import com.atlaaya.evdrecharge.model.ResponseVoucherOrderHistory;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchaseBulk;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchaseBulkOrder;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseVoucherBulkPresenter extends BasePresenter<PurchaseVoucherBulkListener> {

    public void voucherBulkOrder(final Context context, final HashMap<String, RequestBody> requestBody) {

        getView().enableLoadingBar(context, true, context.getString(R.string.txt_please_wait));

        MyApplication.getInstance().getAPIInterface().android_voucher_order(requestBody)
                .enqueue(new Callback<ResponseVoucherPurchaseBulk>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseVoucherPurchaseBulk> call, @NonNull Response<ResponseVoucherPurchaseBulk> response) {
                        getView().enableLoadingBar(context, false, "");

                        if (response.code() == 402) {
                            getView().onSuccessVoucherBulkOrder(null);
                        } else if (!handleError(response)
                                && response.isSuccessful() && response.code() == 200) {
                            getView().onSuccessVoucherBulkOrder(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseVoucherPurchaseBulk> call, @NonNull Throwable t) {
                        try {
                            getView().enableLoadingBar(context, false, "");
                            if (t instanceof TimeoutException || t instanceof SocketTimeoutException) {
                                getView().onErrorToast(context.getString(R.string.msg_unable_connect_server));
                            } else {
                                if (t instanceof NetworkErrorException || t instanceof SocketException) {
                                    getView().onErrorToast(context.getString(R.string.msg_check_internet_connection));
                                } else {
                                    getView().onErrorToast(context.getString(R.string.msg_something_went_wrong));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void voucherBulkOrderDetail(final Context context, final HashMap<String, RequestBody> requestBody) {

        getView().enableLoadingBar(context, true, context.getString(R.string.txt_please_wait));

        MyApplication.getInstance().getAPIInterface().android_print_order(requestBody)
                .enqueue(new Callback<ResponseVoucherPurchaseBulkOrder>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseVoucherPurchaseBulkOrder> call, @NonNull Response<ResponseVoucherPurchaseBulkOrder> response) {
                        getView().enableLoadingBar(context, false, "");

                        if (response.code() == 402) {
                            getView().onSuccessVoucherBulkOrderPrintDetail(null);
                        } else if (!handleError(response)
                                && response.isSuccessful() && response.code() == 200) {
                            getView().onSuccessVoucherBulkOrderPrintDetail(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseVoucherPurchaseBulkOrder> call, @NonNull Throwable t) {
                        try {
                            getView().enableLoadingBar(context, false, "");
                            if (t instanceof TimeoutException || t instanceof SocketTimeoutException) {
                                getView().onErrorToast(context.getString(R.string.msg_unable_connect_server));
                            } else {
                                if (t instanceof NetworkErrorException || t instanceof SocketException) {
                                    getView().onErrorToast(context.getString(R.string.msg_check_internet_connection));
                                } else {
                                    getView().onErrorToast(context.getString(R.string.msg_something_went_wrong));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void voucherOrderHistory(final Context context, final HashMap<String, RequestBody> requestBody) {

        getView().enableLoadingBar(context, true, context.getString(R.string.txt_please_wait));

        MyApplication.getInstance().getAPIInterface().voucherOrdersHistory(requestBody)
                .enqueue(new Callback<ResponseVoucherOrderHistory>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseVoucherOrderHistory> call, @NonNull Response<ResponseVoucherOrderHistory> response) {
                        getView().enableLoadingBar(context, false, "");

                        if (response.code() == 402) {
                            getView().onSuccessVoucherBulkOrderHistory(null);
                        } else if (!handleError(response)
                                && response.isSuccessful() && response.code() == 200) {
                            getView().onSuccessVoucherBulkOrderHistory(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseVoucherOrderHistory> call, @NonNull Throwable t) {
                        try {
                            getView().enableLoadingBar(context, false, "");
                            if (t instanceof TimeoutException || t instanceof SocketTimeoutException) {
                                getView().onErrorToast(context.getString(R.string.msg_unable_connect_server));
                            } else {
                                if (t instanceof NetworkErrorException || t instanceof SocketException) {
                                    getView().onErrorToast(context.getString(R.string.msg_check_internet_connection));
                                } else {
                                    getView().onErrorToast(context.getString(R.string.msg_something_went_wrong));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void voucherBulkOrderFirebase(final Context context, final HashMap<String, RequestBody> requestBody) {

        getView().enableLoadingBar(context, true, context.getString(R.string.txt_please_wait));

        MyApplication.getInstance().getAPIInterface().android_voucher_order_firebase(requestBody)
                .enqueue(new Callback<ResponseVoucherPurchaseBulk>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseVoucherPurchaseBulk> call, @NonNull Response<ResponseVoucherPurchaseBulk> response) {
                        getView().enableLoadingBar(context, false, "");

                        if (response.code() == 402) {
                            getView().onSuccessVoucherBulkOrder(null);
                        } else if (!handleError(response)
                                && response.isSuccessful() && response.code() == 200) {
                            getView().onSuccessVoucherBulkOrder(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseVoucherPurchaseBulk> call, @NonNull Throwable t) {
                        try {
                            getView().enableLoadingBar(context, false, "");
                            if (t instanceof TimeoutException || t instanceof SocketTimeoutException) {
                                getView().onErrorToast(context.getString(R.string.msg_unable_connect_server));
                            } else {
                                if (t instanceof NetworkErrorException || t instanceof SocketException) {
                                    getView().onErrorToast(context.getString(R.string.msg_check_internet_connection));
                                } else {
                                    getView().onErrorToast(context.getString(R.string.msg_something_went_wrong));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void voucherBulkOrderDetailFirebase(final Context context, final HashMap<String, RequestBody> requestBody) {

        getView().enableLoadingBar(context, true, context.getString(R.string.txt_please_wait));

        MyApplication.getInstance().getAPIInterface().android_print_order_firebase(requestBody)
                .enqueue(new Callback<ResponseVoucherPurchaseBulkOrder>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseVoucherPurchaseBulkOrder> call, @NonNull Response<ResponseVoucherPurchaseBulkOrder> response) {
                        getView().enableLoadingBar(context, false, "");

                        if (response.code() == 402) {
                            getView().onSuccessVoucherBulkOrderPrintDetail(null);
                        } else if (!handleError(response)
                                && response.isSuccessful() && response.code() == 200) {
                            getView().onSuccessVoucherBulkOrderPrintDetail(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseVoucherPurchaseBulkOrder> call, @NonNull Throwable t) {
                        try {
                            getView().enableLoadingBar(context, false, "");
                            if (t instanceof TimeoutException || t instanceof SocketTimeoutException) {
                                getView().onErrorToast(context.getString(R.string.msg_unable_connect_server));
                            } else {
                                if (t instanceof NetworkErrorException || t instanceof SocketException) {
                                    getView().onErrorToast(context.getString(R.string.msg_check_internet_connection));
                                } else {
                                    getView().onErrorToast(context.getString(R.string.msg_something_went_wrong));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
