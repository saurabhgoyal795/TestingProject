package com.atlaaya.evdrecharge.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.apiPresenter.ChangePasswordPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityChangePasswordBinding;
import com.atlaaya.evdrecharge.listener.ChangePasswordListener;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ResponseDefault;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.DisableEmojiInEditText;
import com.atlaaya.evdrecharge.utils.MyCustomToast;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener, ChangePasswordListener {

    private ActivityChangePasswordBinding binding;

    private ChangePasswordPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        setSupportActionBar(binding.toolbar);

        presenter = new ChangePasswordPresenter();
        presenter.setView(this);

        binding.etOldPass.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etNewPass.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etConfPass.setFilters(new InputFilter[]{new DisableEmojiInEditText()});

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdate) {
            changePassword();
        }
    }

    private void changePassword() {
        String oldPass = binding.etOldPass.getText().toString();
        String newPass = binding.etNewPass.getText().toString();
        String confPass = binding.etConfPass.getText().toString();

        if (oldPass.isEmpty()) {
            binding.etOldPass.requestFocus();
            binding.etOldPass.setError(getText(R.string.alert_enter_current_password));
        } else if (newPass.isEmpty()) {
            binding.etNewPass.requestFocus();
            binding.etNewPass.setError(getText(R.string.alert_enter_new_password));
        } else if (binding.etNewPass.getText().toString().length() < 6) {
            binding.etNewPass.requestFocus();
            binding.etNewPass.setError(getText(R.string.alert_password_min_length));
        } else if (!newPass.equals(confPass)) {
            binding.etConfPass.requestFocus();
            binding.etConfPass.setError(getText(R.string.alert_new_confirm_same_password));
        } else if (CheckInternetConnection.isInternetConnection(this)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(this);
            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_EMAIL)));
                map.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + userInfo.getId()));
                map.put("old_password", RequestBody.create(MediaType.parse("multipart/form-data"), oldPass));
                map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), newPass));
                map.put("confirm_password", RequestBody.create(MediaType.parse("multipart/form-data"), confPass));

                presenter.changePassword(this, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    @Override
    public void onSuccess(ResponseDefault body) {
        if (body.isRESPONSE()) {
            MyCustomToast.showToast(this, body.getRESPONSE_MSG());
//            MyCustomToast.showSuccessAlert(this, body.getRESPONSE_MSG());
           UserLogoutDeleteAccount();
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
    }

    @Override
    public Context getContext() {
        return this;
    }
}

