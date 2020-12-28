package com.atlaaya.evdrecharge.api;

import com.atlaaya.evdrecharge.model.ResponseBalance;
import com.atlaaya.evdrecharge.model.ResponseBank;
import com.atlaaya.evdrecharge.model.ResponseBankAccount;
import com.atlaaya.evdrecharge.model.ResponseDefault;
import com.atlaaya.evdrecharge.model.ResponseFilterConfig;
import com.atlaaya.evdrecharge.model.ResponseFundRequests;
import com.atlaaya.evdrecharge.model.ResponseLogin;
import com.atlaaya.evdrecharge.model.ResponseOperators;
import com.atlaaya.evdrecharge.model.ResponseServices;
import com.atlaaya.evdrecharge.model.ResponseStatements;
import com.atlaaya.evdrecharge.model.ResponseTempVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseTransactionHistory;
import com.atlaaya.evdrecharge.model.ResponseUsers;
import com.atlaaya.evdrecharge.model.ResponseVoucherOrderHistory;
import com.atlaaya.evdrecharge.model.ResponseVoucherPlan;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchaseBulk;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchaseBulkOrder;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

public interface APIInterface {

    String BASE_URL = "https://demo77.mallxs.com/evdlive/webservices/"; // Live
//    String BASE_URL = "https://evdsoftware.com/demo/webservices/"; // For Test
//    String BASE_URL = "https://evdsoftware.com/highlight/webservices/"; // For Test
//    String BASE_URL = "https://evdsoftware.com/ccf/webservices/"; // For Test
//    String BASE_URL = "http://10.133.241.148/~highlightevd/evdlive/webservices/"; //old VPN
//    String BASE_URL = "http://10.133.241.148/~highlighte/evdlive/webservices/"; // New VPN 12/5/2020
//    String BASE_URL = "http://172.16.88.118/~highlighte/evdlive/webservices/"; // New VPN 18/6/2020
   //  String BASE_URL = "http://43.252.88.118/~highlighte/evdlive/webservices/"; // New VPN 27/6/2020

    @POST("android_login")
    @Multipart
    Call<ResponseLogin> androidLogin(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_services")
    @Multipart
    Call<ResponseServices> androidServices(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_operators")
    @Multipart
    Call<ResponseOperators> androidOperators(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_user_profile")
    @Multipart
    Call<ResponseLogin> updateProfile(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_view_profile")
    @Multipart
    Call<ResponseLogin> viewProfile(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_balance")
    @Multipart
    Call<ResponseBalance> androidBalance(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_edit_password")
    @Multipart
    Call<ResponseDefault> changePassword(@PartMap() Map<String, RequestBody> partMap);

    @POST("topuprecharge")
    @Multipart
    Call<ResponseDefault> topupRecharge(@PartMap() Map<String, RequestBody> partMap);
    @POST("calculatebill")
    @Multipart
    Call<ResponseDefault> calculateElectricity(@PartMap() Map<String, RequestBody> partMap);
    @POST("submitbill")
    @Multipart
    Call<ResponseDefault> submitElectricityRecharge(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_voucher_amounts")
    @Multipart
    Call<ResponseVoucherPlan> android_voucher_amounts(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_assign_temp_voucher")
    @Multipart
    Call<ResponseTempVoucherPurchase> android_assign_temp_voucher(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_sale_voucher")
    @Multipart
    Call<ResponseVoucherPurchase> android_sale_voucher(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_voucher_order")
    @Multipart
    Call<ResponseVoucherPurchaseBulk> android_voucher_order(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_print_order")
    @Multipart
    Call<ResponseVoucherPurchaseBulkOrder> android_print_order(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_voucher_order_firebase")
    @Multipart
    Call<ResponseVoucherPurchaseBulk> android_voucher_order_firebase(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_print_order_firebase")
    @Multipart
    Call<ResponseVoucherPurchaseBulkOrder> android_print_order_firebase(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_print_vouchers")
    @Multipart
    Call<ResponseVoucherPurchase> android_print_vouchers(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_retail_users")
    @Multipart
    Call<ResponseUsers> retailUsers(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_retail_vouchers")
    @Multipart
    Call<ResponseVoucherPlan> myRetailVouchers(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_retail_voucher_orders ")
    @Multipart
    Call<ResponseVoucherOrderHistory> voucherOrdersHistory(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_retail_transactions")
    @Multipart
    Call<ResponseTransactionHistory> retailTransactions(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_retail_statements")
    @Multipart
    Call<ResponseStatements> retailStatements(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_company_banker")
    @Multipart
    Call<ResponseBankAccount> bankAccountCompany(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_user_banker")
    @Multipart
    Call<ResponseBankAccount> bankAccountUser(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_user_banker_add")
    @Multipart
    Call<ResponseDefault> bankAccountAddEdit(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_bank_list")
    @Multipart
    Call<ResponseBank> bankList(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_load_requests")
    @Multipart
    Call<ResponseFundRequests> loadFundRequests(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_load_requests_add")
    @Multipart
    Call<ResponseDefault> loadFundRequestAddEdit(@PartMap() Map<String, RequestBody> partMap);

    @POST("android_filters_config")
    @Multipart
    Call<ResponseFilterConfig> filterConfig(@PartMap() Map<String, RequestBody> partMap);


}