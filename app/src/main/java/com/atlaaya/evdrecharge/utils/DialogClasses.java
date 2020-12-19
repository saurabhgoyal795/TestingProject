package com.atlaaya.evdrecharge.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.activities.LoginActivity;


public class DialogClasses {

    private static AlertDialog alertDialogInternet = null;
    private static AlertDialog alertDialogDeactivate = null;

    public static void showDialogAlert(String msg, String ttl, String btn, final Activity ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(btn, (dialog, id) -> {
                    dialog.cancel();
                    dialog.dismiss();
                });
        if (ttl != null && !ttl.equals(""))
            builder.setTitle(ttl);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showDialog1(String msg, String ttl, String btn, final Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(btn, (dialog, id) -> dialog.cancel());
        if (ttl != null && !ttl.equals(""))
            builder.setTitle(ttl);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showDialogInternetAlert(final Context ctx) {
        String msg = ctx.getString(R.string.alert_internet_not_connect);
        String ttl = ctx.getString(R.string.title_internet_error);
        String pos = ctx.getString(R.string.btn_goto_setting);
        String neg = ctx.getString(R.string.btn_close);

        if (alertDialogInternet != null && alertDialogInternet.isShowing()) {
            alertDialogInternet.dismiss();
            alertDialogInternet = null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(msg).setCancelable(false).setPositiveButton(pos, (dialog, id) -> {
            ctx.startActivity(new Intent(Settings.ACTION_SETTINGS));
            dialog.cancel();
        }).setNegativeButton(neg, (dialog, id) -> {
//                ctx.finish();
            dialog.cancel();
        });
        if (!ttl.isEmpty())
            builder.setTitle(ttl);
        try {
            alertDialogInternet = builder.create();
            //2. now setup to change color of the button
            alertDialogInternet.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {

                    Button btnPos = alertDialogInternet.getButton(AlertDialog.BUTTON_POSITIVE);
                    btnPos.setTextColor(ctx.getResources().getColor(R.color.colorAccent));
                    btnPos.setAllCaps(false);

                    Button btnNeg = alertDialogInternet.getButton(AlertDialog.BUTTON_NEGATIVE);
                    btnNeg.setTextColor(ctx.getResources().getColor(R.color.colorAccent));
                    btnNeg.setAllCaps(false);

                }
            });

            alertDialogInternet.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showNetworkFailureAlert(Context context) {
        if (context != null && !((Activity) context).isFinishing() && (alertDialogInternet == null || !alertDialogInternet.isShowing())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getResources().getString(R.string.msg_no_network)).setTitle(context.getResources().getString(R.string.no_internet))
                    .setCancelable(false)
                    .setNegativeButton(context.getString(R.string.btn_ok), (dialog, id) -> {
                    });
            try {
                alertDialogInternet = builder.create();
                alertDialogInternet.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Button nbutton = alertDialogInternet.getButton(DialogInterface.BUTTON_NEGATIVE);
//            nbutton.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
    }

    public static AlertDialog showDialogDeactivate(final Activity ctx, String msg) {
//        String ttl = "";

        if (alertDialogDeactivate != null && alertDialogDeactivate.isShowing()) {
            alertDialogDeactivate.dismiss();
            alertDialogDeactivate = null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(msg).setCancelable(false).setPositiveButton(ctx.getString(R.string.btn_ok), (dialog, id) -> {

//            new StorageUtil(ctx).clearCache();

            Intent mainIntent = new Intent(ctx, LoginActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ctx.startActivity(mainIntent);
            ctx.finish();

            dialog.cancel();
        });
//        if (!ttl.equals(""))
//            builder.setTitle(ttl);
        alertDialogDeactivate = builder.create();
        alertDialogDeactivate.show();
        return alertDialogDeactivate;
    }


}
