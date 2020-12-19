package com.atlaaya.evdrecharge.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.apiPresenter.ProfilePresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityProfileBinding;
import com.atlaaya.evdrecharge.listener.UpdateProfileListener;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ResponseLogin;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.MyCustomToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ProfileActivity extends BaseActivity implements View.OnClickListener, UpdateProfileListener {

    private ActivityProfileBinding binding;

    private ProfilePresenter profilePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        setSupportActionBar(binding.toolbar);

        profilePresenter = new ProfilePresenter();
        profilePresenter.setView(this);

        binding.txtName.setText("");
        binding.txtFirmName.setText("");
        binding.txtUsername.setText("");
        binding.txtMobile.setText("");
        binding.txtEmail.setText("");
        binding.txtAddress.setText("");
        binding.txtLockFund.setText("");
        binding.txtTerminalNumber.setText("");

        setProfileData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_edit_profile:
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
                break;
            case R.id.action_change_password:
                startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivPhotoEdit:
                startCropActivity();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        callProfileDetailAPI();
    }

    private void startCropActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setScaleType(CropImageView.ScaleType.CENTER)
                .setAspectRatio(1, 1)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE:
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                final CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = result.getUri();
                    Log.e("resultUri", "resultUri: " + resultUri);

                    loadingPhoto(resultUri);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                }
                break;
        }
    }

    private void loadingPhoto(final Uri resultUri) {
        binding.faceWidget.photo(resultUri);
    }

    private void setProfileData() {

        ModelUserInfo userInfo = SessionManager.getUserDetail(this);
        if (userInfo != null) {

            binding.faceWidget.setInitials(userInfo.getInitials());
            binding.faceWidget.photo(userInfo.getImage());

            binding.txtName.setText(userInfo.getFullName());
            binding.txtFirmName.setText(userInfo.getCompany_name());
            binding.txtUsername.setText(userInfo.getUsername());
            binding.txtMobile.setText(userInfo.getMobile());
            binding.txtEmail.setText(userInfo.getEmail());
            binding.txtAddress.setText(userInfo.getAddress());
            binding.txtLockFund.setText(getString(R.string.txt_lockfund, String.valueOf(userInfo.getLockfund())));
            binding.txtTerminalNumber.setText(getString(R.string.txt_terminal_number, String.valueOf(userInfo.getTerminal_id())));
        }
    }

    private void callProfileDetailAPI() {
        if (CheckInternetConnection.isInternetConnection(this)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(this);

            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                map.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + userInfo.getId()));

                profilePresenter.viewProfile(this, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    @Override
    public void onSuccess(ResponseLogin body) {
        if (body.isRESPONSE()) {
            if (body.getUserInfo() != null){
                SessionManager.saveUserDetail(this, body.getUserInfo());
                setProfileData();
            }
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

}

