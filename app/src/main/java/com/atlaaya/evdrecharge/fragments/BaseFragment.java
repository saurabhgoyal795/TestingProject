package com.atlaaya.evdrecharge.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.activities.LoginActivity;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.MyCustomToast;

import icepick.Icepick;

public abstract class BaseFragment extends Fragment {

    private Dialog avlProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        avlProgressDialog = avlProgressDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private Dialog avlProgressDialog() {

//        AVLoadingIndicatorView avLoadingIndicatorView = new AVLoadingIndicatorView(this);
//        avLoadingIndicatorView.setIndicator(new BallPulseIndicator());
//        avLoadingIndicatorView.setIndicatorColor(ContextCompat.getColor(this, R.color.colorAccent));

        Dialog dialog = new Dialog(getActivity());
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
            getActivity().runOnUiThread(() -> {
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
            MyCustomToast.showToast(getActivity(), reason);
        } else {
            MyCustomToast.showToast(getActivity(), getString(R.string.msg_default_error));
        }
    }


    public AlertDialog.Builder getAlertDialogBuilder(String title, String message, boolean cancellable) {
        return new AlertDialog.Builder(getActivity(), R.style.AppTheme_AlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancellable);
    }

    public void dialogAccountDeactivate(String reason) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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



    private  void UserLogoutDeleteAccount() {
        SessionManager.clearAll(getActivity());

        redirectLogin();
    }

   private void redirectLogin() {
        final Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finishAffinity();
    }
}
