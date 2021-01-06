package com.atlaaya.evdrecharge.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.apiPresenter.ChangePasswordPresenter;
import com.atlaaya.evdrecharge.apiPresenter.MobileRechargePresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityTopUpOptionBinding;
import com.atlaaya.evdrecharge.listener.MobileRechargeListner;
import com.atlaaya.evdrecharge.model.ModelOperator;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ResponseDefault;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.DisableEmojiInEditText;
import com.atlaaya.evdrecharge.utils.MyCustomToast;
import com.atlaaya.evdrecharge.utils.Utility;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TopUpOptionActivity extends BaseActivity implements View.OnClickListener, MobileRechargeListner {

    private ActivityTopUpOptionBinding binding;

    private ModelService selectedService;
    private ModelOperator selectedOperator;
    private MobileRechargePresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_top_up_option);
        presenter = new MobileRechargePresenter();
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
            binding.toolbar.setTitle(getString(R.string.txt_voucher_recharge));
        }

        binding.etMobile.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etTopUpAmount.setFilters(new InputFilter[]{new DisableEmojiInEditText()});

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
        String top_up_amount = binding.etTopUpAmount.getText().toString();

        if (mobile_number.isEmpty()) {
            binding.etMobile.requestFocus();
            binding.etMobile.setError(getText(R.string.alert_enter_mobile_number));
        }  else if (!Utility.isValidPhoneNumber("", mobile_number)) {
            binding.etMobile.requestFocus();
            binding.etMobile.setError(getText(R.string.alert_enter_valid_mobile_number));
        }else if (top_up_amount.isEmpty()) {
            binding.etTopUpAmount.requestFocus();
            binding.etTopUpAmount.setError(getText(R.string.alert_enter_top_up_amount));
        } else if (Integer.parseInt(top_up_amount) < 1) {
            binding.etTopUpAmount.requestFocus();
            binding.etTopUpAmount.setError(getText(R.string.alert_enter_valid_top_up_amount));
        } else if (CheckInternetConnection.isInternetConnection(this)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(this);
            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
//                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
//                map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_EMAIL)));
//                map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_PASSWORD)));
                map.put("amount", RequestBody.create(MediaType.parse("multipart/form-data"), "" + top_up_amount));
                map.put("mobilenumber", RequestBody.create(MediaType.parse("multipart/form-data"), "" + mobile_number));
                map.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + userInfo.getId()));
                presenter.mobileRecharge(this, map);
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
    public void onSuccess(ResponseDefault body) {
        if (body.isRESPONSE()) {
            MyCustomToast.showToast(this, body.getRESPONSE_MSG());
//            MyCustomToast.showSuccessAlert(this, body.getRESPONSE_MSG());
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
    }

    @Override
    public void onSuccessVoucherPrint(ResponseDefault body) {

    }

    @Override
    public Context getContext() {
        return null;
    }
}

