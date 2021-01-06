package com.atlaaya.evdrecharge.activities.offline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.atlaaya.evdrecharge.MyApplication;
import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.activities.BaseActivity;
import com.atlaaya.evdrecharge.apiPresenter.PurchaseVoucherBulkPresenter;
import com.atlaaya.evdrecharge.apiPresenter.PurchaseVoucherPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityVoucherConfirmationBinding;
import com.atlaaya.evdrecharge.databinding.ActivityVoucherPurchaseConfirmationBinding;
import com.atlaaya.evdrecharge.databinding.ItemRechargeAmountConfirmBinding;
import com.atlaaya.evdrecharge.firebase.database.MapTypedValueEventListener;
import com.atlaaya.evdrecharge.firebase.references.Database;
import com.atlaaya.evdrecharge.listener.PurchaseVoucherBulkListener;
import com.atlaaya.evdrecharge.listener.PurchaseVoucherListener;
import com.atlaaya.evdrecharge.model.ModelOperator;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ModelVoucher;
import com.atlaaya.evdrecharge.model.ModelVoucherPlan;
import com.atlaaya.evdrecharge.model.ResponseTempVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherOrderHistory;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchaseBulk;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchaseBulkOrder;
import com.atlaaya.evdrecharge.model.ResponseVoucherSingle;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.MyCustomToast;
import com.atlaaya.evdrecharge.utils.PriceFormat;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class VoucherPurchaseConfirmationActivity extends BaseActivity implements View.OnClickListener,
        PurchaseVoucherListener, PurchaseVoucherBulkListener {

    ArrayList<ModelVoucherPlan> rechargePlanList;
    private ActivityVoucherPurchaseConfirmationBinding binding;
    private ModelService selectedService;
    private ModelOperator selectedOperator;
    private ModelVoucherPlan selectedPlan;
    private String clickBtn = "";
    private String mobile = "";
    private String email = "";

    private PurchaseVoucherPresenter presenter;
    private PurchaseVoucherBulkPresenter voucherBulkPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voucher_purchase_confirmation);

        presenter = new PurchaseVoucherPresenter();
        presenter.setView(this);

        voucherBulkPresenter = new PurchaseVoucherBulkPresenter();
        voucherBulkPresenter.setView(this);

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
        switch (v.getId()) {
            case R.id.btnBuyNow:
                if (rechargePlanList == null || rechargePlanList.isEmpty()) {
                    MyCustomToast.showErrorAlert(this, "Nothing to buy. Please select at least 1 voucher.");
                } else {
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

    private void callVoucherPurchaseBulk() {
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

                voucherBulkPresenter.voucherBulkOrderFirebase(this, map);
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

                voucherBulkPresenter.voucherBulkOrderDetailFirebase(this, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }


    @Override
    public void onSuccessTempVoucher(ResponseTempVoucherPurchase body) {
    }

    @Override
    public void onSuccessVoucher(ResponseVoucherPurchase body) {

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
//            loadVouchersFirebase();
            MyApplication.getInstance().enableVoucherSync();

            MyCustomToast.showToast(this, "Voucher purchased successfully.");
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
    }

    @Override
    public void onSuccessVoucherBulkOrderHistory(ResponseVoucherOrderHistory body) {

    }

    private void loadVouchersFirebase() {
        ModelUserInfo userInfo = SessionManager.getUserDetail(this);
        DatabaseReference db =  Database.voucherNonPrints(databaseReference(), "" + userInfo.getId());
        db.addValueEventListener(new MapTypedValueEventListener<ModelVoucher>(ModelVoucher.class) {
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }

                    @Override
                    public void onDataChange(Map data) {
                        db.removeEventListener(this);
                    }
                });
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

            binding.txtQty.setText(String.format("x %s", String.valueOf(rechargePlanList.get(holder.getAdapterPosition()).getSelectedQty())));
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

