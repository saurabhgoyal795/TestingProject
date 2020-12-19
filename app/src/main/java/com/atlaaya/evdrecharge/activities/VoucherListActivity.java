package com.atlaaya.evdrecharge.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.atlaaya.evdrecharge.apiPresenter.PurchaseVoucherPresenter;
import com.atlaaya.evdrecharge.apiPresenter.VoucherPlansPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityVoucherListBinding;
import com.atlaaya.evdrecharge.databinding.ItemRechargeAmountBinding;
import com.atlaaya.evdrecharge.listener.PurchaseVoucherListener;
import com.atlaaya.evdrecharge.listener.VoucherPlanListener;
import com.atlaaya.evdrecharge.model.ModelOperator;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ModelVoucherPlan;
import com.atlaaya.evdrecharge.model.ResponseTempVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherPlan;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchase;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.MyCustomToast;
import com.atlaaya.evdrecharge.utils.PriceFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class VoucherListActivity extends BaseActivity implements View.OnClickListener,
        PurchaseVoucherListener, VoucherPlanListener {

    private ActivityVoucherListBinding binding;

    private ModelService selectedService;
    private ModelOperator selectedOperator;
    private ModelVoucherPlan selectedPlan;

    private ArrayList<ModelVoucherPlan> rechargePlanList;

    private PurchaseVoucherPresenter presenter;
    private VoucherPlansPresenter voucherPlansPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voucher_list);

        presenter = new PurchaseVoucherPresenter();
        presenter.setView(this);

        voucherPlansPresenter = new VoucherPlansPresenter();
        voucherPlansPresenter.setView(this);

        rechargePlanList = new ArrayList<>();

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

        binding.btnProceed.setOnClickListener(this);

//        binding.btnProceed.setVisibility(View.GONE);

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
        if (v.getId() == R.id.btnProceed) {

            ArrayList<ModelVoucherPlan> selectedVoucherPlans = new ArrayList<>();

            for (ModelVoucherPlan voucherPlan : rechargePlanList) {
                if(voucherPlan.getSelectedQty()>0){
                    selectedVoucherPlans.add(voucherPlan);
                }
            }

            if (selectedVoucherPlans.isEmpty()) {
                MyCustomToast.showErrorAlert(VoucherListActivity.this, "Min voucher quantity should be 1.");
            } else {
//                callTemAssign();

                Intent intent = new Intent(this, VoucherConfirmationActivity.class);
                intent.putExtra("service", selectedService);
                intent.putExtra("operator", selectedOperator);
                intent.putParcelableArrayListExtra("planList", selectedVoucherPlans);
                startActivityForResult(intent, 200);
            }
        }
    }

    @java.lang.Override
    protected void onStart() {
        super.onStart();
        callVoucherPlanListAPI();
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

    @java.lang.Override
    public void onSuccessTempVoucher(ResponseTempVoucherPurchase body) {

        if (body.isRESPONSE()) {
            Intent intent = new Intent(this, VoucherConfirmationActivity.class);
            intent.putExtra("service", selectedService);
            intent.putExtra("operator", selectedOperator);
            intent.putExtra("plan", selectedPlan);
            intent.putExtra("temp_voucher_id", body.getRESPONSE_DATA());
            startActivityForResult(intent, 200);
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
    }

    @java.lang.Override
    public void onSuccessVoucher(ResponseVoucherPurchase body) {
    }

    @java.lang.Override
    public void onSuccess(ResponseVoucherPlan body) {
        rechargePlanList.clear();
        if (body.isRESPONSE()) {
            rechargePlanList.addAll(body.getVoucherPlanList());
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }

        setPlanData();
    }

    @java.lang.Override
    public Context getContext() {
        return this;
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

    private void callVoucherPlanListAPI() {
        if (CheckInternetConnection.isInternetConnection(this)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(this);
            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                map.put("operator_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + selectedOperator.getId()));

                voucherPlansPresenter.voucherAmounts(this, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    private void callTemAssign() {
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


           /*     List<Integer> voucher_amount_ids = new ArrayList<>();
                List<Integer> quantitys = new ArrayList<>();
                for (ModelVoucherPlan voucherPlan : rechargePlanList) {
                    if(voucherPlan.getSelectedQty()>0){
                        voucher_amount_ids.add(voucherPlan.getId());
                        quantitys.add(voucherPlan.getSelectedQty());
                    }
                }
                for (int i = 0; i < voucher_amount_ids.size(); i++) {
                    map.put("voucher_amount_id["+i+"]", RequestBody.create(MediaType.parse("multipart/form-data"), "" + voucher_amount_ids.get(i)));
                    map.put("quantity["+i+"]", RequestBody.create(MediaType.parse("multipart/form-data"), "" + quantitys.get(i)));
                }*/

                presenter.tempVoucher(this, map);
//                presenter.tempVoucherBulkOrder(this, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    public class RechargeAmountAdapter extends RecyclerView.Adapter<RechargeAmountAdapter.ViewHolder> {

        private Context mContext;

        private RechargeAmountAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_recharge_amount, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            ItemRechargeAmountBinding binding = (ItemRechargeAmountBinding) holder.getBinding();

            binding.txtAmount.setText(String.format("%s %s", rechargePlanList.get(position).getAmount(), getString(R.string.currency_ethiopia_unit)));

            int ttlQuantity = rechargePlanList.get(holder.getAdapterPosition()).getSelectedQty();
            if(ttlQuantity>0){
                binding.etQuantity.setText(String.valueOf(ttlQuantity));
            }else{
                binding.etQuantity.setText("");
            }

            binding.txtTtlAmount.setText(String.format("%s %s",
                    rechargePlanList.get(position).getAmount() * ttlQuantity,
                    getString(R.string.currency_ethiopia_unit)));

//            binding.txtDescription.setText(rechargePlanList.get(position).getDescription());

            binding.etQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String value = binding.etQuantity.getText().toString().trim();
                    if (!value.isEmpty()) {
                        rechargePlanList.get(holder.getAdapterPosition()).setSelectedQty(Integer.parseInt(value));
                    }
                    int ttlQuantity = rechargePlanList.get(holder.getAdapterPosition()).getSelectedQty();
                    binding.txtTtlAmount.setText(String.format("%s %s",
                            rechargePlanList.get(position).getAmount() * ttlQuantity,
                            getString(R.string.currency_ethiopia_unit)));

                    setGranTtlAmount();
//                    notifyDataSetChanged();
                }
            });

            binding.btnSelect.setOnClickListener(v -> {

                String value = binding.etQuantity.getText().toString().trim();
                if (value.isEmpty() || Integer.parseInt(value) < 1) {
                    MyCustomToast.showErrorAlert(VoucherListActivity.this, "Min voucher quantity should be 1.");
                } else {
                    selectedPlan = rechargePlanList.get(holder.getAdapterPosition());
                    selectedPlan.setSelectedQty(Integer.parseInt(value));

                    callTemAssign();

    /*                Intent intent = new Intent(VoucherListActivity.this, VoucherConfirmationActivity.class);
                    intent.putExtra("service", selectedService);
                    intent.putExtra("operator", selectedOperator);
                    intent.putExtra("plan", rechargePlanList.get(holder.getAdapterPosition()));
                    startActivityForResult(intent, 200);*/
                }
            });
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

