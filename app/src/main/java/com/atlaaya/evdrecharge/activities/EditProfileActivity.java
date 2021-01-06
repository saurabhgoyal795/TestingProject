package com.atlaaya.evdrecharge.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.databinding.DataBindingUtil;

import com.atlaaya.evdrecharge.MyApplication;
import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.api.APIInterface;
import com.atlaaya.evdrecharge.api.RestService;
import com.atlaaya.evdrecharge.apiPresenter.ProfilePresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityEditProfileBinding;
import com.atlaaya.evdrecharge.listener.UpdateProfileListener;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ResponseLogin;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.DisableEmojiInEditText;
import com.atlaaya.evdrecharge.utils.LanguageUtil;
import com.atlaaya.evdrecharge.utils.MyCustomToast;
import com.atlaaya.evdrecharge.utils.Utility;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class EditProfileActivity extends BaseActivity implements View.OnClickListener, UpdateProfileListener,AdapterView.OnItemSelectedListener {

    private ActivityEditProfileBinding binding;

    private ModelUserInfo userInfo;
    private ProfilePresenter profilePresenter;
    private String selectedLanguage ;
    String[] languageType = { "en","am" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        setSupportActionBar(binding.toolbar);

        profilePresenter = new ProfilePresenter();
        profilePresenter.setView(this);

        binding.etFirstName.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etLastName.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etMobile.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etEmail.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etFirmName.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etAddress.setFilters(new InputFilter[]{new DisableEmojiInEditText()});

        LanguageUtil.setTextViewTextByLanguage(getContext(),binding.tvFirstName, "txt_first_name");
        LanguageUtil.setEditTextHintTextByLanguage(getContext(),binding.etFirstName, "hint_enter_first_name");
        LanguageUtil.setTextViewTextByLanguage(getContext(),binding.tvLastName, "txt_last_name");
        LanguageUtil.setEditTextHintTextByLanguage(getContext(),binding.etLastName, "hint_enter_last_name");
        LanguageUtil.setTextViewTextByLanguage(getContext(),binding.tvMobile, "txt_mobile");
        LanguageUtil.setEditTextHintTextByLanguage(getContext(),binding.etMobile, "hint_mobile_number_");
        LanguageUtil.setTextViewTextByLanguage(getContext(),binding.tvEmail, "txt_email");
        LanguageUtil.setEditTextHintTextByLanguage(getContext(),binding.etEmail, "etEmail");
        LanguageUtil.setTextViewTextByLanguage(getContext(),binding.tvFirm, "txt_firm_name");
        LanguageUtil.setEditTextHintTextByLanguage(getContext(),binding.etFirmName, "hint_enter_firm_name");
        LanguageUtil.setTextViewTextByLanguage(getContext(),binding.tvAddress, "txt_address");
        LanguageUtil.setEditTextHintTextByLanguage(getContext(),binding.etAddress, "hint_enter_address");
        LanguageUtil.setButtonTextByLanguage(getContext(),binding.btnSave, "btn_save");
        Spinner spin = binding.spinner.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,languageType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        setProfileData();

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()== R.id.spinner) {
            if (position == 0) {
                selectedLanguage = "en";
            } else {
                selectedLanguage = "am";
            }

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
        if (v.getId() == R.id.btnSave) {
            editProfile();
        }
    }

    private void setProfileData() {
        userInfo = SessionManager.getUserDetail(this);
        if (userInfo != null) {
            binding.etFirstName.setText(userInfo.getFirst_name());
            binding.etLastName.setText(userInfo.getLast_name());
            binding.etMobile.setText(userInfo.getMobile());
            binding.etEmail.setText(userInfo.getEmail());
            binding.etFirmName.setText(userInfo.getCompany_name());
            binding.etAddress.setText(userInfo.getAddress());
        }
    }

    private void editProfile() {
        String firstName = binding.etFirstName.getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();
        String mobile = binding.etMobile.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String firmName = binding.etFirmName.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();

        if (firstName.isEmpty()) {
            binding.etFirstName.requestFocus();
            binding.etFirstName.setError(getText(R.string.alert_enter_first_name));
        } else if (lastName.isEmpty()) {
            binding.etLastName.requestFocus();
            binding.etLastName.setError(getText(R.string.alert_enter_last_name));
        } else if (mobile.isEmpty()) {
            binding.etMobile.requestFocus();
            binding.etMobile.setError(getText(R.string.alert_enter_mobile_number));
        } else if (!Utility.isValidPhoneNumber("", mobile)) {
            binding.etMobile.requestFocus();
            binding.etMobile.setError(getText(R.string.alert_enter_valid_mobile_number));
        } else if (email.isEmpty()) {
            binding.etEmail.requestFocus();
            binding.etEmail.setError(getText(R.string.alert_enter_email));
        } else if (!Utility.isValidEmail(email)) {
            binding.etEmail.requestFocus();
            binding.etEmail.setError(getText(R.string.alert_enter_email_valid));
        } else if (firmName.isEmpty()) {
            binding.etFirmName.requestFocus();
            binding.etFirmName.setError(getText(R.string.alert_enter_firm_name));
        } else if (address.isEmpty()) {
            binding.etAddress.requestFocus();
            binding.etAddress.setError(getText(R.string.alert_enter_address));
        } else if (CheckInternetConnection.isInternetConnection(this)) {

            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_PASSWORD)));
                map.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + userInfo.getId()));
                map.put("first_name", RequestBody.create(MediaType.parse("multipart/form-data"), firstName));
                map.put("last_name", RequestBody.create(MediaType.parse("multipart/form-data"), lastName));
                map.put("mobile", RequestBody.create(MediaType.parse("multipart/form-data"), mobile));
                map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), email));
                map.put("company_name", RequestBody.create(MediaType.parse("multipart/form-data"), firmName));
                map.put("address", RequestBody.create(MediaType.parse("multipart/form-data"), address));
                if(selectedLanguage!=""){
                    map.put("language", RequestBody.create(MediaType.parse("multipart/form-data"), selectedLanguage));
                }


                profilePresenter.updateProfile(this, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }


    @Override
    public void onSuccess(ResponseLogin body) {
        if (body.isRESPONSE()) {
            if (body.getUserInfo() != null)
                SessionManager.saveUserDetail(this, body.getUserInfo());
            MyCustomToast.showToast(this, body.getRESPONSE_MSG());
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

