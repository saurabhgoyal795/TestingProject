package com.atlaaya.evdrecharge.listener;


import com.atlaaya.evdrecharge.model.ResponseTempVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherOrderHistory;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchaseBulk;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchaseBulkOrder;

public interface PurchaseVoucherBulkListener extends BaseListener {

    void onSuccessVoucherBulkOrder(ResponseVoucherPurchaseBulk body);

    void onSuccessVoucherBulkOrderPrintDetail(ResponseVoucherPurchaseBulkOrder body);

    void onSuccessVoucherBulkOrderHistory(ResponseVoucherOrderHistory body);

}
