package com.atlaaya.evdrecharge.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.atlaaya.evdrecharge.R;
import com.tapadoo.alerter.Alerter;


public class MyCustomToast {

    private static Toast toast;

    public static void showToast(Context context, String msg) {
        cancelToast(context);
        toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
//		 View view = toast.getView();
        // TextView text = (TextView) view.findViewById(android.R.id.message);
        // /* here you can do anything with text */
        toast.show();
    }

    private static void cancelToast(Context context) {
        if (toast != null) {
            toast.cancel();
        }
    }

    public static void showErrorAlert(AppCompatActivity activity, String text) {
        Alerter.create(activity)
                .setText(text)
                .setBackgroundColorRes(R.color.colorRed)
                .setTextAppearance(R.style.AlertTextAppearance)
                .show();
    }
    public static void showSuccessAlert(AppCompatActivity activity, String text) {
        Alerter.create(activity)
                .setText(text)
                .setBackgroundColorRes(R.color.colorGreen)
                .setTextAppearance(R.style.AlertTextAppearance)
                .show();
    }


}	
