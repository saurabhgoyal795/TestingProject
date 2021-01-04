package com.atlaaya.evdrecharge.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.activities.offline.VoucherPurchaseConfirmationActivity;
import com.atlaaya.evdrecharge.apiPresenter.PurchaseVoucherPresenter;
import com.atlaaya.evdrecharge.apiPresenter.VoucherPlansPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityVoucherListBinding;
import com.atlaaya.evdrecharge.databinding.ItemRechargeAmountBinding;
import com.atlaaya.evdrecharge.firebase.database.MapTypedValueEventListener;
import com.atlaaya.evdrecharge.firebase.references.Database;
import com.atlaaya.evdrecharge.firebase.references.DocumentRefrence;
import com.atlaaya.evdrecharge.listener.PurchaseVoucherListener;
import com.atlaaya.evdrecharge.listener.VoucherPlanListener;
import com.atlaaya.evdrecharge.model.ModelOperator;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ModelVoucher;
import com.atlaaya.evdrecharge.model.ModelVoucherPlan;
import com.atlaaya.evdrecharge.model.ResponseTempVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherPlan;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchase;
import com.atlaaya.evdrecharge.sqlite.DBHelper;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.MyCustomToast;
import com.atlaaya.evdrecharge.utils.PriceFormat;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class VoucherListActivity extends BaseActivity implements View.OnClickListener, VoucherPlanListener {

    private ActivityVoucherListBinding binding;

    private ModelService selectedService;
    private ModelOperator selectedOperator;
    private ModelVoucherPlan selectedPlan;
    private ModelUserInfo userInfo;

    private List<ModelVoucher> voucherUnSoldList;
    private ArrayList<ModelVoucherPlan> rechargePlanList;

    private VoucherPlansPresenter voucherPlansPresenter;

    private DBHelper dbHelper;
    private ListenerRegistration listenerRegistration;
    boolean offline = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voucher_list);
        dbHelper = DBHelper.getInstance(this);
        userInfo = SessionManager.getUserDetail(this);

        voucherPlansPresenter = new VoucherPlansPresenter();
        voucherPlansPresenter.setView(this);

        voucherUnSoldList = new ArrayList<>();
        rechargePlanList = new ArrayList<>();

        if (getIntent().hasExtra("service")) {
            selectedService = getIntent().getParcelableExtra("service");
        }
        if (getIntent().hasExtra("offline")) {
            offline = false;
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

//        loadUnsoldVouchers();
//        loadVouchersFirestore();
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
                if (voucherPlan.getSelectedQty() > 0) {
                    selectedVoucherPlans.add(voucherPlan);
                }
            }

            if (selectedVoucherPlans.isEmpty()) {
                MyCustomToast.showErrorAlert(VoucherListActivity.this, "Min voucher quantity should be 1.");
            } else {
                Intent intent;
                if (getIntent().hasExtra("forPurchaseVoucher")) {
                    intent = new Intent(this, VoucherPurchaseConfirmationActivity.class);
                } else {
                    intent = new Intent(this, VoucherConfirmationActivity.class);
                }
                intent.putExtra("service", selectedService);
                intent.putExtra("mobileStatus", offline);
                intent.putExtra("operator", selectedOperator);
                intent.putParcelableArrayListExtra("planList", selectedVoucherPlans);
                startActivityForResult(intent, 200);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        callVoucherPlanListAPI();
        loadVouchersFirestore();
    }

    @Override
    protected void onPause() {
        if (listenerRegistration != null)
            listenerRegistration.remove();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (listenerRegistration != null)
            listenerRegistration.remove();
        super.onDestroy();
    }

    private void setPlanData() {
        rechargePlanList.clear();
        rechargePlanList.addAll(dbHelper.getAllVoucherAmountsOperator(selectedOperator.getId()));
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
            setPlanData();
//            DialogClasses.showDialogInternetAlert(this);
        }
    }

    private void loadVouchersFirestore() {
        showProgressDialog();
        listenerRegistration = DocumentRefrence.vouchersNonPrinted(firebaseFirestore(), userInfo.getId())
                .addSnapshotListener(MetadataChanges.INCLUDE, (querySnapshot, e) -> {
                    if (e != null) {
                        Log.w("vouchers", "Listen error", e);
                        dismissProgressDialog();
                        return;
                    }
                    voucherUnSoldList.clear();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        ModelVoucher voucher = document.toObject(ModelVoucher.class);
                        voucherUnSoldList.add(voucher);
                    }
                    dismissProgressDialog();
                    callVoucherPlanListAPI();
                });
    }

    private void loadUnsoldVouchers(double amount, TextView textView) {
        Database.voucherNonPrints(databaseReference(), "" + userInfo.getId())
                .addValueEventListener(new MapTypedValueEventListener<ModelVoucher>(ModelVoucher.class) {
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                    @Override
                    public void onDataChange(Map data) {

                        ArrayList<ModelVoucher> vouchers = null;
                        if (data != null) {
                            vouchers = new ArrayList<>(data.values());
                        }
                        ArrayList<ModelVoucher> finalList = new ArrayList<>();
                        if (vouchers != null && !vouchers.isEmpty()) {
                            for (ModelVoucher voucher : vouchers) {
                                if (voucher.getIs_print() == 0 && voucher.getVoucher_amount() == amount) {
                                    finalList.add(voucher);
                                }
                            }
                        }

                        if (textView != null)
                            textView.setText(getString(R.string.txt_stock, String.valueOf(finalList.size())));
                    }
                });
    }

    private void loadUnsoldVouchers() {
        Database.voucherNonPrints(databaseReference(), "" + userInfo.getId())
                .addValueEventListener(new MapTypedValueEventListener<ModelVoucher>(ModelVoucher.class) {
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                    @Override
                    public void onDataChange(Map data) {

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
                        voucherUnSoldList.clear();
                        voucherUnSoldList.addAll(finalList);
                    }
                });
    }

    private int getStockUnsoldVoucherQtyAmount(double amount) {
        int qty = 0;
        for (ModelVoucher voucher : voucherUnSoldList) {
            if (voucher.getVoucher_amount() == amount) {
                qty++;
            }
        }
        return qty;
    }

    @Override
    public void onSuccess(ResponseVoucherPlan body) {
       /* rechargePlanList.clear();
        if (body.isRESPONSE()) {
            rechargePlanList.addAll(body.getVoucherPlanList());
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }*/

        if (body.isRESPONSE()) {
            dbHelper.insertOrUpdateVoucherAmountsOperator(body.getVoucherPlanList(), selectedOperator.getId());
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
        setPlanData();
    }

    @Override
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
            int qty = getStockUnsoldVoucherQtyAmount(rechargePlanList.get(position).getAmount());
            if (offline) {
                binding.txtStock.setText(getString(R.string.txt_stock, "---"));
                binding.txtStock.setText(getString(R.string.txt_stock, String.valueOf(qty)));
            } else {
                binding.txtStock.setVisibility(View.GONE);
            }
            binding.txtAmount.setText(String.format("%s %s", rechargePlanList.get(position).getAmount(), getString(R.string.currency_ethiopia_unit)));

//            loadUnsoldVouchers(rechargePlanList.get(position).getAmount(), binding.txtStock);

            int ttlQuantity = rechargePlanList.get(holder.getAdapterPosition()).getSelectedQty();
            if (ttlQuantity > 0) {
                binding.etQuantity.setText(String.valueOf(ttlQuantity));
            } else {
                binding.etQuantity.setText("");
            }
            if(qty==0){
                if (getIntent().hasExtra("forPurchaseVoucher")) {
                    binding.TextInputLayout.setVisibility(View.VISIBLE);
                }else{
                    binding.TextInputLayout.setVisibility(View.GONE);
                }
            }else{
                binding.TextInputLayout.setVisibility(View.VISIBLE);
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
                    }else{
                        rechargePlanList.get(holder.getAdapterPosition()).setSelectedQty(0);
                    }
                    int ttlQuantity = rechargePlanList.get(holder.getAdapterPosition()).getSelectedQty();
                    binding.txtTtlAmount.setText(String.format("%s %s",
                            rechargePlanList.get(position).getAmount() * ttlQuantity,
                            getString(R.string.currency_ethiopia_unit)));

                    setGranTtlAmount();
//                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return rechargePlanList.size();
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

