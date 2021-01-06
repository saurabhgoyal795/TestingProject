package com.atlaaya.evdrecharge.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.apiPresenter.PurchaseVoucherBulkPresenter;
import com.atlaaya.evdrecharge.apiPresenter.PurchaseVoucherPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityVoucherConfirmationBinding;
import com.atlaaya.evdrecharge.databinding.ItemRechargeAmountConfirmBinding;
import com.atlaaya.evdrecharge.firebase.references.DocumentRefrence;
import com.atlaaya.evdrecharge.listener.PurchaseVoucherBulkListener;
import com.atlaaya.evdrecharge.listener.PurchaseVoucherListener;
import com.atlaaya.evdrecharge.model.ModelOperator;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ModelVoucher;
import com.atlaaya.evdrecharge.model.ModelVoucherPlan;
import com.atlaaya.evdrecharge.model.ModelVoucherPurchased;
import com.atlaaya.evdrecharge.model.ResponseTempVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherOrderHistory;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchaseBulk;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchaseBulkOrder;
import com.atlaaya.evdrecharge.model.ResponseVoucherSingle;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.ChangeDateFormat;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.MyCustomToast;
import com.atlaaya.evdrecharge.utils.PriceFormat;
import com.atlaaya.evdrecharge.utils.Utility;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class VoucherConfirmationActivity extends BaseActivity implements View.OnClickListener,
        PurchaseVoucherListener, PurchaseVoucherBulkListener {

    ArrayList<ModelVoucherPlan> rechargePlanList;
    private ActivityVoucherConfirmationBinding binding;
    private ModelService selectedService;
    private ModelOperator selectedOperator;
    private ModelVoucherPlan selectedPlan;
    private String clickBtn = "";
    private String mobile = "";
    private String email = "";
    private boolean mobileStatus = false;

    private PurchaseVoucherPresenter presenter;
    private PurchaseVoucherBulkPresenter voucherBulkPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voucher_confirmation);

        presenter = new PurchaseVoucherPresenter();
        presenter.setView(this);

        voucherBulkPresenter = new PurchaseVoucherBulkPresenter();
        voucherBulkPresenter.setView(this);

        if (getIntent().hasExtra("plan")) {
            selectedPlan = getIntent().getParcelableExtra("plan");
        }
        if (getIntent().hasExtra("mobileStatus")) {
            mobileStatus = getIntent().getBooleanExtra("mobileStatus",false);
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

        if (selectedService != null) {
            binding.toolbar.setTitle(selectedService.getService_name());
        } else {
            binding.toolbar.setTitle(getString(R.string.txt_voucher_recharge));
        }

        binding.cardSelectedPlan.setVisibility(View.VISIBLE);

       /* if (selectedPlan != null) {
            binding.cardSelectedPlan.setVisibility(View.VISIBLE);
            binding.txtAmount.setText(String.format("%s %s", selectedPlan.getAmount(), getString(R.string.currency_ethiopia_unit)));
            binding.txtQuantity.setText(String.format("%s: %d", getString(R.string.hint_quantity), selectedPlan.getSelectedQty()));
        }*/

        if (rechargePlanList == null) {
            rechargePlanList = new ArrayList<>();
        }
        setPlanData();
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
        Intent intent;
        switch (v.getId()) {
            case R.id.btnPrint:
                if (rechargePlanList == null || rechargePlanList.isEmpty()) {
                    MyCustomToast.showErrorAlert(this, "Nothing to print. Please select at least 1 voucher.");
                } else {
                    clickBtn = "Print";
                    callVoucherPurchaseBulk();
                }

                break;
            case R.id.btnSendSMS:

                mobile = binding.textInputMobile.getEditText().getText().toString().trim();

                if (Objects.requireNonNull(mobile).isEmpty()) {
                    binding.textInputMobile.requestFocus();
                    MyCustomToast.showErrorAlert(this, getString(R.string.alert_enter_mobile_number));
                } else if (rechargePlanList == null || rechargePlanList.isEmpty()) {
                    MyCustomToast.showErrorAlert(this, "Nothing to print. Please select at least 1 voucher.");
                } else {
                    clickBtn = "SMS";
                    callVoucherPurchaseBulk();
                }

                break;
            case R.id.btnSendEmail:

                email = binding.textInputEmail.getEditText().getText().toString().trim();

                if (Objects.requireNonNull(email).isEmpty()) {
                    binding.textInputEmail.requestFocus();
                    MyCustomToast.showErrorAlert(this, getString(R.string.alert_enter_email));
                } else if (!Utility.isValidEmail(email)) {
                    binding.textInputEmail.requestFocus();
                    MyCustomToast.showErrorAlert(this, getString(R.string.alert_enter_email_valid));
                } else if (rechargePlanList == null || rechargePlanList.isEmpty()) {
                    MyCustomToast.showErrorAlert(this, "Nothing to print. Please select at least 1 voucher.");
                } else {
                    clickBtn = "Email";
                    callVoucherPurchaseBulk();
                }
                break;
        }
    }

    private void setPlanData() {
        binding.recyclerView.setAdapter(new RechargeAmountAdapter(this));
        setGranTtlAmount();
    }

    private void setGranTtlAmount() {
        double ttlAmount = 0;
        for (ModelVoucherPlan voucherPlan : rechargePlanList) {
            ttlAmount = ttlAmount + voucherPlan.getAmount() * voucherPlan.getSelectedQty();
        }
        binding.txtTtlAmount.setText(getString(R.string.txt_grand_ttl, PriceFormat.decimalTwoDigit1(ttlAmount)));
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

    private void callVoucherPurchase() {
        if (CheckInternetConnection.isInternetConnection(this)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(this);
            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                map.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + userInfo.getId()));
                map.put("username", RequestBody.create(MediaType.parse("multipart/form-data"), userInfo.getUsername()));
                map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_EMAIL)));
                map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_PASSWORD)));
                map.put("service_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + selectedService.getId()));
                map.put("operator_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + selectedOperator.getId()));
                map.put("voucher_amount_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + selectedPlan.getId()));
                map.put("quantity", RequestBody.create(MediaType.parse("multipart/form-data"), "" + selectedPlan.getSelectedQty()));
                map.put("temp_voucher_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + getIntent().getIntExtra("temp_voucher_id", 0)));

                if (clickBtn.equals("Email")) {
                    map.put("email_send", RequestBody.create(MediaType.parse("multipart/form-data"), email));
                } else if (clickBtn.equals("SMS")) {
                    map.put("mobile_send", RequestBody.create(MediaType.parse("multipart/form-data"), mobile));
                }

                presenter.purchaseVoucher(this, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    private void callVoucherPurchaseBulk() {
        if(mobileStatus) {
            callVoucherPurchaseBulkOffline();
        }else {
            if (CheckInternetConnection.isInternetConnection(this)) {
                ModelUserInfo userInfo = SessionManager.getUserDetail(this);
                if (userInfo != null) {
                    HashMap<String, RequestBody> map = new HashMap<>();
                    map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                    map.put("username", RequestBody.create(MediaType.parse("multipart/form-data"), userInfo.getUsername()));
                    map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_PASSWORD)));
                    map.put("service_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + selectedService.getId()));
                    map.put("operator_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + selectedOperator.getId()));

                    if (clickBtn.equals("Email")) {
                        map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), email));
                    } else if (clickBtn.equals("SMS")) {
                        map.put("mobile", RequestBody.create(MediaType.parse("multipart/form-data"), mobile));
                    }

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

                    voucherBulkPresenter.voucherBulkOrder(this, map);
                }
            } else {
                DialogClasses.showDialogInternetAlert(this);
            }
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

                voucherBulkPresenter.voucherBulkOrderDetail(this, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    private void callVoucherPurchaseBulkOffline() {

        showProgressDialog();

        ModelUserInfo userInfo = SessionManager.getUserDetail(this);

    /*    DatabaseReference db = Database.voucherNonPrints(databaseReference(), "" + userInfo.getId());
        db.addValueEventListener(new MapTypedValueEventListener<ModelVoucher>(ModelVoucher.class) {
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dismissProgressDialog();
            }

            @Override
            public void onDataChange(Map data) {
                db.removeEventListener(this);
                ArrayList<ModelVoucher> vouchers = null;
                if (data != null) {
                    vouchers = new ArrayList<>(data.values());
                }

                ArrayList<ModelVoucher> finalList = new ArrayList<>();
                if (vouchers != null && !vouchers.isEmpty()) {
                    for (ModelVoucher voucher : vouchers) {
                        if (voucher.getIs_print() == 0) {
                            finalList.add(voucher);
                        }
                    }
                }

                if (finalList.size() == 0) {
                    dismissProgressDialog();
                    MyCustomToast.showToast(getApplicationContext(), "No vouchers available for sell. You need to purchase vouchers.");
                } else {
                    ArrayList<ModelVoucherPlan> voucherSelectedList = new ArrayList<>();
                    for (ModelVoucherPlan voucherPlan : rechargePlanList) {
                        if (voucherPlan.getSelectedQty() > 0) {
                            voucherSelectedList.add(voucherPlan);
                        }
                    }
                    boolean isNoStock = false;

                    ArrayList<ModelVoucher> finalPurchasedVoucherList = new ArrayList<>();
                    for (ModelVoucherPlan voucherSelected : voucherSelectedList) {
                        ArrayList<ModelVoucher> selectedToSellVoucherList = new ArrayList<>();
                        for (ModelVoucher voucher : finalList) {
                            if (voucher.getVoucher_amount() == voucherSelected.getAmount()) {
                                selectedToSellVoucherList.add(voucher);
                            }
                        }
                        if (selectedToSellVoucherList.size() >= voucherSelected.getSelectedQty()) {
                            finalPurchasedVoucherList.addAll(selectedToSellVoucherList.subList(0, voucherSelected.getSelectedQty()));
//                                    finalPurchasedVoucherList.addAll(selectedToSellVoucherList.subList(0,
//                                            voucherSelected.getSelectedQty()==1?voucherSelected.getSelectedQty():voucherSelected.getSelectedQty()-1));
                        } else {
                            MyCustomToast.showToast(getApplicationContext(),
                                    "No stock available of voucher amount " + voucherSelected.getAmount()
                                            + ". You need to purchase vouchers.");
                            isNoStock = true;
                            dismissProgressDialog();
                            break;
                        }
                    }
                    if (isNoStock)
                        return;

                    if (finalPurchasedVoucherList.isEmpty()) {
                        dismissProgressDialog();
                        MyCustomToast.showToast(getApplicationContext(), "No vouchers available for sell. You need to purchase vouchers.");
                    } else {

                        ArrayList<ModelVoucherPurchased> voucherPurchasedList = new ArrayList<>();

                        Map<String, Object> map = new HashMap<>();

                        long currentTimeMillis = System.currentTimeMillis();
                        String offlineSoldDate = ChangeDateFormat.getDateTimeFromMillisecond("yyyy-MM-dd HH:mm:ss", currentTimeMillis);
                        String offlineOrder_id = "of_evd_"+userInfo.getId()+"_"+currentTimeMillis;

                        for (ModelVoucher voucher : finalPurchasedVoucherList) {
                            voucher.setIs_print(1);
                            voucher.setOffline_sold_date(offlineSoldDate);
                            voucher.setOffline_order_id(offlineOrder_id);

                            ModelVoucherPurchased modelVoucherPurchased = new ModelVoucherPurchased();
                            modelVoucherPurchased.setPrintable_text(AppConstants.getPinPrintableData(voucher, userInfo));
                            modelVoucherPurchased.setNon_printable_text(AppConstants.getPinNonPrintableData(voucher, userInfo));
                            modelVoucherPurchased.setSaleid(voucher.getOrder_id());
                            modelVoucherPurchased.setVchr_odr_id(voucher.getOffline_order_id());

                            voucherPurchasedList.add(modelVoucherPurchased);

                            map.put("/" + voucher.getVoucher_id() + "/", voucher);
                        }

                        MyCustomToast.showToast(getApplicationContext(),
                                "Success");

                        dismissProgressDialog();
                        Intent intent = new Intent(getApplicationContext(), SendingRequestActivity.class);
                        intent.putExtra("service", selectedService);
                        intent.putExtra("operator", selectedOperator);

                        VoucherSuccessPrintActivity.setPrintVoucherContentArrayList(voucherPurchasedList);

                        if (clickBtn.equals("Email")) {
                            intent.putExtra("email", email);
                        } else if (clickBtn.equals("SMS")) {
                            intent.putExtra("mobile", mobile);
                        }
                        startActivityForResult(intent, 200);

                        Database.voucherNonPrints(databaseReference(), "" + userInfo.getId())
                                .updateChildren(map);
                    }
                }
            }
        });*/


        DocumentRefrence.vouchersNonPrinted(firebaseFirestore(),  userInfo.getId())
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<ModelVoucher> finalList = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ModelVoucher voucher = document.toObject(ModelVoucher.class);
                            finalList.add(voucher);
                        }

                        if (finalList.size() == 0) {
                            dismissProgressDialog();
                            MyCustomToast.showToast(getApplicationContext(), "No vouchers available for sell. You need to purchase vouchers.");
                        } else {
                            ArrayList<ModelVoucherPlan> voucherSelectedList = new ArrayList<>();
                            for (ModelVoucherPlan voucherPlan : rechargePlanList) {
                                if (voucherPlan.getSelectedQty() > 0) {
                                    voucherSelectedList.add(voucherPlan);
                                }
                            }
                            boolean isNoStock = false;

                            ArrayList<ModelVoucher> finalPurchasedVoucherList = new ArrayList<>();
                            for (ModelVoucherPlan voucherSelected : voucherSelectedList) {
                                ArrayList<ModelVoucher> selectedToSellVoucherList = new ArrayList<>();
                                for (ModelVoucher voucher : finalList) {
                                    if (voucher.getVoucher_amount() == voucherSelected.getAmount()) {
                                        selectedToSellVoucherList.add(voucher);
                                    }
                                }
                                if (selectedToSellVoucherList.size() >= voucherSelected.getSelectedQty()) {
                                    finalPurchasedVoucherList.addAll(selectedToSellVoucherList.subList(0, voucherSelected.getSelectedQty()));
                                } else {
                                    MyCustomToast.showToast(getApplicationContext(),
                                            "No stock available of voucher amount " + voucherSelected.getAmount()
                                                    + ". You need to purchase vouchers.");
                                    isNoStock = true;
                                    dismissProgressDialog();
                                    break;
                                }
                            }
                            if (isNoStock)
                                return;

                            if (finalPurchasedVoucherList.isEmpty()) {
                                dismissProgressDialog();
                                MyCustomToast.showToast(getApplicationContext(), "No vouchers available for sell. You need to purchase vouchers.");
                            } else {

                                ArrayList<ModelVoucherPurchased> voucherPurchasedList = new ArrayList<>();

//                                Map<String, Object> map = new HashMap<>();

                                long currentTimeMillis = System.currentTimeMillis();
                                String offlineSoldDate = ChangeDateFormat.getDateTimeFromMillisecond("yyyy-MM-dd HH:mm:ss", currentTimeMillis);
                                String offlineOrder_id = "of_evd_"+userInfo.getId()+"_"+currentTimeMillis;

                                // Get a new write batch
                                WriteBatch batch = firebaseFirestore().batch();

                                for (ModelVoucher voucher : finalPurchasedVoucherList) {
                                    voucher.setIs_print(1);
                                    voucher.setOffline_sold_date(offlineSoldDate);
                                    voucher.setOffline_order_id(offlineOrder_id);

                                    ModelVoucherPurchased modelVoucherPurchased = new ModelVoucherPurchased();
                                    modelVoucherPurchased.setPrintable_text(AppConstants.getPinPrintableData(voucher, userInfo));
                                    modelVoucherPurchased.setNon_printable_text(AppConstants.getPinNonPrintableData(voucher, userInfo));
                                    modelVoucherPurchased.setSaleid(voucher.getOrder_id());
                                    modelVoucherPurchased.setVchr_odr_id(voucher.getOffline_order_id());
                                    modelVoucherPurchased.setVoucher(voucher);

                                    voucherPurchasedList.add(modelVoucherPurchased);

//                                    map.put("/" + voucher.getVoucher_id() + "/", voucher);

                                    DocumentReference sfRef = DocumentRefrence.updateVouchers(firebaseFirestore(), userInfo.getId())
                                            .document(""+voucher.getVoucher_id());
//                batch.update(sfRef, ""+voucher.getVoucher_id(), voucher);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("is_print", voucher.getIs_print());
                                    map.put("offline_sold_date", voucher.getOffline_sold_date());
                                    map.put("offline_order_id", voucher.getOffline_order_id());
                                    batch.update(sfRef, map);

                                }

                                // Commit the batch
                                batch.commit().addOnCompleteListener(task1 -> {
                                    // ...
                                });

                                MyCustomToast.showToast(getApplicationContext(),
                                        "Success");

                                dismissProgressDialog();
                                Intent intent = new Intent(getApplicationContext(), SendingRequestActivity.class);
                                intent.putExtra("service", selectedService);
                                intent.putExtra("operator", selectedOperator);

                                VoucherSuccessPrintActivity.setPrintVoucherContentArrayList(voucherPurchasedList);

                                if (clickBtn.equals("Email")) {
                                    intent.putExtra("email", email);
                                } else if (clickBtn.equals("SMS")) {
                                    intent.putExtra("mobile", mobile);
                                }
                                startActivityForResult(intent, 200);
                            }
                        }
                    } else {
                        Log.d("vouchers", "Error getting documents: ", task.getException());
                        dismissProgressDialog();
                    }
                })
                .addOnFailureListener(e -> dismissProgressDialog())
                .addOnCanceledListener(this::dismissProgressDialog);

    }

    @Override
    public void onSuccessTempVoucher(ResponseTempVoucherPurchase body) {
    }

    @Override
    public void onSuccessVoucher(ResponseVoucherPurchase body) {
        Intent intent;
        if (body.isRESPONSE()) {
            intent = new Intent(this, SendingRequestActivity.class);
            intent.putExtra("service", selectedService);
            intent.putExtra("operator", selectedOperator);
            if (selectedPlan != null) {
                intent.putExtra("plan", selectedPlan);
            }
            intent.putExtra("printData", body.getRESPONSE_DATA());

            if (clickBtn.equals("Email")) {
                intent.putExtra("email", email);
            } else if (clickBtn.equals("SMS")) {
                intent.putExtra("mobile", mobile);
            }
            startActivityForResult(intent, 200);

        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
    }

    @Override
    public void onSuccessSinglePrintVoucher(ResponseVoucherPurchase body) {

    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onSuccessVoucherBulkOrder(ResponseVoucherPurchaseBulk body) {
        if (body.isRESPONSE()) {
            callVoucherPurchaseBulkOrderDetail(body.getVoucherPurchased().getVchr_odr_id());
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
    }

    @Override
    public void onSuccessVoucherBulkOrderPrintDetail(ResponseVoucherPurchaseBulkOrder body) {
        if (body.isRESPONSE()) {
            runOnUiThread(() -> {
                Intent intent = new Intent(getApplicationContext(), SendingRequestActivity.class);
                intent.putExtra("service", selectedService);
                intent.putExtra("operator", selectedOperator);

                VoucherSuccessPrintActivity.setPrintVoucherContentArrayList(body.getVoucherPurchasedList());
//                intent.putParcelableArrayListExtra("printDataList", body.getVoucherPurchasedList());

                if (clickBtn.equals("Email")) {
                    intent.putExtra("email", email);
                } else if (clickBtn.equals("SMS")) {
                    intent.putExtra("mobile", mobile);
                }
                startActivityForResult(intent, 200);
            });
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
    }

    @Override
    public void onSuccessVoucherBulkOrderHistory(ResponseVoucherOrderHistory body) {

    }

    public class RechargeAmountAdapter extends RecyclerView.Adapter<RechargeAmountAdapter.ViewHolder> {

        private Context mContext;

        private RechargeAmountAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public RechargeAmountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_recharge_amount_confirm, parent, false);
            return new RechargeAmountAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final RechargeAmountAdapter.ViewHolder holder, int position) {
            ItemRechargeAmountConfirmBinding binding = (ItemRechargeAmountConfirmBinding) holder.getBinding();

            binding.txtQty.setText(String.format("x %s", rechargePlanList.get(holder.getAdapterPosition()).getSelectedQty()));
            binding.txtAmount.setText(String.format("%s %s", PriceFormat.decimalTwoDigit1(rechargePlanList.get(position).getAmount()), getString(R.string.currency_ethiopia_unit)));

            int ttlQuantity = rechargePlanList.get(holder.getAdapterPosition()).getSelectedQty();
            binding.txtTtlAmount.setText(PriceFormat.decimalTwoDigit1(rechargePlanList.get(position).getAmount() * ttlQuantity));

        }

        @Override
        public int getItemCount() {
            return
                    rechargePlanList.size();
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

}


