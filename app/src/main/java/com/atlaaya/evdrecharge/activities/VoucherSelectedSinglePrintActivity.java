package com.atlaaya.evdrecharge.activities;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.Constraints;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.atlaaya.evdrecharge.MyApplication;
import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityVoucherSelectedSinglePrintBinding;
import com.atlaaya.evdrecharge.model.ModelOperator;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ModelVoucherPlan;
import com.atlaaya.evdrecharge.model.ModelVoucherPurchased;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchaseBulk;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchaseBulkOrder;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.MyCustomToast;
import com.atlaaya.evdrecharge.utils.Utility;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.printer.UsbThermalPrinter;

import java.io.File;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherSelectedSinglePrintActivity extends BaseActivity implements View.OnClickListener {

    private final int NOPAPER = 3;
    private final int LOWBATTERY = 4;
    private final int PRINTVERSION = 5;
    private final int PRINTCONTENT = 9;
    private final int CANCELPROMPT = 10;
    private final int PRINTERR = 11;
    private final int OVERHEAT = 12;
    private final int PRINTPICTURE = 14;
    private ArrayList<ModelVoucherPlan> rechargePlanList;
    private String printBy = "";
    private ActivityVoucherSelectedSinglePrintBinding binding;
    private ModelService selectedService;
    private ModelOperator selectedOperator;
    private ModelVoucherPlan selectedPlan;
    private String printVersion;
    private int leftDistance = 0; // default
    private int lineDistance = 0; // default
    private int wordFont = 1; // default
    private int printGray = 1; // default
//    private File fileReceipt;
//    private String picturePath;
//    private String shareReceiptAction = "";
    private MyHandler handler;
    private ProgressDialog progressDialog, dialog;
    private UsbThermalPrinter mUsbThermalPrinter = new UsbThermalPrinter(this);
    private String Result;
    private Boolean nopaper = false;
    private boolean LowBattery = false;
    private final BroadcastReceiver printReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_NOT_CHARGING);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                //TPS390 can not print,while in low battery,whether is charging or not charging
			/*	if(SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS390.ordinal()){
					if (level * 5 <= scale) {
						LowBattery = true;
					} else {
						LowBattery = false;
					}
				}else {
					if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
						if (level * 5 <= scale) {
							LowBattery = true;
						} else {
							LowBattery = false;
						}
				  } else {
					        LowBattery = false;
				        }
				  }*/
            }
            //Only use for TPS550MTK devices
            else if (action != null && action.equals("android.intent.action.BATTERY_CAPACITY_EVENT")) {
                int status = intent.getIntExtra("action", 0);
                int level = intent.getIntExtra("level", 0);
                if (status == 0) {
                    LowBattery = level < 1;
                } else {
                    LowBattery = false;
                }
            }
        }
    };
    private boolean isPrintClicked = false;
    private ArrayList<ModelVoucherPurchased> voucherPurchasedArrayList =  new ArrayList<>();
    private ModelUserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voucher_selected_single_print);
        userInfo = SessionManager.getUserDetail(this);

        if (getIntent().hasExtra("plan")) {
            selectedPlan = getIntent().getParcelableExtra("plan");
        }
        if (getIntent().hasExtra("planList")) {
            rechargePlanList = getIntent().getParcelableArrayListExtra("planList");
        }
        if (getIntent().hasExtra("service")) {
            selectedService = getIntent().getParcelableExtra("service");
        }
        if (getIntent().hasExtra("operator")) {
            selectedOperator = getIntent().getParcelableExtra("operator");
        }
        setSupportActionBar(binding.toolbar);

