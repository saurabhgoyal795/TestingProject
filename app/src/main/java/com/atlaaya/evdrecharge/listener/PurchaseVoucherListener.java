package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseDefault;
import com.atlaaya.evdrecharge.model.ResponseTempVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherSingle;

public interface PurchaseVoucherListener extends BaseListener {

    void onSuccessTempVoucher(ResponseTempVoucherPurchase body);

    void onSuccessVoucher(ResponseVoucherPurchase body);
    void onSuccessSinglePrintVoucher(ResponseVoucherPurchase body);

}
