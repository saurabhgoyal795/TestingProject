package com.atlaaya.evdrecharge.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.databinding.ActivityVoucherSuccessBinding;
import com.atlaaya.evdrecharge.databinding.ItemPrintVoucherContentBinding;
import com.atlaaya.evdrecharge.utils.ChangeDateFormat;
import com.atlaaya.evdrecharge.utils.Utility;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.printer.UsbThermalPrinter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class LoanPaymentSuccessPrintActivity extends BaseActivity {

    private final static int MAX_LEFT_DISTANCE = 255;
    public static String barcodeStr;
    public static String qrcodeStr;
    public static int paperWalk;
    public static String printContent;

    private final int NOPAPER = 3;
    private final int LOWBATTERY = 4;
    private final int PRINTVERSION = 5;
    private final int PRINTCONTENT = 9;
    private final int CANCELPROMPT = 10;
    private final int PRINTERR = 11;
    private final int OVERHEAT = 12;
    private final int PRINTPICTURE = 14;
    String accountName = "";
    String accountNumber = "";
    String amountToPay = "";
    String refNum = "";
    String loanPrintData = "";
    private String printVersion;
    private int leftDistance = 0; // default
    private int lineDistance = 0; // default
    private int wordFont = 1; // default
    private int printGray = 1; // default
    private MyHandler handler;
    private ProgressDialog dialog;
    private UsbThermalPrinter mUsbThermalPrinter = new UsbThermalPrinter(LoanPaymentSuccessPrintActivity.this);
    private ProgressDialog progressDialog;
    private String Result;
    private Boolean nopaper = false;
    private boolean LowBattery = false;
    private final BroadcastReceiver printReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
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
            else if (action.equals("android.intent.action.BATTERY_CAPACITY_EVENT")) {
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
    private ActivityVoucherSuccessBinding binding;
    private String picturePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/111.bmp";
    private File fileReceipt, fileReceiptDuplicate;
    private String shareReceiptAction = "";
    private boolean printDuplicate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voucher_success);

        if (getIntent().hasExtra("accountName")) {
            accountName = getIntent().getStringExtra("accountName");
        }
        if (getIntent().hasExtra("accountNumber")) {
            accountNumber = getIntent().getStringExtra("accountNumber");
        }
        if (getIntent().hasExtra("amountToPay")) {
            amountToPay = getIntent().getStringExtra("amountToPay");
        }
        if (getIntent().hasExtra("refNum")) {
            refNum = getIntent().getStringExtra("refNum");
        }

        String dateTime = ChangeDateFormat.getDateTimeFromMillisecond("dd/MM/yyyy HH:mm:ss", System.currentTimeMillis());

        loanPrintData = "<b>Ethiopia Telecom</b><br><br>"
                + "<b>Client Name : </b> " + accountName + "<br>"
                + "<b>Account Number : </b> " + accountNumber + "<br>"
                + "<b>Amount Paid : </b> " + amountToPay + " Birr<br>"
                + "<b>Reference number : </b> " + refNum + "<br><br><br>"
                + "Date Time : "+ dateTime+"<br>"
                + "<b>Powered By : Highlight Trading</b><br>";

        setSupportActionBar(binding.toolbar);

        binding.layoutReceiptShare.post(() -> binding.layoutReceiptShare.setLayoutParams(new Constraints.LayoutParams(406, Constraints.LayoutParams.WRAP_CONTENT)));

        binding.toolbar.setTitle(getString(R.string.txt_loan_payment_information));
        binding.text2.setText(getString(R.string.txt_loan_payment_information));
        binding.text1.setText(getString(R.string.txt_successful)+"\nAvailable Balance : 1500 Birr\n");

        setReceiptInfo();

//        savepic();
        handler = new MyHandler();

        IntentFilter pIntentFilter = new IntentFilter();
        pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        pIntentFilter.addAction("android.intent.action.BATTERY_CAPACITY_EVENT");
        registerReceiver(printReceive, pIntentFilter);

        dialog = new ProgressDialog(this);
        dialog.setTitle(R.string.idcard_czz);
        dialog.setMessage(getText(R.string.watting));
        dialog.setCancelable(false);
        dialog.show();

        runOnUiThread(() -> new Thread(() -> {
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
                if (printVersion != null) {
                    Message message = new Message();
                    message.what = PRINTVERSION;
                    message.obj = "1";
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = PRINTVERSION;
                    message.obj = "0";
                    handler.sendMessage(message);
                }
            }
        }).start());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_print_voucher, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.action_print:
                printDuplicate = true;

