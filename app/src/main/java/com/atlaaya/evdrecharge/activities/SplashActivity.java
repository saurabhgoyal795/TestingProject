package com.atlaaya.evdrecharge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.atlaaya.evdrecharge.MyApplication;
import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.api.APIInterface;
import com.atlaaya.evdrecharge.api.RestService;
import com.atlaaya.evdrecharge.storage.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (SessionManager.getUserDetail(SplashActivity.this) == null) {
                    MyApplication.BASE_URL = "https://highlightevd.com/evdlive/webservices/";
                    APIInterface service = RestService.createRetrofitService(APIInterface.class, MyApplication.BASE_URL);
                    MyApplication.getInstance().setAPIInterface(service);
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                } else {
                    intent = new Intent(getApplicationContext(), HomeActivity.class);
                }
                Log.d("SplashTag", MyApplication.BASE_URL+"SplashTag");
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
