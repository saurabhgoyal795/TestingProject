package com.atlaaya.evdrecharge.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.MyCustomToast;

/**
 * Base Activity for all app Activities
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_READ_WRITE = 600;

    //    public ProgressDialog progressDialog;
    private Dialog avlProgressDialog;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        progressDialog = new ProgressDialog(this);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage(getString(R.string.msg_please_wait));
//        progressDialog.setContentView(R.layout.avl_loadingbar);

        avlProgressDialog = avlProgressDialog();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private Dialog avlProgressDialog() {

//        AVLoadingIndicatorView avLoadingIndicatorView = new AVLoadingIndicatorView(this);
//        avLoadingIndicatorView.setIndicator(new BallPulseIndicator());
//        avLoadingIndicatorView.setIndicatorColor(ContextCompat.getColor(this, R.color.colorAccent));

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.avl_loadingbar);
        return dialog;

    /*    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(avLoadingIndicatorView);
        return dialogBuilder.create();*/

    }

    public void showProgressDialog() {
       /* AVLoadingIndicatorView avLoadingIndicatorView = new AVLoadingIndicatorView(this);
        avLoadingIndicatorView.setIndicator("BallPulseIndicator");

        BallPulseIndicator ballPulseIndicator = new BallPulseIndicator();
        ballPulseIndicator.setColor(ContextCompat.getColor(this, R.color.colorAccent));

        // Set custom view(Loading Indicator View) after showing dialog
//        progressDialog.setContentView(avLoadingIndicatorView);
        progressDialog.setIndeterminateDrawable(ballPulseIndicator);

        progressDialog.show();*/

        try {
            runOnUiThread(() -> {
                if (avlProgressDialog != null && !avlProgressDialog.isShowing()) {
                    avlProgressDialog.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

//        progressDialog.show();
    }

    public void dismissProgressDialog() {
        try {
            if (avlProgressDialog != null && avlProgressDialog.isShowing()) {
                avlProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableLoadingBar(Context context, boolean enable, String s) {
        if (enable) {
            showProgressDialog();
        } else {
            dismissProgressDialog();
        }
    }

    public void onError(String reason) {
//        onError(reason, false);
        onErrorToast(reason);
    }

    public void onError(String reason, final boolean finishOnOk) {
        if (reason != null && !reason.isEmpty()) {
            getAlertDialogBuilder(null, reason, false)
                    .setPositiveButton(getString(R.string.btn_ok), finishOnOk ? (DialogInterface.OnClickListener) (dialogInterface, i) -> {/*finish();*/} : null)
                    .show();
        } else {
            getAlertDialogBuilder(null, getString(R.string.msg_default_error), false)
                    .setPositiveButton(getString(R.string.btn_ok), finishOnOk ? (DialogInterface.OnClickListener) (dialogInterface, i) -> {/*finish();*/} : null)
                    .show();
        }
    }

    public void onErrorToast(String reason) {
        if (reason != null && !reason.isEmpty()) {
            MyCustomToast.showErrorAlert(this, reason);
        } else {
            MyCustomToast.showErrorAlert(this, getString(R.string.msg_default_error));
        }
    }

    public AlertDialog.Builder getAlertDialogBuilder(String title, String message, boolean cancellable) {
        return new AlertDialog.Builder(this, R.style.AppTheme_AlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancellable);
    }

    public void dialogAccountDeactivate(String reason) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.text_warning));
        builder.setMessage(reason);
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.btn_ok), (dialog, id) -> {
            dialog.cancel();
            UserLogoutDeleteAccount();
        });

        final AlertDialog alert = builder.create();
        //2. now setup to change color of the button
        alert.setOnShowListener(arg0 ->
                alert.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.colorAccent)));
        alert.show();
    }

    public void dialogLogout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.text_warning));
        builder.setMessage(getString(R.string.msg_want_logout));
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.btn_logout), (dialog, id) -> {
            dialog.cancel();
            UserLogoutDeleteAccount();
        });
        builder.setNegativeButton(getString(R.string.btn_cancel), (dialog, id) -> {
            dialog.cancel();
        });

        final AlertDialog alert = builder.create();
        //2. now setup to change color of the button
        alert.setOnShowListener(arg0 -> {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(getResources().getColor(R.color.colorAccent));
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(getResources().getColor(R.color.colorAccent));
                }
        );
        alert.show();
    }

    public void openExternalWebView(String url) {
        if (url != null) {
            Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent1);
        }
    }

   public void UserLogoutDeleteAccount() {
        clearCache(this);
        redirectLogin();
    }

    public static void clearCache(Context context){
        String email = SessionManager.getString(context, SessionManager.KEY_EMAIL);
        SessionManager.clearAll(context);
        SessionManager.saveLoginDetail(context, email, "");
    }

    void redirectLogin() {
        final Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        BaseActivity.this.finishAffinity();
    }

    public boolean checkReadWritePermission() {
        int result0 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result0 == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void requestReadWritePermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, MY_PERMISSIONS_REQUEST_READ_WRITE);
    }


}
