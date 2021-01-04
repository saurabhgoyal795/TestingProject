package com.atlaaya.evdrecharge.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.atlaaya.evdrecharge.MyApplication;
import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.api.APIInterface;
import com.atlaaya.evdrecharge.api.RestService;
import com.atlaaya.evdrecharge.apiPresenter.LanguagePresenter;
import com.atlaaya.evdrecharge.apiPresenter.ServicesPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.listener.LanguageListener;
import com.atlaaya.evdrecharge.model.LanguageServices;
import com.atlaaya.evdrecharge.model.ResponseServices;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SplashActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, LanguageListener {
    String[] vpntype = { "M2M sim", "Internet"};
    String[] printerType = { "Pos Machine","Mobile" };
    private LanguagePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        presenter = new LanguagePresenter();
        presenter.setView(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SessionManager.getUserDetail(SplashActivity.this) == null) {
                    showDialog();
                } else {
                    Intent intent;
                    intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 2000);
    }


    private void callServiceAPI() {
        if (CheckInternetConnection.isInternetConnection(this)) {
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
            presenter.servicesList(this, map);
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    public void showDialog() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);
        LinearLayout layout = new LinearLayout(this);
        Spinner spin = popupView.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        Spinner spin1 = popupView.findViewById(R.id.spinnerprinter);
        spin1.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,vpntype);
        ArrayAdapter aa1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,printerType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        spin1.setAdapter(aa1);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent;
                popupWindow.dismiss();
                if (SessionManager.getUserDetail(SplashActivity.this) == null) {
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                } else {
                    intent = new Intent(getApplicationContext(), HomeActivity.class);
                }
                Log.d("SplashTag", MyApplication.BASE_URL+"SplashTag2");
                startActivity(intent);
                finish();
                return true;
            }
        });
        popupView.findViewById(R.id.button_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                popupWindow.dismiss();
                if (SessionManager.getUserDetail(SplashActivity.this) == null) {
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                } else {
                    intent = new Intent(getApplicationContext(), HomeActivity.class);
                }
                Log.d("SplashTag", MyApplication.BASE_URL+"SplashTag");
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()== R.id.spinner) {
            if (position == 0) {
                MyApplication.BASE_URL = "https://196.189.116.116/demo/Webservices/";
                APIInterface service = RestService.createRetrofitService(APIInterface.class, MyApplication.BASE_URL);
                MyApplication.getInstance().setAPIInterface(service);
            } else {
                MyApplication.BASE_URL = "https://196.189.116.116/demo/Webservices/";
                APIInterface service = RestService.createRetrofitService(APIInterface.class, MyApplication.BASE_URL);
                MyApplication.getInstance().setAPIInterface(service);
            }
            SessionManager.saveUrl(getApplicationContext(), MyApplication.BASE_URL);
        }else{
            if(position==0){
                SessionManager.savePrinter(getApplicationContext(), "pos");
            }else{
                SessionManager.savePrinter(getApplicationContext(), "mobile");
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        callServiceAPI();
    }

    @Override
    public void onSuccess(LanguageServices body) {
        SessionManager.saveLanguageData(getApplicationContext(), body.getRESPONSE());
        Log.d("lannguagesuccess", ""+body.getRESPONSE());
    }

    @Override
    public Context getContext() {
        return null;
    }
}
