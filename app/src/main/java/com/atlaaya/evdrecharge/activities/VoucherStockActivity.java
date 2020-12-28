package com.atlaaya.evdrecharge.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.apiPresenter.VoucherPlansPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityVoucherStockBinding;
import com.atlaaya.evdrecharge.databinding.ItemVoucherStockBinding;
import com.atlaaya.evdrecharge.firebase.database.MapTypedValueEventListener;
import com.atlaaya.evdrecharge.firebase.references.Database;
import com.atlaaya.evdrecharge.firebase.references.DocumentRefrence;
import com.atlaaya.evdrecharge.listener.VoucherPlanListener;
import com.atlaaya.evdrecharge.model.ModelOperator;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ModelVoucher;
import com.atlaaya.evdrecharge.model.ModelVoucherPlan;
import com.atlaaya.evdrecharge.model.ResponseVoucherPlan;
import com.atlaaya.evdrecharge.sqlite.DBHelper;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.MyCustomToast;
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

public class VoucherStockActivity extends BaseActivity implements VoucherPlanListener {

    private ActivityVoucherStockBinding binding;

    private ModelService selectedService;
    private ModelOperator selectedOperator;
    private ModelUserInfo userInfo;

    private List<ModelVoucher> voucherUnSoldList;
    private ArrayList<ModelVoucherPlan> rechargePlanList;

    private VoucherPlansPresenter voucherPlansPresenter;

    private DBHelper dbHelper;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voucher_stock);
        dbHelper = DBHelper.getInstance(this);
        userInfo = SessionManager.getUserDetail(this);

        voucherPlansPresenter = new VoucherPlansPresenter();
        voucherPlansPresenter.setView(this);

        voucherUnSoldList = new ArrayList<>();
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
            binding.toolbar.setTitle(getString(R.string.menu_voucher_stock));
        }

        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

//        loadUnsoldVouchers();
        loadVouchersFirestore();
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
    protected void onStart() {
        super.onStart();
//        callVoucherPlanListAPI();
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
                    R.layout.item_voucher_stock, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            ItemVoucherStockBinding binding = (ItemVoucherStockBinding) holder.getBinding();

            binding.txtAmount.setText(String.format("%s %s", rechargePlanList.get(position).getAmount(), getString(R.string.currency_ethiopia_unit)));
            binding.txtStock.setText(getString(R.string.txt_stock, "---"));
            binding.txtStock.setText(getString(R.string.txt_stock, String.valueOf(getStockUnsoldVoucherQtyAmount(rechargePlanList.get(position).getAmount()))));
//            loadUnsoldVouchers(rechargePlanList.get(position).getAmount(), binding.txtStock);

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

