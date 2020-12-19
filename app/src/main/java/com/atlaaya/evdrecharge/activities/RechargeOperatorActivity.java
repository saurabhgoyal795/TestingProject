package com.atlaaya.evdrecharge.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.apiPresenter.OperatorsPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityRechargeCarrierBinding;
import com.atlaaya.evdrecharge.databinding.ItemServiceBinding;
import com.atlaaya.evdrecharge.enums.EnumPrintAllowed;
import com.atlaaya.evdrecharge.listener.OperatorsListener;
import com.atlaaya.evdrecharge.model.ModelOperator;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ResponseOperators;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.MyCustomToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RechargeOperatorActivity extends BaseActivity implements OperatorsListener {

    private ActivityRechargeCarrierBinding binding;

    private List<ModelOperator> operatorList;
    private OperatorsPresenter presenter;

    private ModelService selectedService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recharge_carrier);

        operatorList = new ArrayList<>();

        presenter = new OperatorsPresenter();
        presenter.setView(this);

        if (getIntent().hasExtra("service")) {
            selectedService = getIntent().getParcelableExtra("service");
        }
        setSupportActionBar(binding.toolbar);

        if (selectedService != null) {
            binding.toolbar.setTitle(selectedService.getService_name());
        } else {
            binding.toolbar.setTitle(getString(R.string.txt_voucher_recharge));
        }

        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

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
        callOperatorAPI();
    }


    private void callOperatorAPI() {
        if (CheckInternetConnection.isInternetConnection(this)) {
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
            map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_EMAIL)));
            map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_PASSWORD)));
            map.put("service_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + selectedService.getId()));

            presenter.operatorsList(this, map);
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    @Override
    public void onSuccess(ResponseOperators body) {
        operatorList.clear();

        if (body.isRESPONSE()) {
            operatorList.addAll(body.getOperatorList());
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }

        setServiceData();
    }

    @Override
    public Context getContext() {
        return this;
    }

    private void setServiceData() {
        binding.recyclerView.setAdapter(new ServiceAdapter(this));
    }


    public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

        private Context mContext;

        private ServiceAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_service, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            ItemServiceBinding binding = (ItemServiceBinding) holder.getBinding();

            binding.ivLogo.setImageResource(R.drawable.ethio_telecom);

            binding.txtName.setText(operatorList.get(position).getTitle());

            binding.layoutMain.setOnClickListener(v -> {
                ModelUserInfo userInfo = SessionManager.getUserDetail(mContext);
                if (userInfo != null) {
                    Intent intent;
                    if (selectedService.getService_name().equals("Topup")) {
                        intent = new Intent(RechargeOperatorActivity.this, TopUpOptionActivity.class);
                    } else if(selectedService.getService_name().equals("Electricity bill")){
                        intent = new Intent(RechargeOperatorActivity.this, ElectricityType.class);
                    }else if(selectedService.getService_name().equals("Voucher")){
                        intent = new Intent(RechargeOperatorActivity.this, PrintTypeActivity.class);
                    } else{
                        if(userInfo.getPrint_allowed().equals(EnumPrintAllowed.both)){
                            intent = new Intent(RechargeOperatorActivity.this, PrintTypeActivity.class);
                        } else if(userInfo.getPrint_allowed().equals(EnumPrintAllowed.bulk)){
                            intent = new Intent(RechargeOperatorActivity.this, VoucherListActivity.class);
                        } else{
                            intent = new Intent(RechargeOperatorActivity.this, VoucherListSingleActivity.class);
                        }
                    }
                    intent.putExtra("operator", operatorList.get(position));
                    intent.putExtra("service", selectedService);
                    startActivityForResult(intent, 200);
                }else{
                    MyCustomToast.showToast(mContext, "Something wrong. Please login again");
                    UserLogoutDeleteAccount();
                }
            });
        }

        @Override
        public int getItemCount() {
            return operatorList.size();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200 && resultCode== Activity.RESULT_OK){
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
