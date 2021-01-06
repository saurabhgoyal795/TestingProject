package com.atlaaya.evdrecharge.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.apiPresenter.MobileRechargePresenter;
import com.atlaaya.evdrecharge.apiPresenter.PurchaseVoucherPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivitySinglePrintBinding;
import com.atlaaya.evdrecharge.databinding.ActivityTopUpOptionBinding;
import com.atlaaya.evdrecharge.listener.MobileRechargeListner;
import com.atlaaya.evdrecharge.listener.PurchaseVoucherListener;
import com.atlaaya.evdrecharge.model.ModelOperator;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ModelVoucherPurchased;
import com.atlaaya.evdrecharge.model.ModelVoucherSingle;
import com.atlaaya.evdrecharge.model.ResponseDefault;
import com.atlaaya.evdrecharge.model.ResponseTempVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherSingle;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.DisableEmojiInEditText;
import com.atlaaya.evdrecharge.utils.MyCustomToast;
import com.atlaaya.evdrecharge.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SinglePrint extends BaseActivity implements View.OnClickListener, PurchaseVoucherListener {
    private ActivitySinglePrintBinding binding;
    private ModelService selectedService;
    private ModelOperator selectedOperator;
    private PurchaseVoucherPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_print);
        presenter = new PurchaseVoucherPresenter();
        presenter.setView(this);
        if (getIntent().hasExtra("service")) {
            selectedService = getIntent().getParcelableExtra("service");
        }
        if (getIntent().hasExtra("operator")) {
            selectedOperator = getIntent().getParcelableExtra("operator");
        }
        setSupportActionBar(binding.toolbar);

        if (selectedService != null) {
            binding.toolbar.setTitle(selectedService.getService_name());
        } else {
            binding.toolbar.setTitle(getString(R.string.txt_print_screen));
        }

        binding.etMobile.setFilters(new InputFilter[]{new DisableEmojiInEditText()});


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnProceed) {
            proceed();
        }
    }

    private void proceed() {
        String mobile_number = binding.etMobile.getText().toString();

        if (mobile_number.isEmpty()) {
            binding.etMobile.requestFocus();
            binding.etMobile.setError(getText(R.string.alert_enter_mobile_number));
        }  else if (CheckInternetConnection.isInternetConnection(this)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(this);
            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                map.put("username", RequestBody.create(MediaType.parse("multipart/form-data"), userInfo.getUsername()));
//
               map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"),userInfo.getEmail()));
                map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_PASSWORD)));

                map.put("voucher_serial_number", RequestBody.create(MediaType.parse("multipart/form-data"), "" + mobile_number));
                presenter.purchasedVoucherSinglePrintDetail(this, map);
//                Intent intent = new Intent(this, SendingRequestActivity.class);
//                intent.putExtra("service", selectedService);
//                intent.putExtra("operator", selectedOperator);
//                intent.putExtra("mobile_number", mobile_number);
//                intent.putExtra("top_up_amount", top_up_amount);
//                intent.putExtra("user_id", userInfo.getId());
//                startActivityForResult(intent, 200);
            }
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void onSuccessTempVoucher(ResponseTempVoucherPurchase body) {

    }

    @Override
    public void onSuccessVoucher(ResponseVoucherPurchase body) {
        if (body.isRESPONSE()) {
          ModelVoucherPurchased data =body.getRESPONSE_DATA();

        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
    }

    @Override
    public void onSuccessSinglePrintVoucher(ResponseVoucherPurchase body) {
        if (body.isRESPONSE()) {
            this.runOnUiThread(() -> {
                Intent intent = new Intent(this, VoucherSuccessPrintActivity.class);

                ArrayList<ModelVoucherPurchased> printDataList = new ArrayList<>();
                printDataList.add(body.getRESPONSE_DATA());

                VoucherSuccessPrintActivity.setPrintVoucherContentArrayList(printDataList);
                 intent.putExtra("fromHistory", true);
                startActivity(intent);
            });


        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
    }
}