//        binding.layoutReceiptShare.post(() -> binding.layoutReceiptShare.setLayoutParams(new Constraints.LayoutParams(405, Constraints.LayoutParams.WRAP_CONTENT)));

        binding.layoutPleaseWait.setVisibility(View.GONE);

        if (selectedService != null) {
            binding.toolbar.setTitle(selectedService.getService_name());
        } else {
            binding.toolbar.setTitle(getString(R.string.txt_voucher_recharge));
        }

        if (rechargePlanList == null) {
            rechargePlanList = new ArrayList<>();
        }

        if (selectedPlan != null) {
            binding.txtAmount.setVisibility(View.VISIBLE);
            binding.txtAmount.setText(String.format("%s %s", selectedPlan.getAmount(), getString(R.string.currency_ethiopia_unit)));
            selectedPlan.setSelectedQty(1);
            rechargePlanList.add(selectedPlan);
        }

        if (AppConstants.IS_POS_APK) {
            binding.btnPrint.setVisibility(View.VISIBLE);
            binding.btnPrintBluetooth.setVisibility(View.GONE);

            handler = new MyHandler();

            dialog = new ProgressDialog(this);
            dialog.setTitle(R.string.idcard_czz);
            dialog.setMessage(getText(R.string.watting));
            dialog.setCancelable(false);
            dialog.show();

            IntentFilter pIntentFilter = new IntentFilter();
            pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            pIntentFilter.addAction("android.intent.action.BATTERY_CAPACITY_EVENT");
            registerReceiver(printReceive, pIntentFilter);

            runOnUiThread(() -> new Thread(() -> {
                Looper.prepare();
                try {
                    mUsbThermalPrinter.start(0);
                    mUsbThermalPrinter.reset();
                    printVersion = mUsbThermalPrinter.getVersion();
                    int st = mUsbThermalPrinter.checkStatus();
                    Log.e("yw", "status" + " " + st);
                } catch (TelpoException e) {
                    Log.e("yw", "status  111" + " " + e.toString());
                    e.printStackTrace();
                } finally {
                    Message message = new Message();
                    message.what = PRINTVERSION;
                    if (printVersion != null) {
                        message.obj = "1";
                    } else {
                        message.obj = "0";
                    }
                    handler.sendMessage(message);
                }
            }).start());
        } else {
            binding.btnPrint.setVisibility(View.GONE);
            binding.btnPrintBluetooth.setVisibility(View.VISIBLE);
        }

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
        switch (v.getId()) {
            case R.id.btnPrint:
                printBy = "usb";
                if (rechargePlanList == null || rechargePlanList.isEmpty()) {
                    MyCustomToast.showErrorAlert(this, "Nothing to print. Please select at least 1 voucher.");
                } else {
                    if (Build.VERSION.SDK_INT >= 23 && !checkReadWritePermission()) {
                        requestReadWritePermission();
                        return;
                    }
                    callVoucherPurchaseBulk();
                }
                break;
            case R.id.btnPrintBluetooth:
                printBy = "bluetooth";
                if (rechargePlanList == null || rechargePlanList.isEmpty()) {
                    MyCustomToast.showErrorAlert(VoucherSelectedSinglePrintActivity.this, "Nothing to print. Please select at least 1 voucher.");
                } else {
                    if (Build.VERSION.SDK_INT >= 23 && !checkReadWritePermission()) {
                        requestReadWritePermission();
                        return;
                    }
                    callVoucherPurchaseBulk();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && !this.isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        try {
            unregisterReceiver(printReceive);
            if (mUsbThermalPrinter != null)
                mUsbThermalPrinter.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void callVoucherPurchaseBulk() {

        if (Build.VERSION.SDK_INT >= 23 && !checkReadWritePermission()) {
            requestReadWritePermission();
            return;
        }

        if (CheckInternetConnection.isInternetConnection(this)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(this);
            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                map.put("username", RequestBody.create(MediaType.parse("multipart/form-data"), userInfo.getUsername()));
                map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_PASSWORD)));
                map.put("service_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + selectedService.getId()));
                map.put("operator_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + selectedOperator.getId()));

                List<Integer> voucher_amount_ids = new ArrayList<>();
                List<Integer> quantitys = new ArrayList<>();
                for (ModelVoucherPlan voucherPlan : rechargePlanList) {
                    if (voucherPlan.getSelectedQty() > 0) {
                        voucher_amount_ids.add(voucherPlan.getId());
                        quantitys.add(voucherPlan.getSelectedQty());
                    }
                }
                for (int i = 0; i < voucher_amount_ids.size(); i++) {
                    map.put("voucher_amount_id[" + i + "]", RequestBody.create(MediaType.parse("multipart/form-data"), "" + voucher_amount_ids.get(i)));
                    map.put("quantity[" + i + "]", RequestBody.create(MediaType.parse("multipart/form-data"), "" + quantitys.get(i)));
                }

                isPrintClicked = true;

                binding.btnPrint.setEnabled(false);
                binding.btnPrintBluetooth.setEnabled(false);
                binding.layoutPleaseWait.setVisibility(View.VISIBLE);

                MyApplication.getInstance().getAPIInterface().android_voucher_order(map)
                        .enqueue(new Callback<ResponseVoucherPurchaseBulk>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseVoucherPurchaseBulk> call, @NonNull Response<ResponseVoucherPurchaseBulk> response) {

                                ResponseVoucherPurchaseBulk body = response.body();
                                if (response.isSuccessful() && response.code() == 200 && body != null) {
                                    if (body.isRESPONSE()) {
                                        callVoucherPurchaseBulkOrderDetail(body.getVoucherPurchased().getVchr_odr_id());
                                    } else {
                                        binding.btnPrint.setEnabled(true);
                                        binding.btnPrintBluetooth.setEnabled(true);
                                        binding.layoutPleaseWait.setVisibility(View.GONE);
                                        onErrorToast(body.getRESPONSE_MSG());
                                    }
                                } else {
                                    binding.btnPrint.setEnabled(true);
                                    binding.btnPrintBluetooth.setEnabled(true);
                                    binding.layoutPleaseWait.setVisibility(View.GONE);
                                    onErrorToast(getString(R.string.msg_something_went_wrong));
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ResponseVoucherPurchaseBulk> call, @NonNull Throwable t) {
                                try {
                                    binding.btnPrint.setEnabled(true);
                                    binding.btnPrintBluetooth.setEnabled(true);
                                    binding.layoutPleaseWait.setVisibility(View.GONE);

                                    if (t instanceof TimeoutException || t instanceof SocketTimeoutException) {
                                        onErrorToast(getString(R.string.msg_unable_connect_server));
                                    } else {
                                        if (t instanceof NetworkErrorException || t instanceof SocketException) {
                                            onErrorToast(getString(R.string.msg_check_internet_connection));
                                        } else {
                                            onErrorToast(getString(R.string.msg_something_went_wrong));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    private void callVoucherPurchaseBulkOrderDetail(String orderId) {
        if (CheckInternetConnection.isInternetConnection(this)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(this);
            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                map.put("username", RequestBody.create(MediaType.parse("multipart/form-data"), userInfo.getUsername()));
                map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_PASSWORD)));
                map.put("order_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + orderId));

                MyApplication.getInstance().getAPIInterface().android_print_order(map)
                        .enqueue(new Callback<ResponseVoucherPurchaseBulkOrder>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseVoucherPurchaseBulkOrder> call, @NonNull Response<ResponseVoucherPurchaseBulkOrder> response) {

                                ResponseVoucherPurchaseBulkOrder body = response.body();
                                if (response.isSuccessful() && response.code() == 200 && body != null) {
                                    if (body.isRESPONSE()) {
                                        setReceiptInfo(body.getVoucherPurchasedList());
                                    } else {
                                        binding.btnPrint.setEnabled(true);
                                        binding.btnPrintBluetooth.setEnabled(true);
                                        binding.layoutPleaseWait.setVisibility(View.GONE);
                                        onErrorToast(body.getRESPONSE_MSG());
                                    }
                                } else {
                                    binding.btnPrint.setEnabled(true);
                                    binding.btnPrintBluetooth.setEnabled(true);
                                    binding.layoutPleaseWait.setVisibility(View.GONE);
                                    onErrorToast(getString(R.string.msg_something_went_wrong));
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ResponseVoucherPurchaseBulkOrder> call, @NonNull Throwable t) {
                                try {
                                    binding.btnPrint.setEnabled(true);
                                    binding.btnPrintBluetooth.setEnabled(true);
                                    binding.layoutPleaseWait.setVisibility(View.GONE);
                                    if (t instanceof TimeoutException || t instanceof SocketTimeoutException) {
                                        onErrorToast(getString(R.string.msg_unable_connect_server));
                                    } else {
                                        if (t instanceof NetworkErrorException || t instanceof SocketException) {
                                            onErrorToast(getString(R.string.msg_check_internet_connection));
                                        } else {
                                            onErrorToast(getString(R.string.msg_something_went_wrong));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    /**
     * need for Android 6.0 and above runtime permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_WRITE:
                if (grantResults.length > 0) {
                    boolean READ_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (READ_STORAGE && WRITE_STORAGE) {
//                        binding.btnPrint.performClick();
                        MyCustomToast.showToast(this, getString(R.string.msg_permission_allowed));
                    } else {
                        new AlertDialog.Builder(this)
                                .setMessage(getString(R.string.msg_access_storage_permission_print))
                                .setPositiveButton(getString(R.string.btn_allow), (dialog, which) -> {

                                    final Intent i = new Intent();
                                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    i.addCategory(Intent.CATEGORY_DEFAULT);
                                    i.setData(Uri.parse("package:" + getPackageName()));
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(i);
                                }).setNegativeButton(getString(R.string.btn_cancel), null).show();
                    }
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setReceiptInfo(ArrayList<ModelVoucherPurchased> voucherPurchasedArrayList) {
        if (voucherPurchasedArrayList != null) {

            this.voucherPurchasedArrayList.clear();
            this.voucherPurchasedArrayList.addAll(voucherPurchasedArrayList);
            printReceiptPicture();

/*            runOnUiThread(() -> {
                System.gc();
                PrintableAdapter printableAdapter = new PrintableAdapter(getApplicationContext());
                printableAdapter.setPrintable(true);
                printableAdapter.setDuplicate(false);
                printableAdapter.setData(voucherPurchasedArrayList);
                binding.recyclerViewShare.setAdapter(null);
                binding.recyclerViewShare.setAdapter(printableAdapter);

                if (printBy.equals("bluetooth")) {
                    VoucherSuccessPrintActivity.setPrintVoucherContentArrayList(voucherPurchasedArrayList);
                }

                new Handler().postDelayed(this::getReceipt, 600);
            });*/

        }
    }

/*    private void getReceipt() {
        binding.btnPrint.setEnabled(true);
        binding.btnPrintBluetooth.setEnabled(true);
        binding.layoutPleaseWait.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= 23 && !checkReadWritePermission()) {
            requestReadWritePermission();
            return;
        }
        runOnUiThread(() -> {
            showProgressDialog();
            new Handler().postDelayed(() -> {
                try {
                    fileReceipt = new ReceiptImageGenerate(binding.recyclerViewShare).execute("1").get();
                    shareReceiptAction = "printPicture";
                    dismissProgressDialog();

                    printReceiptAction();
                } catch (Exception e) {
                    dismissProgressDialog();
                    e.printStackTrace();
                }
            }, 600);
        });
    }*/

/*    private void printReceiptAction() {
        if (fileReceipt != null) {
            switch (shareReceiptAction) {
                case "printPicture":
                    printReceiptPicture();
                    break;
            }
        } else {
            Log.i("TAG", "Oops! Image could not be saved.");
        }
    }*/

    private void printReceiptPicture() {
//        picturePath = fileReceipt.getAbsolutePath();
        binding.btnPrint.setEnabled(true);
        binding.btnPrintBluetooth.setEnabled(true);
        binding.layoutPleaseWait.setVisibility(View.GONE);

        if (printBy.equals("usb")) {
            if (LowBattery) {
                handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
            } else {
                if (!nopaper) {
                    progressDialog = ProgressDialog.show(this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                    handler.sendMessage(handler.obtainMessage(PRINTCONTENT, 1, 0, null));
//                    handler.sendMessage(handler.obtainMessage(PRINTPICTURE, 1, 0, null));
                } else {
                    Toast.makeText(this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                }
            }
        } else {

            VoucherSuccessPrintActivity.setPrintVoucherContentArrayList(voucherPurchasedArrayList);
            Intent i = new Intent(getApplicationContext(), BluetoothMainActivity.class);
//            i.putExtra("path", picturePath);
            i.putExtra("path", "");
            startActivity(i);
        }
    }

    private void noPaperDlg() {
        android.app.AlertDialog.Builder dlg = new android.app.AlertDialog.Builder(this);
        dlg.setTitle(getString(R.string.noPaper));
        dlg.setMessage(getString(R.string.noPaperNotice));
        dlg.setCancelable(false);
        dlg.setPositiveButton(R.string.sure, (dialogInterface, i) -> {
        });
        dlg.show();
    }

    private class PrintableAdapter extends RecyclerView.Adapter<PrintableAdapter.ViewHolder> {

        private Context mContext;
        private boolean isPrintable = false;
        private boolean isDuplicate = false;
        private ArrayList<ModelVoucherPurchased> voucherPurchasedArrayList;

        private PrintableAdapter(Context mContext) {
            this.mContext = mContext;
            voucherPurchasedArrayList = new ArrayList<>();
        }

        public void setData(ArrayList<ModelVoucherPurchased> voucherPurchasedArrayList) {
            if (this.voucherPurchasedArrayList != null) {
                this.voucherPurchasedArrayList.clear();
            } else {
                this.voucherPurchasedArrayList = new ArrayList<>();
            }
            if (voucherPurchasedArrayList != null) {
                this.voucherPurchasedArrayList.addAll(voucherPurchasedArrayList);
            }
        }

        public void setPrintable(boolean printable) {
            isPrintable = printable;
        }

        public void setDuplicate(boolean duplicate) {
            isDuplicate = duplicate;
        }

        @NonNull
        @Override
        public PrintableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            System.gc();
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_print_voucher_content_printer, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final PrintableAdapter.ViewHolder holder, int position) {
            System.gc();

            ModelVoucherPurchased data = voucherPurchasedArrayList.get(position);

            if (isPrintable) {
//                holder.txtPrintData.setText(Html.fromHtml(data.getPrintable_text()));
                holder.txtPrintData.setText(Html.fromHtml(AppConstants.getPinPrintableData(data.getVoucher(), userInfo)));
            } else {
//                holder.txtPrintData.setText(Html.fromHtml(data.getNon_printable_text()));
                holder.txtPrintData.setText(Html.fromHtml(AppConstants.getPinNonPrintableData(data.getVoucher(), userInfo)));
            }
            if (isDuplicate) {
                holder.txtDuplicate.setVisibility(View.VISIBLE);
            } else {
                holder.txtDuplicate.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return voucherPurchasedArrayList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivOperatorLogo;
            ImageView ivCompanyLogo;
            TextView txtDuplicate;
            TextView txtPrintData;

            public ViewHolder(View itemView) {
                super(itemView);
                ivOperatorLogo = itemView.findViewById(R.id.ivOperatorLogo);
                ivCompanyLogo = itemView.findViewById(R.id.ivCompanyLogo);
                txtDuplicate = itemView.findViewById(R.id.txtDuplicate);
                txtPrintData = itemView.findViewById(R.id.txtPrintData);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ReceiptImageGenerate extends AsyncTask<String, Void, File> {

        View view;

        ReceiptImageGenerate(View view) {
            this.view = view;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            showProgressDialog();
        }

        protected File doInBackground(String... params) {
            return Utility.saveBitMap(getApplicationContext(), view, params[0]);
        }

        @Override
        protected void onPostExecute(File result) {
//            dismissProgressDialog();
        }
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (binding.layoutPleaseWait.getVisibility() == View.VISIBLE && isPrintClicked) {
                binding.layoutPleaseWait.setVisibility(View.GONE);
            }
            switch (msg.what) {
                case NOPAPER:
                    noPaperDlg();
                    break;
                case LOWBATTERY:
                    android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(VoucherSelectedSinglePrintActivity.this);
                    alertDialog.setTitle(R.string.operation_result);
                    alertDialog.setMessage(getString(R.string.LowBattery));
                    alertDialog.setPositiveButton(getString(R.string.dialog_comfirm), (dialogInterface, i) -> {
                    });
                    alertDialog.show();
                    break;
                case PRINTVERSION:
                    dialog.dismiss();
                    if (msg.obj.equals("1")) {
                        Log.e("", "printVersion: " + printVersion);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.operation_fail, Toast.LENGTH_LONG).show();
                    }
                    break;
                case PRINTCONTENT:
                    new contentPrintThread().start();
                    break;
                case PRINTPICTURE:
//                    new picturePrintThread().start();
                    break;
                case CANCELPROMPT:
                    if (progressDialog != null && !VoucherSelectedSinglePrintActivity.this.isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case OVERHEAT:
                    android.app.AlertDialog.Builder overHeatDialog = new android.app.AlertDialog.Builder(VoucherSelectedSinglePrintActivity.this);
                    overHeatDialog.setTitle(R.string.operation_result);
                    overHeatDialog.setMessage(getString(R.string.overTemp));
                    overHeatDialog.setPositiveButton(getString(R.string.dialog_comfirm), (dialogInterface, i) -> {
                    });
                    overHeatDialog.show();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Print Error!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

/*    private class picturePrintThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.reset();
                File file = new File(picturePath);
                if (file.exists()) {
                    mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
                    mUsbThermalPrinter.setLeftIndent(leftDistance);
                    mUsbThermalPrinter.setLineSpace(lineDistance);
                    mUsbThermalPrinter.setGray(printGray);

                    mUsbThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath), false);

                    mUsbThermalPrinter.walkPaper(8);
                } else {
                    runOnUiThread(() -> Toast.makeText(VoucherSelectedSinglePrintActivity.this, getString(R.string.not_find_picture), Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper) {
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
            }
        }
    }*/

    private class contentPrintThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
                mUsbThermalPrinter.setLeftIndent(leftDistance);
                mUsbThermalPrinter.setLineSpace(lineDistance);
             /*   if (wordFont == 4) {
                    mUsbThermalPrinter.setFontSize(55);
                } else if (wordFont == 3) {
                    mUsbThermalPrinter.setTextSize(45);
                } else if (wordFont == 2) {
                    mUsbThermalPrinter.setTextSize(35);
                } else if (wordFont == 1) {
                    mUsbThermalPrinter.setTextSize(25);
                }else {
                    mUsbThermalPrinter.setTextSize(30);
                }*/

                mUsbThermalPrinter.setGray(printGray);

                Bitmap mBitmapLogo = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.merge1),
                        386, 80, true);

                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
                for (ModelVoucherPurchased modelVoucherPurchased : voucherPurchasedArrayList) {
                    mUsbThermalPrinter.printLogo(mBitmapLogo, true);

                    mUsbThermalPrinter.setTextSize(34);
                    mUsbThermalPrinter.setBold(true);
                    mUsbThermalPrinter.addString((int)modelVoucherPurchased.getVoucher().getVoucher_amount()+" Birr Airtime");

                    mUsbThermalPrinter.setTextSize(24);
                    mUsbThermalPrinter.setBold(false);
                    mUsbThermalPrinter.addString("E-Voucher Pin :");

                    mUsbThermalPrinter.setTextSize(42);
                    mUsbThermalPrinter.setBold(true);
                    mUsbThermalPrinter.addString("--------------\n" +
                            modelVoucherPurchased.getVoucher().getPin_no()+"\n" +
                            "--------------");

                    mUsbThermalPrinter.setTextSize(24);
                    mUsbThermalPrinter.setBold(false);
                    mUsbThermalPrinter.addString("E-voucher serial number :");

                    mUsbThermalPrinter.setTextSize(24);
                    mUsbThermalPrinter.setBold(true);
                    mUsbThermalPrinter.addString(modelVoucherPurchased.getVoucher().getPin_serial_no());

                    mUsbThermalPrinter.setTextSize(24);
                    mUsbThermalPrinter.setBold(false);
                    mUsbThermalPrinter.addString("Txn Ref No: \n" +
                            modelVoucherPurchased.getVoucher().getTxn_ref_num()+"\n" +
                            "Date:"+modelVoucherPurchased.getVoucher().getDate()+"\n" +
                            "To Recharge your mobile :\n" +
                            "Dial *805*e-voucher pin#\n" +
                            "Agent : "+userInfo.getFullName()+"\n" +
                            "Powered By Highlight Trading");

                    mUsbThermalPrinter.setTextSize(20);
                    mUsbThermalPrinter.setBold(true);
                    mUsbThermalPrinter.addString("--------------------------------\n");

                    mUsbThermalPrinter.printString();
                }

                mUsbThermalPrinter.walkPaper(6);
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper) {
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
            }
        }
    }

}

