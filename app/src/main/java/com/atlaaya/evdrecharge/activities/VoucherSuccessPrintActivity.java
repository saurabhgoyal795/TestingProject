package com.atlaaya.evdrecharge.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityVoucherSuccessBinding;
import com.atlaaya.evdrecharge.model.ModelOperator;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ModelVoucherPlan;
import com.atlaaya.evdrecharge.model.ModelVoucherPurchased;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.BluetoothUtil;
import com.atlaaya.evdrecharge.utils.SunmiPrintHelper;
import com.atlaaya.evdrecharge.utils.Utility;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.printer.UsbThermalPrinter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class VoucherSuccessPrintActivity extends BaseActivity {

    private static ArrayList<ModelVoucherPurchased> voucherPurchasedArrayList;
    private final int NOPAPER = 3;
    private final int LOWBATTERY = 4;
    private final int PRINTVERSION = 5;
    private final int PRINTCONTENT = 9;
    private final int CANCELPROMPT = 10;
    private final int PRINTERR = 11;
    private final int OVERHEAT = 12;
    private final int PRINTPICTURE = 14;
    private String printby = "";
    private String printVersion;
    private int leftDistance = 0; // default
    Bitmap bitmap;
    private int lineDistance = 0; // default
    private int wordFont = 1; // default
    private int printGray = 1; // default
    private MyHandler handler;
    private ProgressDialog dialog;
    private UsbThermalPrinter mUsbThermalPrinter = new UsbThermalPrinter(VoucherSuccessPrintActivity.this);
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
    private ModelService selectedService;
    private ModelOperator selectedOperator;
    private ModelVoucherPlan selectedPlan;
    private ModelVoucherPurchased voucherPrintData;

    private File fileReceipt, fileReceiptDuplicate;
    private String shareReceiptAction = "";
    private boolean printDuplicate = false;

    private ModelUserInfo userInfo;

    public static ArrayList<ModelVoucherPurchased> getPrintVoucherContentArrayList() {
        if (voucherPurchasedArrayList == null) {
            voucherPurchasedArrayList = new ArrayList<>();
        }
        return voucherPurchasedArrayList;
    }

    public static void setPrintVoucherContentArrayList(ArrayList<ModelVoucherPurchased> voucherArrayList) {

        if (voucherPurchasedArrayList == null) {
            voucherPurchasedArrayList = new ArrayList<>();
        }
        voucherPurchasedArrayList.clear();
        if (voucherArrayList != null) {
            voucherPurchasedArrayList.addAll(voucherArrayList);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voucher_success);

        userInfo = SessionManager.getUserDetail(this);

        if (getIntent().hasExtra("plan")) {
            selectedPlan = getIntent().getParcelableExtra("plan");
        }
        if (getIntent().hasExtra("service")) {
            selectedService = getIntent().getParcelableExtra("service");
        }
        if (getIntent().hasExtra("operator")) {
            selectedOperator = getIntent().getParcelableExtra("operator");
        }
        if (getIntent().hasExtra("printData")) {
            voucherPrintData = getIntent().getParcelableExtra("printData");
        }

        if (getIntent().hasExtra("printDataList")) {
            voucherPurchasedArrayList = getIntent().getParcelableArrayListExtra("printDataList");
        }

        if (voucherPurchasedArrayList == null) {
            voucherPurchasedArrayList = new ArrayList<>();
        }

        setSupportActionBar(binding.toolbar);

        if (selectedService != null) {
            binding.toolbar.setTitle(selectedService.getService_name());
        } else {
            binding.toolbar.setTitle(getString(R.string.txt_voucher_recharge));
        }

        if (getIntent().hasExtra("fromHistory")) {
            printDuplicate = true;
            binding.text1.setVisibility(View.GONE);
            binding.text2.setVisibility(View.INVISIBLE);
            binding.toolbar.setTitle(getString(R.string.txt_voucher_information));
        }

        setReceiptInfo();

//        if (AppConstants.IS_POS_APK) {
//            printby = "usb";
//
//            handler = new MyHandler();
//
//            IntentFilter pIntentFilter = new IntentFilter();
//            pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
//            pIntentFilter.addAction("android.intent.action.BATTERY_CAPACITY_EVENT");
//            registerReceiver(printReceive, pIntentFilter);
//
//            dialog = new ProgressDialog(this);
//            dialog.setTitle(R.string.idcard_czz);
//            dialog.setMessage(getText(R.string.watting));
//            dialog.setCancelable(false);
//            dialog.show();
//
//            runOnUiThread(() -> new Thread(() -> {
//                try {
//                    mUsbThermalPrinter.start(0);
//                    mUsbThermalPrinter.reset();
//                    printVersion = mUsbThermalPrinter.getVersion();
//                    int st = mUsbThermalPrinter.checkStatus();
//                    Log.e("yw", "status" + " " + st);
//                } catch (TelpoException e) {
//                    Log.e("yw", "status  111" + " " + e.toString());
//                    e.printStackTrace();
//                } finally {
//                    if (printVersion != null) {
//                        Message message = new Message();
//                        message.what = PRINTVERSION;
//                        message.obj = "1";
//                        handler.sendMessage(message);
//                    } else {
//                        Message message = new Message();
//                        message.what = PRINTVERSION;
//                        message.obj = "0";
//                        handler.sendMessage(message);
//                    }
//                }
//            }).start());
//        } else {
//            printby = "";
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_print_voucher, menu);
//
//        MenuItem action_share = menu.findItem(R.id.action_share);
//        action_share.setVisible(false);
//
//        MenuItem action_print = menu.findItem(R.id.action_print);
//        MenuItem bluetooth_action_print = menu.findItem(R.id.bluetooth_action_print);
//        if (AppConstants.IS_POS_APK) {
//            action_print.setVisible(true);
//            bluetooth_action_print.setVisible(false);
//        } else {
//            action_print.setVisible(false);
//            bluetooth_action_print.setVisible(true);
//        }

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
                printby = "usb";
                shareReceiptAction="printText";
//                shareReceiptAction = "printPicture";
                getReceipt();
                break;
            case R.id.bluetooth_action_print:
                printby = "bluetooth";
                shareReceiptAction="printText";
//                shareReceiptAction = "printPicture";
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
        if (voucherPurchasedArrayList != null) {
            runOnUiThread(() -> {
                System.gc();

                binding.recyclerView.setAdapter(new NonPrintableAdapter(getApplicationContext()));
                for (ModelVoucherPurchased modelVoucherPurchased : voucherPurchasedArrayList) {
                    if (!BluetoothUtil.isBlueToothPrinter) {
                        SunmiPrintHelper.getInstance().setAlign(1);
                        if(printDuplicate){
                            SunmiPrintHelper.getInstance().printText(getString(R.string.txt_duplicate), 23, false, false);
                        }
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inTargetDensity = 160;
                        options.inDensity = 160;
                        if (bitmap == null) {
                            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.merge3, options);

                        }

                        SunmiPrintHelper.getInstance().printBitmap(bitmap, 0);
                        SunmiPrintHelper.getInstance().printText("\n", 24, false, false);

                        SunmiPrintHelper.getInstance().printText((int)modelVoucherPurchased.getVoucher().getVoucher_amount()+" Birr Airtime: \n", 34, true, false);
                        SunmiPrintHelper.getInstance().printText("E-Voucher Pin : \n", 24, false, false);
                        SunmiPrintHelper.getInstance().printText("--------------\n" +
                                modelVoucherPurchased.getVoucher().getPin_no()+"\n" +
                                "-------------- \n", 42, true, false);
                        SunmiPrintHelper.getInstance().printText("E-voucher serial number : \n", 24, false, false);
                        SunmiPrintHelper.getInstance().printText(modelVoucherPurchased.getVoucher().getPin_serial_no()+"\n", 24, true, false);
                        SunmiPrintHelper.getInstance().printText("Txn Ref No: \n" +
                                modelVoucherPurchased.getVoucher().getTxn_ref_num()+"\n" +
                                "Date:"+modelVoucherPurchased.getVoucher().getDate()+"\n" +
                                "To Recharge your mobile :\n" +
                                "Dial *805*e-voucher pin#\n" +
                                "Agent : "+userInfo.getFullName()+"\n" +
                                "Powered By Alami Telecom \n", 24, false, false);
                        SunmiPrintHelper.getInstance().printText("--------------------------------\n", 20, false, false);
                        SunmiPrintHelper.getInstance().feedPaper();
                    }
                }
                System.gc();
          /*      PrintableAdapter printableAdapter = new PrintableAdapter(getApplicationContext());
                printableAdapter.setPrintable(true);
                printableAdapter.setDuplicate(false);
                binding.recyclerViewShare.setAdapter(printableAdapter);

                System.gc();
                PrintableAdapter printableAdapter1 = new PrintableAdapter(getApplicationContext());
                printableAdapter1.setPrintable(true);
                printableAdapter1.setDuplicate(true);
                binding.recyclerViewShareDuplicate.setAdapter(printableAdapter1);*/
            });
        }
    }

    private void getReceipt() {
        if (Build.VERSION.SDK_INT >= 23 && !checkReadWritePermission()) {
            requestReadWritePermission();
            return;
        }

        printReceiptAction();

/*        if (AppConstants.IS_POS_APK) {
            runOnUiThread(() -> {
                showProgressDialog();
                if (fileReceipt == null || fileReceiptDuplicate == null) {
                    try {
                        binding.recyclerViewShare.setVisibility(View.VISIBLE);
                        binding.recyclerViewShareDuplicate.setVisibility(View.GONE);

                        fileReceipt = new ReceiptImageGenerate(binding.recyclerViewShare).execute("1").get();

                        binding.recyclerViewShare.setVisibility(View.GONE);
                        binding.recyclerViewShareDuplicate.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(() -> {
                            try {
                                fileReceiptDuplicate = new ReceiptImageGenerate(binding.recyclerViewShareDuplicate).execute("2").get();
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
        }else {
            printReceiptAction();
        }*/
    }

    private void printReceiptAction() {
   /*     if (AppConstants.IS_POS_APK) {
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
        } else {*/
            switch (shareReceiptAction) {
                case "share":
//                    shareReceipt();
                    break;
                case "printText":
                    printReceiptText();
                    break;
                case "printPicture":
                    printReceiptPicture();
                    break;
            }
//        }
    }

