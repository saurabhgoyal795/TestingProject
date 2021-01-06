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
import com.atlaaya.evdrecharge.databinding.ItemService2Binding;
import com.atlaaya.evdrecharge.databinding.ItemServiceBinding;
import com.atlaaya.evdrecharge.enums.EnumPrintAllowed;
import com.atlaaya.evdrecharge.listener.OperatorsListener;
import com.atlaaya.evdrecharge.model.ModelOperator;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ResponseOperators;
import com.atlaaya.evdrecharge.sqlite.DBHelper;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.LanguageUtil;
import com.atlaaya.evdrecharge.utils.MyCustomToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class OperatorsActivity extends BaseActivity implements OperatorsListener {

    private ActivityRechargeCarrierBinding binding;

    private List<ModelOperator> operatorList;
    private OperatorsPresenter presenter;

    private ModelService selectedService;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recharge_carrier);
        dbHelper = DBHelper.getInstance(this);
        operatorList = new ArrayList<>();

        presenter = new OperatorsPresenter();
        presenter.setView(this);

        if (getIntent().hasExtra("service")) {
            selectedService = getIntent().getParcelableExtra("service");
        }
        setSupportActionBar(binding.toolbar);
        LanguageUtil.setTextViewTextByLanguage(getContext(),binding.text1, "txt_select_operator");
        if (selectedService != null) {
            if (getIntent().hasExtra("forPurchaseVoucher")) {
              //  binding.toolbar.setTitle(getString(R.string.title_purchase_voucher));
                LanguageUtil.setToolBarTextByLanguage(getContext(),binding.toolbar, "txt_voucher_recharge");
            }else  if (getIntent().hasExtra("viewStockOnly")) {
                binding.toolbar.setTitle(getString(R.string.menu_voucher_stock));
            }else {
                binding.toolbar.setTitle(selectedService.getService_name());
            }
        } else {
            LanguageUtil.setToolBarTextByLanguage(getContext(),binding.toolbar, "txt_voucher_recharge");
         //   binding.toolbar.setTitle(getString(R.string.txt_voucher_recharge));
        }

        binding.recyclerView2.setLayoutManager(new GridLayoutManager(this, 1));

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
            setServiceData();
//            DialogClasses.showDialogInternetAlert(this);
        }
    }

    @Override
    public void onSuccess(ResponseOperators body) {
       /* operatorList.clear();
        if (body.isRESPONSE()) {
            operatorList.addAll(body.getOperatorList());
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }*/
        if (body.isRESPONSE()) {
            dbHelper.insertOrUpdateOperatorsOfService(body.getOperatorList(), selectedService.getId());
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
        operatorList.clear();
        operatorList.addAll(dbHelper.getAllOperatorsOfService(selectedService.getId()));
        binding.recyclerView2.setAdapter(new ServiceAdapter(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
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
                    R.layout.item_service2, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            ItemService2Binding binding = (ItemService2Binding) holder.getBinding();

            binding.ivLogo2.setImageResource(R.drawable.voucher_4);

            binding.txtName2.setText(operatorList.get(position).getTitle());

            binding.layoutMain2.setOnClickListener(v -> {
                ModelUserInfo userInfo = SessionManager.getUserDetail(mContext);
                if (userInfo != null) {
                    Intent intent;
                    if (selectedService.getService_name().equals("Topup")) {
                        intent = new Intent(OperatorsActivity.this, TopUpOptionActivity.class);
                    } else {
                        if (getIntent().hasExtra("forPurchaseVoucher")) {
                            intent = new Intent(OperatorsActivity.this, VoucherListActivity.class);
                            intent.putExtra("forPurchaseVoucher", true);
                        } else  if (getIntent().hasExtra("viewStockOnly")) {
                            intent = new Intent(OperatorsActivity.this, VoucherStockActivity.class);
                        } else {
                            if (userInfo.getPrint_allowed().equals(EnumPrintAllowed.both)) {
                                intent = new Intent(OperatorsActivity.this, PrintTypeActivity.class);
                            } else if (userInfo.getPrint_allowed().equals(EnumPrintAllowed.bulk)) {
                                intent = new Intent(OperatorsActivity.this, VoucherListActivity.class);
                            } else {
                                intent = new Intent(OperatorsActivity.this, VoucherListSingleActivity.class);
                            }
                        }
                    }
                    intent.putExtra("operator", operatorList.get(position));
                    intent.putExtra("service", selectedService);
                    startActivityForResult(intent, 200);
                } else {
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
}
