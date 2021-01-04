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
import com.atlaaya.evdrecharge.apiPresenter.VoucherPlansPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityVoucherListBinding;
import com.atlaaya.evdrecharge.databinding.ItemRechargeAmountBinding;
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
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.MyCustomToast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class VoucherListSingleActivity extends BaseActivity implements
        VoucherPlanListener {

    private ActivityVoucherListBinding binding;

    private ModelService selectedService;
    private ModelOperator selectedOperator;

    private ArrayList<ModelVoucherPlan> rechargePlanList;
    private DBHelper dbHelper;
    private List<ModelVoucher> voucherUnSoldList;

    private VoucherPlansPresenter voucherPlansPresenter;
    private ListenerRegistration listenerRegistration;
    private ModelUserInfo userInfo;
    boolean offline = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voucher_list);
        dbHelper = DBHelper.getInstance(this);
        voucherPlansPresenter = new VoucherPlansPresenter();
        voucherPlansPresenter.setView(this);
        userInfo = SessionManager.getUserDetail(this);
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

        binding.txtTtlAmount.setVisibility(View.GONE);
        binding.btnProceed.setVisibility(View.GONE);

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
        loadVouchersFirestore();
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
    }

    @Override
    public void onSuccess(ResponseVoucherPlan body) {
//        rechargePlanList.clear();
//        if (body.isRESPONSE()) {
//            rechargePlanList.addAll(body.getVoucherPlanList());
//        } else {
//            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
//        }

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


            int ttlQuantity = rechargePlanList.get(holder.getAdapterPosition()).getSelectedQty();
            if (ttlQuantity > 0) {
                binding.etQuantity.setText(String.valueOf(ttlQuantity));
            } else {
                binding.etQuantity.setText("");
            }

            binding.txtTtl.setVisibility(View.GONE);
            binding.txtTtlAmount.setVisibility(View.GONE);
            binding.TextInputLayout.setVisibility(View.GONE);
            binding.btnSelect.setVisibility(View.VISIBLE);
            if (offline) {
                if (qty == 0) {
                    binding.btnSelect.setVisibility(View.GONE);
                } else {
                    binding.btnSelect.setVisibility(View.VISIBLE);
                }
            }
            binding.btnSelect.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, VoucherSelectedSinglePrintActivity.class);
                intent.putExtra("service", selectedService);
                intent.putExtra("operator", selectedOperator);
                intent.putExtra("mobileStatus", offline);
                intent.putExtra("plan", rechargePlanList.get(holder.getAdapterPosition()));
                startActivityForResult(intent, 200);

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