//                shareReceiptAction="printText";
                shareReceiptAction = "printPicture";
                getReceipt();
                break;
            case R.id.action_share:
                shareReceiptAction = "share";
                getReceipt();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && !this.isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if (!getIntent().hasExtra("top_up_amount")) {
            try {
                unregisterReceiver(printReceive);
                if (mUsbThermalPrinter != null)
                    mUsbThermalPrinter.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        super.onDestroy();
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
                        getReceipt();
                    } else {
                        if (!READ_STORAGE) {
                            new AlertDialog.Builder(this)
                                    .setMessage(getString(R.string.msg_access_storage_permission))
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
                        if (!WRITE_STORAGE) {
                            new AlertDialog.Builder(this)
                                    .setMessage(getString(R.string.msg_access_storage_permission))
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
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setReceiptInfo() {

        if (loanPrintData != null) {

            runOnUiThread(() -> {

                System.gc();
                binding.recyclerView.setAdapter(new NonPrintableAdapter(getApplicationContext()));

                System.gc();
                NonPrintableAdapter printableAdapter = new NonPrintableAdapter(getApplicationContext());
                printableAdapter.setPrintable(true);
                printableAdapter.setDuplicate(false);
                binding.recyclerViewShare.setAdapter(printableAdapter);

                System.gc();
                NonPrintableAdapter printableAdapter1 = new NonPrintableAdapter(getApplicationContext());
                printableAdapter1.setPrintable(true);
                printableAdapter1.setDuplicate(true);
                binding.recyclerViewShareDuplicate.setAdapter(printableAdapter1);
            });
        }
    }

    private void getReceipt() {
        if (Build.VERSION.SDK_INT >= 23 && !checkReadWritePermission()) {
            requestReadWritePermission();
            return;
        }

        runOnUiThread(() -> {
            showProgressDialog();
            if (fileReceipt == null || fileReceiptDuplicate == null) {
                try {

                    binding.recyclerViewShare.setVisibility(View.VISIBLE);
                    binding.recyclerViewShareDuplicate.setVisibility(View.GONE);

                    fileReceipt = new ReceiptImageGenerate().execute("1").get();

                    binding.recyclerViewShare.setVisibility(View.GONE);
                    binding.recyclerViewShareDuplicate.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(() -> {
                        try {
                            fileReceiptDuplicate = new ReceiptImageGenerate().execute("2").get();
                            dismissProgressDialog();
                            printReceiptAction();
                        } catch (ExecutionException | InterruptedException e) {
                            dismissProgressDialog();
                            e.printStackTrace();
                        }
                    }, 600);
                } catch (ExecutionException | InterruptedException e) {
                    dismissProgressDialog();
                    e.printStackTrace();
                }
            } else {
                dismissProgressDialog();
                printReceiptAction();
            }
//        dismissProgressDialog();
        });
    }

    private void printReceiptAction() {
        if (fileReceipt != null && fileReceiptDuplicate != null) {
            Log.i("TAG", "Drawing saved to the gallery!");
            switch (shareReceiptAction) {
                case "share":
                    shareReceipt();
                    break;
                case "printText":
                    printReceiptText();
                    break;
                case "printPicture":
                    printReceiptPicture();
                    break;
            }
        } else {
            Log.i("TAG", "Oops! Image could not be saved.");
        }
    }

    private void shareReceipt() {

        if (fileReceipt != null) {
            // create new Intent
            Intent intent = new Intent(Intent.ACTION_SEND);
            // set flag to give temporary permission to external app to use your FileProvider
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // generate URI, I defined authority as the application ID in the Manifest, the last param is file I want to open
//                    Uri fileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file);
            try {
                Uri fileUri = FileProvider.getUriForFile(this,
                        this.getApplicationContext().getPackageName() + ".provider", fileReceipt);
//                         I am opening a PDF file so I give it a valid MIME type
//                        intent.setDataAndType(fileUri, "image/*");
                // Put the Uri and MIME type in the result Intent
//                        intent.setDataAndType(fileUri, getContentResolver().getType(fileUri));

                intent.setType(getContentResolver().getType(fileUri));
                intent.putExtra(Intent.EXTRA_STREAM, fileUri);

                // validate that the device can open your File!
                PackageManager pm = getPackageManager();
                if (intent.resolveActivity(pm) != null) {
                    startActivity(intent);
                }
            } catch (IllegalArgumentException e) {
                Log.e("File Selector", "The selected file can't be shared: " + fileReceipt.toString());
            }
        } else {
            Log.i("TAG", "Oops! Image could not be saved.");
        }
    }

    private void printReceiptText() {
        if (loanPrintData == null || loanPrintData.length() == 0) {
            Toast.makeText(this, getString(R.string.empty), Toast.LENGTH_LONG).show();
            return;
        }
        printContent = "" + Html.fromHtml(loanPrintData);
//        printContent = "" + loanPrintData;

        if (LowBattery == true) {
            handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
        } else {
            if (!nopaper) {
                progressDialog = ProgressDialog.show(this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                handler.sendMessage(handler.obtainMessage(PRINTCONTENT, 1, 0, null));
            } else {
                Toast.makeText(this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void printReceiptPicture() {

//        picturePath = fileReceipt;
        picturePath = fileReceipt.getAbsolutePath();

        if (LowBattery == true) {
            handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
        } else {
            if (!nopaper) {
                progressDialog = ProgressDialog.show(this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                handler.sendMessage(handler.obtainMessage(PRINTPICTURE, 1, 0, null));
            } else {
                Toast.makeText(this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void savepic() {
        File file = new File(picturePath);
        if (!file.exists()) {
            InputStream inputStream = null;
            FileOutputStream fos = null;
            byte[] tmp = new byte[1024];
            try {
                inputStream = getApplicationContext().getAssets().open("syhlogo.png");
                fos = new FileOutputStream(file);
                int length = 0;
                while ((length = inputStream.read(tmp)) > 0) {
                    fos.write(tmp, 0, length);
                }
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void noPaperDlg() {
        android.app.AlertDialog.Builder dlg = new android.app.AlertDialog.Builder(this);
        dlg.setTitle(getString(R.string.noPaper));
        dlg.setMessage(getString(R.string.noPaperNotice));
        dlg.setCancelable(false);
        dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dlg.show();
    }

    public class NonPrintableAdapter extends RecyclerView.Adapter<NonPrintableAdapter.ViewHolder> {

        private Context mContext;
        private boolean isPrintable = false;
        private boolean isDuplicate = false;

        private NonPrintableAdapter(Context mContext) {
            this.mContext = mContext;
        }

        public void setPrintable(boolean printable) {
            isPrintable = printable;
        }

        public void setDuplicate(boolean duplicate) {
            isDuplicate = duplicate;
        }

        @NonNull
        @Override
        public NonPrintableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            System.gc();
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_print_voucher_content, parent, false);
            return new NonPrintableAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final NonPrintableAdapter.ViewHolder holder, int position) {

            System.gc();
            ItemPrintVoucherContentBinding binding = (ItemPrintVoucherContentBinding) holder.getBinding();

            if (isPrintable) {
                binding.txtPrintData.setText(Html.fromHtml(loanPrintData));
            } else {
                binding.txtPrintData.setText(Html.fromHtml(loanPrintData));
            }

            if (isDuplicate) {
                binding.txtDuplicate.setVisibility(View.VISIBLE);
            } else {
                binding.txtDuplicate.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewDataBinding binding;

            ViewHolder(ViewDataBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                this.binding.executePendingBindings();
            }

            public ViewDataBinding getBinding() {
                return binding;
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOPAPER:
                    noPaperDlg();
                    break;
                case LOWBATTERY:
                    android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(LoanPaymentSuccessPrintActivity.this);
                    alertDialog.setTitle(R.string.operation_result);
                    alertDialog.setMessage(getString(R.string.LowBattery));
                    alertDialog.setPositiveButton(getString(R.string.dialog_comfirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alertDialog.show();
                    break;
                case PRINTVERSION:
                    dialog.dismiss();
                    if (msg.obj.equals("1")) {
                        Log.e("", "printVersion: " + printVersion);

                        printDuplicate = getIntent().hasExtra("fromHistory");
                        shareReceiptAction = "printPicture";
                        getReceipt();
                    } else {
                        Toast.makeText(LoanPaymentSuccessPrintActivity.this, R.string.operation_fail, Toast.LENGTH_LONG).show();
                    }
                    break;
                case PRINTCONTENT:
                    new contentPrintThread().start();
                    break;
                case PRINTPICTURE:
                    new picturePrintThread().start();
                    break;
                case CANCELPROMPT:
                    if (progressDialog != null && !LoanPaymentSuccessPrintActivity.this.isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case OVERHEAT:
                    android.app.AlertDialog.Builder overHeatDialog = new android.app.AlertDialog.Builder(LoanPaymentSuccessPrintActivity.this);
                    overHeatDialog.setTitle(R.string.operation_result);
                    overHeatDialog.setMessage(getString(R.string.overTemp));
                    overHeatDialog.setPositiveButton(getString(R.string.dialog_comfirm), (dialogInterface, i) -> {
                    });
                    overHeatDialog.show();
                    break;
                default:
                    Toast.makeText(LoanPaymentSuccessPrintActivity.this, "Print Error!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private class contentPrintThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.reset();

                if (printDuplicate) {
                    mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_RIGHT);
                    mUsbThermalPrinter.setLeftIndent(leftDistance);
                    mUsbThermalPrinter.setLineSpace(lineDistance);
                    mUsbThermalPrinter.setGray(printGray);

                    mUsbThermalPrinter.addString("**Duplicate**");
                }

                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
                mUsbThermalPrinter.setLeftIndent(leftDistance);
                mUsbThermalPrinter.setLineSpace(lineDistance);
                if (wordFont == 4) {
                    mUsbThermalPrinter.setFontSize(52);
                } else if (wordFont == 3) {
                    mUsbThermalPrinter.setTextSize(42);
                } else if (wordFont == 2) {
                    mUsbThermalPrinter.setTextSize(32);
                } else if (wordFont == 1) {
                    mUsbThermalPrinter.setTextSize(22);
                }
                mUsbThermalPrinter.setGray(printGray);

                mUsbThermalPrinter.addString(printContent);

                mUsbThermalPrinter.printString();

                mUsbThermalPrinter.walkPaper(10);
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

    private class picturePrintThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.reset();
                File file = new File(picturePath);
//                File file = fileReceipt;
                if (file.exists()) {

                /*    if(printDuplicate){
                        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_RIGHT);
                        mUsbThermalPrinter.setLeftIndent(leftDistance);
                        mUsbThermalPrinter.setLineSpace(lineDistance);
                        mUsbThermalPrinter.setGray(printGray);

                        mUsbThermalPrinter.addString("**Duplicate**");
                    }*/

                    mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
                    mUsbThermalPrinter.setLeftIndent(leftDistance);
                    mUsbThermalPrinter.setLineSpace(lineDistance);
                    mUsbThermalPrinter.setGray(printGray);

                    if (printDuplicate) {
                        mUsbThermalPrinter.printLogo(BitmapFactory.decodeFile(fileReceiptDuplicate.getAbsolutePath()), false);
                    } else {
                        mUsbThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath), false);
                    }

                    mUsbThermalPrinter.walkPaper(10);
                } else {
                    runOnUiThread(() -> Toast.makeText(LoanPaymentSuccessPrintActivity.this, getString(R.string.not_find_picture), Toast.LENGTH_LONG).show());
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
    }

    @SuppressLint("StaticFieldLeak")
    private class ReceiptImageGenerate extends AsyncTask<String, Void, File> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            showProgressDialog();
        }

        protected File doInBackground(String... params) {
            return Utility.saveBitMap(getApplicationContext(), binding.layoutReceiptShare, params[0]);
        }

        @Override
        protected void onPostExecute(File result) {
//            dismissProgressDialog();

        }
    }


}

