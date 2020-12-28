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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.activities.BaseActivity;
import com.atlaaya.evdrecharge.activities.LoanPaymentRequestActivity;
import com.atlaaya.evdrecharge.activities.OperatorsActivity;
import com.atlaaya.evdrecharge.apiPresenter.ServicesPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivitySelectServiceBinding;
import com.atlaaya.evdrecharge.databinding.ItemServiceBinding;
import com.atlaaya.evdrecharge.listener.ServiceListener;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ResponseServices;
import com.atlaaya.evdrecharge.sqlite.DBHelper;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.MyCustomToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;


public class ServiceListActivity extends BaseActivity implements ServiceListener {

    private ActivitySelectServiceBinding binding;
    private List<ModelService> serviceList;
    private ServicesPresenter presenter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_service);
        setSupportActionBar(binding.toolbar);
        serviceList = new ArrayList<>();
        dbHelper = DBHelper.getInstance(this);

        presenter = new ServicesPresenter();
        presenter.setView(this);

        if (getIntent().hasExtra("viewStockOnly")) {
            binding.toolbar.setTitle(getString(R.string.menu_voucher_stock));
        }

        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        callServiceAPI();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setServiceData() {
        serviceList.clear();
        serviceList.addAll(dbHelper.getAllServices());
        binding.recyclerView.setAdapter(new ServiceAdapter(this));
    }

    private void callServiceAPI() {
        if (CheckInternetConnection.isInternetConnection(this)) {
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));

            presenter.servicesList(this, map);
        } else {
            setServiceData();
//            DialogClasses.showDialogInternetAlert(this);
        }
    }

    @Override
    public void onSuccess(ResponseServices body) {
       /* serviceList.clear();
        if (body.isRESPONSE()) {
            serviceList.addAll(body.getServiceList());
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }*/
        if (body.isRESPONSE()) {
            dbHelper.insertOrUpdateServices(body.getServiceList());
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
        setServiceData();
    }

    @Override
    public Context getContext() {
        return this;
    }

    public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

        private Context mContext;

        private ServiceAdapter(Context mContext) {
            this.mContext = mContext;
        }


        @NonNull
        @Override
        public ServiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_service, parent, false);
            return new ServiceAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ServiceAdapter.ViewHolder holder, int position) {
            ItemServiceBinding binding = (ItemServiceBinding) holder.getBinding();

            if (serviceList.get(position).getService_name().equalsIgnoreCase("Voucher")) {
                binding.ivLogo.setImageResource(R.drawable.voucher);
                binding.layoutMain.setVisibility(View.VISIBLE);
            } else {
                binding.layoutMain.setVisibility(View.GONE);
            }

            binding.txtName.setText(serviceList.get(position).getService_name());

            binding.layoutMain.setOnClickListener(v -> {

                Intent intent;
                if (serviceList.get(position).getService_name().equalsIgnoreCase("Loan Payment")) {
                    intent = new Intent(getApplicationContext(), LoanPaymentRequestActivity.class);
                }else{
//                intent = new Intent(HomeActivity.this, VoucherListActivity.class);
                    intent = new Intent(getApplicationContext(), OperatorsActivity.class);
                }
//                intent.putExtra("type", AppConstants.KEY_TYPE_VOUCHER);
                intent.putExtra("service", serviceList.get(position));
                if (getIntent().hasExtra("viewStockOnly")) {
                    intent.putExtra("viewStockOnly", true);
                }else{
                    intent.putExtra("forPurchaseVoucher", true);
                }
                startActivityForResult(intent, 200);
            });
        }

        @Override
        public int getItemCount() {
            return serviceList.size();
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