/*    private void shareReceipt() {
        if (fileReceipt != null) {
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
//            Log.i("TAG", "Oops! Image could not be saved.");
            runOnUiThread(() -> {
                showProgressDialog();
                try {
                    binding.recyclerViewShare.setVisibility(View.VISIBLE);
                    binding.recyclerViewShareDuplicate.setVisibility(View.GONE);

                    fileReceipt = new ReceiptImageGenerate(binding.recyclerViewShare).execute("1").get();

                    dismissProgressDialog();
                    if (fileReceipt != null) {
                        shareReceipt();
                    } else {
                        Log.i("TAG", "Oops! Image could not be saved.");
                    }
                } catch (ExecutionException | InterruptedException e) {
                    dismissProgressDialog();
                    e.printStackTrace();
                    Log.i("TAG", "Oops! Image could not be saved.");
                }
            });
        }
    }*/

    private void printReceiptText() {
        if (printby.equals("usb")) {
            if (LowBattery) {
                handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
            } else {
                if (!nopaper) {
                    progressDialog = ProgressDialog.show(this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                    handler.sendMessage(handler.obtainMessage(PRINTCONTENT, 1, 0, null));
                } else {
                    Toast.makeText(this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                }
            }
        } else if (printby.equals("bluetooth")) {
            Intent i = new Intent(getApplicationContext(), BluetoothMainActivity.class);
//            i.putExtra("path", printDuplicate ? fileReceiptDuplicate.getAbsolutePath() : fileReceipt.getAbsolutePath());
            i.putExtra("printDuplicate", printDuplicate);
            startActivity(i);
        }
    }

    private void printReceiptPicture() {
        if (printby.equals("usb")) {
            if (LowBattery) {
                handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
            } else {
                if (!nopaper) {
                    progressDialog = ProgressDialog.show(this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                    handler.sendMessage(handler.obtainMessage(PRINTPICTURE, 1, 0, null));
                } else {
                    Toast.makeText(this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                }
            }
        } else if (printby.equals("bluetooth")) {
            Intent i = new Intent(getApplicationContext(), BluetoothMainActivity.class);
//            i.putExtra("path", printDuplicate ? fileReceiptDuplicate.getAbsolutePath() : fileReceipt.getAbsolutePath());
            i.putExtra("printDuplicate", printDuplicate);
            startActivity(i);
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

    private class NonPrintableAdapter extends RecyclerView.Adapter<NonPrintableAdapter.ViewHolder> {

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
//            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
//                    R.layout.item_print_voucher_content, parent, false);
//            return new ViewHolder(binding);

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_print_voucher_content, parent, false);
            return new ViewHolder(v);

        }

        @Override
        public void onBindViewHolder(@NonNull final NonPrintableAdapter.ViewHolder holder, int position) {
            System.gc();

            ModelVoucherPurchased  data = voucherPurchasedArrayList.get(position);

           /* ItemPrintVoucherContentBinding binding = (ItemPrintVoucherContentBinding) holder.getBinding();

//            binding.txtPrintData.setVisibility(View.GONE);
            if (isPrintable) {
                binding.txtPrintData.setText(Html.fromHtml(data.getPrintable_text()));
//                binding.webViewPrintData.loadData(data.getPrintable_text(), "text/html", "UTF-8");
            } else {
                binding.txtPrintData.setText(Html.fromHtml(data.getNon_printable_text()));
//                binding.webViewPrintData.loadData(data.getNon_printable_text(), "text/html", "UTF-8");
            }
            if (isDuplicate) {
                binding.txtDuplicate.setVisibility(View.VISIBLE);
            } else {
                binding.txtDuplicate.setVisibility(View.GONE);
            }*/


            if (isPrintable) {
//                holder.txtPrintData.setText(Html.fromHtml(data.getPrintable_text()));
                holder.txtPrintData.setText(Html.fromHtml(AppConstants.getPinPrintableData(data.getVoucher(), userInfo)));
            } else {
//                holder.txtPrintData.setText(Html.fromHtml(data.getNon_printable_text()));
                holder.txtPrintData.setText(Html.fromHtml(AppConstants.getPinNonPrintableData(data.getVoucher(), userInfo)));
                String content = String.valueOf(Html.fromHtml(AppConstants.getPinNonPrintableText(data.getVoucher(), userInfo)));


                if (!BluetoothUtil.isBlueToothPrinter) {
                   // SunmiPrintHelper.getInstance().setAlign(1);
                  //  mUsbThermalPrinter.printLogo(mBitmapLogo, true);

//                    if(printDuplicate){
//                        mUsbThermalPrinter.setTextSize(23);
//                        mUsbThermalPrinter.setBold(false);
//                        mUsbThermalPrinter.addString(getString(R.string.txt_duplicate));
//                    }
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inTargetDensity = 160;
//                    options.inDensity = 160;
//                    if (bitmap == null) {
//                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.merge2, options);
//
//                    }
//                    SunmiPrintHelper.getInstance().printBitmap(bitmap, 0);
//                    SunmiPrintHelper.getInstance().printText("\n", 24, false, false);
//                    SunmiPrintHelper.getInstance().printText((int)data.getVoucher().getVoucher_amount()+" Birr Airtime: \n", 34, true, false);
//                    SunmiPrintHelper.getInstance().printText("E-Voucher Pin : \n", 24, false, false);
//                    SunmiPrintHelper.getInstance().printText("--------------\n" +
//                            data.getVoucher().getPin_no()+"\n" +
//                            "-------------- \n", 42, true, false);
//                    SunmiPrintHelper.getInstance().printText("E-voucher serial number : \n", 24, false, false);
//                    SunmiPrintHelper.getInstance().printText(data.getVoucher().getPin_serial_no()+"\n", 24, true, false);
//                    SunmiPrintHelper.getInstance().printText("Txn Ref No: \n" +
//                            data.getVoucher().getTxn_ref_num()+"\n" +
//                            "Date:"+data.getVoucher().getDate()+"\n" +
//                            "To Recharge your mobile :\n" +
//                            "Dial *805*e-voucher pin#\n" +
//                            "Agent : "+userInfo.getFullName()+"\n" +
//                            "Powered By Highlight Trading \n", 24, false, false);
//                    SunmiPrintHelper.getInstance().printText("--------------------------------\n", 20, false, false);
//                    SunmiPrintHelper.getInstance().feedPaper();
                }
            }
            if (isDuplicate) {
                holder.txtDuplicate.setVisibility(View.VISIBLE);
            } else {
                holder.txtDuplicate.setVisibility(View.GONE);
            }
        }
        public Bitmap scaleBitmap(Bitmap mBitmap) {
            int ScaleSize = 235;//max Height or width to Scale
            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();
            float excessSizeRatio = width > height ? width / ScaleSize : height / ScaleSize;
            Bitmap bitmap = Bitmap.createBitmap(
                    mBitmap, 0, 0,(int) (width/excessSizeRatio),(int) (height/excessSizeRatio));
            //mBitmap.recycle(); if you are not using mBitmap Obj
            return bitmap;
        }
        @Override
        public int getItemCount() {
            return voucherPurchasedArrayList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            /*
                    ViewDataBinding binding;
             ViewHolder(ViewDataBinding binding) {
                  super(binding.getRoot());
                  this.binding = binding;
                  this.binding.executePendingBindings();
              }

              public ViewDataBinding getBinding() {
                  return binding;
              }*/

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

    private class PrintableAdapter extends RecyclerView.Adapter<PrintableAdapter.ViewHolder> {

        private Context mContext;
        private boolean isPrintable = false;
        private boolean isDuplicate = false;

        private PrintableAdapter(Context mContext) {
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
        public PrintableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            System.gc();
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_print_voucher_content_printer, parent, false);
            return new ViewHolder(v);

        }

        @Override
        public void onBindViewHolder(@NonNull final PrintableAdapter.ViewHolder holder, int position) {
            System.gc();

            ModelVoucherPurchased  data = voucherPurchasedArrayList.get(position);

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
            /*
                    ViewDataBinding binding;
             ViewHolder(ViewDataBinding binding) {
                  super(binding.getRoot());
                  this.binding = binding;
                  this.binding.executePendingBindings();
              }

              public ViewDataBinding getBinding() {
                  return binding;
              }*/

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

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOPAPER:
                    noPaperDlg();
                    break;
                case LOWBATTERY:
                    android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(VoucherSuccessPrintActivity.this);
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
                        shareReceiptAction = "printText";
//                        shareReceiptAction = "printPicture";
                        getReceipt();
                    } else {
                        Toast.makeText(VoucherSuccessPrintActivity.this, R.string.operation_fail, Toast.LENGTH_LONG).show();
                    }
                    break;
                case PRINTCONTENT:
                    new contentPrintThread().start();
                    break;
                case PRINTPICTURE:
                    new picturePrintThread().start();
                    break;
                case CANCELPROMPT:
                    if (progressDialog != null && !VoucherSuccessPrintActivity.this.isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case OVERHEAT:
                    android.app.AlertDialog.Builder overHeatDialog = new android.app.AlertDialog.Builder(VoucherSuccessPrintActivity.this);
                    overHeatDialog.setTitle(R.string.operation_result);
                    overHeatDialog.setMessage(getString(R.string.overTemp));
                    overHeatDialog.setPositiveButton(getString(R.string.dialog_comfirm), (dialogInterface, i) -> {
                    });
                    overHeatDialog.show();
                    break;
                default:
                    Toast.makeText(VoucherSuccessPrintActivity.this, "Print Error!", Toast.LENGTH_LONG).show();
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

                Bitmap mBitmapLogo = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.merge3),
                        386, 80, true);

                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
                for (ModelVoucherPurchased modelVoucherPurchased : voucherPurchasedArrayList) {
                    mUsbThermalPrinter.printLogo(mBitmapLogo, true);

                    if(printDuplicate){
                        mUsbThermalPrinter.setTextSize(23);
                        mUsbThermalPrinter.setBold(false);
                        mUsbThermalPrinter.addString(getString(R.string.txt_duplicate));
                    }

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
                            "Powered By Alami Telecom");

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

    private class picturePrintThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.reset();
                if (fileReceipt != null && fileReceipt.exists()) {

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

                    mUsbThermalPrinter.printLogo(BitmapFactory.decodeFile(
                            printDuplicate ? fileReceiptDuplicate.getAbsolutePath() : fileReceipt.getAbsolutePath()), false);

                    mUsbThermalPrinter.walkPaper(10);
                } else {
                    runOnUiThread(() -> Toast.makeText(VoucherSuccessPrintActivity.this, getString(R.string.not_find_picture), Toast.LENGTH_LONG).show());
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


}

