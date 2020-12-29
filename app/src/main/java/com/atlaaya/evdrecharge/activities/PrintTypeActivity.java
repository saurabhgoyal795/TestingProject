package com.atlaaya.evdrecharge.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import com.atlaaya.evdrecharge.MyApplication;
import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.api.APIInterface;
import com.atlaaya.evdrecharge.apiPresenter.CalculateElectricityPresenter;
import com.atlaaya.evdrecharge.databinding.ActivityPrintTypeBinding;
import com.atlaaya.evdrecharge.listener.CalculateElectricityListner;
import com.atlaaya.evdrecharge.model.ModelOperator;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ResponseDefault;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.MyCustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class PrintTypeActivity extends BaseActivity implements View.OnClickListener, CalculateElectricityListner {

    private ActivityPrintTypeBinding binding;
    private CalculateElectricityPresenter presenter;
    private ModelService selectedService;
    private ModelOperator selectedOperator;
    private boolean isValidBillNumber = false;
    ProgressDialog pDialog;
    private String printText;
    boolean offline = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_print_type);
        presenter = new CalculateElectricityPresenter();
        pDialog = new ProgressDialog(this);

        pDialog.setCancelable(false);
        presenter.setView(this);
        if (getIntent().hasExtra("service")) {
            selectedService = getIntent().getParcelableExtra("service");
        }
        if (getIntent().hasExtra("operator")) {
            selectedOperator = getIntent().getParcelableExtra("operator");
        }
        if (getIntent().hasExtra("offline")) {
            offline = false;
        }
        setSupportActionBar(binding.toolbar);

        if (selectedService != null) {
            binding.toolbar.setTitle(selectedService.getService_name());
        } else {
            binding.toolbar.setTitle(getString(R.string.txt_voucher_recharge));
        }

        binding.cardViewSinglePrint.setOnClickListener(this);
        binding.cardViewBulkPrint.setOnClickListener(this);
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
        if (v.getId() == R.id.cardViewSinglePrint) {
            Intent intent = new Intent(this, VoucherListSingleActivity.class);
            intent.putExtra("service", selectedService);
            intent.putExtra("operator", selectedOperator);
            if (offline == false) {
                intent.putExtra("offline", "false");
            }
            startActivityForResult(intent, 200);
        } else if (v.getId() == R.id.cardViewBulkPrint) {
            Intent intent = new Intent(this, VoucherListActivity.class);
            intent.putExtra("service", selectedService);
            intent.putExtra("operator", selectedOperator);
            if (offline == false) {
                intent.putExtra("offline", "false");
            }
            startActivityForResult(intent, 200);
        }
        if (v.getId() == R.id.btnSubmit) {
            onClickCalculateBill();
        }
        if (v.getId() == R.id.btnProceed) {
            callSubmitBillAPI();
        }
        if (v.getId() == R.id.btnPrint) {
            printText(printText);
        }
    }
    private void onClickCalculateBill() {
        if (Objects.requireNonNull(binding.textInputBillId.getEditText()).getText().toString().trim().isEmpty()) {
            binding.textInputBillId.requestFocus();
            MyCustomToast.showErrorAlert(this, getString(R.string.alert_enter_bill_id));
        }  else {
            callCalculateBillAPI();
        }
    }

    private void callCalculateBillAPI() {
        if (CheckInternetConnection.isInternetConnection(this)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(this);
            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("bill_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + binding.textInputBillId.getEditText().getText().toString().trim() ));
                map.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + userInfo.getId()));
                presenter.calculateElectricity(this, map);
            }

        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    private void printText(String text) {
        MyCustomToast.showSuccessAlert(this, "Print successfully");
    }

    private void callSubmitBillAPI() {
        if (CheckInternetConnection.isInternetConnection(this)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(this);
            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("bill_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + binding.textInputBillId.getEditText().getText().toString().trim()));
                map.put("check_response", RequestBody.create(MediaType.parse("multipart/form-data"), "" + 1));
                map.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + userInfo.getId()));
                new JSONtask().execute(MyApplication.BASE_URL +"submitbill");
                //presenter.submitElectricity(this, map);
            }

        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }


    @SuppressLint("NewApi")
    class JSONtask extends AsyncTask<String, String, JSONObject> {

        public ArrayList<String> list = new ArrayList<String>();
        String json;
        JSONObject jsonObj, jsonObj1;
        //List list=new ArrayList();

        @Override
        protected void onPreExecute() {
            ModelUserInfo userInfo = SessionManager.getUserDetail(getApplicationContext());
            json = "{\n" +
                    "\"user_id\":\"" + userInfo.getId() + "\"" + ",\n" +
                    "\"check_response\":\"" + 1 + "\"" + ",\n" +
                    "\"bill_id\":\"" + binding.textInputBillId.getEditText().getText().toString().trim() + "\"" + "\n" +
                    "}";
            pDialog.setMessage("Please wait ...");
            showDialog();
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);


                //  String payload = "username=" + emailString + "&password=" + passwordString;
                Log.e("send value", json);
                connection = (HttpsURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-type", "application/json");

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                writer.write(json);
                writer.close();

                connection.connect();

                int code = connection.getResponseCode();
                //list.add(String.valueOf(code));

                if (code == 200) {

                } else {
                    Log.e("response code", String.valueOf(code));


                    //Toast.makeText(getApplicationContext(), "Enter correct credentials", Toast.LENGTH_LONG).show();
                }

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                jsonObj = new JSONObject(buffer.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return jsonObj;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            hideDialog();
            try {
                boolean isStatus = jsonObject.getBoolean("RESPONSE");

                if(isStatus){
                    JSONObject responseMsg = jsonObject.getJSONObject("RESPONSE_MSG");
                    String responseString = responseMsg.getString("message");
                    printText = responseString;
                    binding.text1.setText(Html.fromHtml(responseString, Html.FROM_HTML_MODE_COMPACT));
                    binding.btnProceed.setVisibility(View.INVISIBLE);
                    binding.btnPrint.setVisibility(View.VISIBLE);

                }else{
                    JSONObject responseMsg = jsonObject.getJSONObject("RESPONSE_MSG");
                    String responseString = responseMsg.getString("message");
                    MyCustomToast.showErrorAlert(PrintTypeActivity.this, responseString);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSuccess(ResponseDefault body) {
        if (body.isRESPONSE()) {
            if(isValidBillNumber){
                MyCustomToast.showToast(this, body.getRESPONSE_MSG());
                MyCustomToast.showSuccessAlert(this, body.getRESPONSE_MSG());
                binding.text1.setVisibility(View.VISIBLE);
                binding.text1.setText(Html.fromHtml(body.getRESPONSE_MSG(), Html.FROM_HTML_MODE_COMPACT));
            }else{
                isValidBillNumber = true;
                binding.btnProceed.setVisibility(View.VISIBLE);
                binding.btnSubmit.setVisibility(View.INVISIBLE);
                binding.textInputBillId.setVisibility(View.INVISIBLE);
                binding.text1.setText(Html.fromHtml(body.getRESPONSE_MSG(), Html.FROM_HTML_MODE_COMPACT));
            }

        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
    }

    @Override
    public Context getContext() {
        return null;
    }
}

