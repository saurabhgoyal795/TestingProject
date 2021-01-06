package com.atlaaya.evdrecharge.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.apiPresenter.LoginPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;

import com.atlaaya.evdrecharge.databinding.ActivityLoginBinding;
import com.atlaaya.evdrecharge.listener.LoginListener;
import com.atlaaya.evdrecharge.model.ResponseLogin;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.LanguageUtil;
import com.atlaaya.evdrecharge.utils.MyCustomToast;
import com.atlaaya.evdrecharge.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LoginActivity extends BaseActivity implements View.OnClickListener, LoginListener {

    private ActivityLoginBinding binding;

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        presenter = new LoginPresenter();
        presenter.setView(this);

        String email = SessionManager.getString(this, SessionManager.KEY_EMAIL);
        binding.textInputUserId.setText(email);

        binding.btnLogin.setOnClickListener(this);

        Utility.getDeviceDensityString(this);
        LanguageUtil.setTextViewTextByLanguage(getContext(),binding.passwordText, "hint_password");
//        LanguageUtil.setTextViewTextByLanguage(getContext(),binding.usernameText, "title_profile");


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogin) {
            onClickLogin();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void onClickLogin() {
        if (Objects.requireNonNull(binding.textInputUserId).getText().toString().trim().isEmpty()) {
            MyCustomToast.showErrorAlert(this, getString(R.string.alert_enter_user_id));
        } else if (Objects.requireNonNull(binding.textInputPassword).getText().toString().trim().isEmpty()) {
            MyCustomToast.showErrorAlert(this, getString(R.string.alert_enter_password));
        } else {
            callLoginAPI();
        }
    }

    private void callLoginAPI() {
        if (CheckInternetConnection.isInternetConnection(this)) {
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
            map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), binding.textInputUserId.getText().toString().trim()));
            map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), binding.textInputPassword.getText().toString()));

            presenter.login(this, map);
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    @Override
    public void onSuccess(ResponseLogin body) {
        if (body.isRESPONSE()) {
            SessionManager.saveLoginDetail(this,
                    binding.textInputUserId.getText().toString().trim(),
                    binding.textInputPassword.getText().toString());
            SessionManager.saveUserDetail(this, body.getUserInfo());

            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

}
