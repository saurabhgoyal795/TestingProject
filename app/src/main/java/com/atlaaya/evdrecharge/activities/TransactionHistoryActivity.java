package com.atlaaya.evdrecharge.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.apiPresenter.TransactionPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityTransactionHistoryBinding;
import com.atlaaya.evdrecharge.databinding.ItemTransactionHistoryBinding;
import com.atlaaya.evdrecharge.listener.TransactionListener;
import com.atlaaya.evdrecharge.model.ModelFilterConfig;
import com.atlaaya.evdrecharge.model.ModelTransaction;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ResponseTransactionHistory;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.PriceFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TransactionHistoryActivity extends BaseActivity implements TransactionListener {

    private ActivityTransactionHistoryBinding binding;

    private TransactionListAdapter adapter;
    private TransactionPresenter presenter;

    private ModelFilterConfig filterConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_history);

        filterConfig = SessionManager.getFilterConfig(this);

        presenter = new TransactionPresenter();
        presenter.setView(this);

        setSupportActionBar(binding.toolbar);
        binding.txtNoRecord.setText("");

        adapter = new TransactionListAdapter(this);
        binding.recyclerView.setAdapter(adapter);

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
        callListAPI();
    }

    @Override
    public void onTransactionListSuccess(ResponseTransactionHistory body) {
        if(adapter != null){
            adapter.setList(body.getTransactionList());
            if (body.getTransactionList().isEmpty()) {
                binding.txtNoRecord.setText(body.getRESPONSE_MSG());
            } else {
                binding.txtNoRecord.setText("");
            }
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    private void callListAPI() {
        if (CheckInternetConnection.isInternetConnection(this)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(this);
            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_EMAIL)));
                map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_PASSWORD)));

                presenter.retailTransactions(this, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.ViewHolder> {

        private Context mContext;

        private List<ModelTransaction> transactionList;

        private TransactionListAdapter(Context mContext) {
            this.mContext = mContext;
            transactionList = new ArrayList<>();
        }

        public void setList(List<ModelTransaction> transactionList) {
            this.transactionList = transactionList == null ? new ArrayList<>() : transactionList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_transaction_history, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            ItemTransactionHistoryBinding binding = (ItemTransactionHistoryBinding) holder.getBinding();

            ModelTransaction transactionInfo = transactionList.get(position);

            binding.txtTransacRemark.setText(transactionInfo.getRemark());
            binding.txtOperator.setText(transactionInfo.getOperator().getTitle());
            binding.txtDate.setText(transactionInfo.getCreated());
            binding.txtAmount.setText(getString(R.string.balance, PriceFormat.decimalTwoDigit1(transactionInfo.getTransactionAmount())));

            String status = "";
            String mainType = "";

            if (filterConfig != null && filterConfig.getFilters() != null) {
                try {
                    if (!filterConfig.getFilters().getTransactionStatus().isEmpty())
                        status = filterConfig.getFilters().getTransactionStatus().get(transactionInfo.getTransactionStatus());

                    if (!filterConfig.getFilters().getTransactionMaintype().isEmpty())
                        mainType = filterConfig.getFilters().getTransactionMaintype().get(transactionInfo.getMaintype());
                }catch (Exception e) {

                }
            }

            switch (transactionInfo.getTransactionStatus()) {
                case 0:
                    status = status.isEmpty() ? getString(R.string.txt_status_pending) : status;
                    binding.txtStatus.setTextColor(ContextCompat.getColor(mContext, R.color.orange));
                    break;
                case 1:
                    status = status.isEmpty() ? getString(R.string.txt_status_success) : status;
                    binding.txtStatus.setTextColor(ContextCompat.getColor(mContext, R.color.green_success));
                    break;
                default:
                    status = status.isEmpty() ? getString(R.string.txt_status_failed) : status;
                    binding.txtStatus.setTextColor(ContextCompat.getColor(mContext, R.color.red_fail));
                    break;
            }
            binding.txtStatus.setText(status);


            switch (transactionInfo.getMaintype()) {
                case 1:
                    mainType = mainType.isEmpty() ? getString(R.string.txt_transac_main_type_AdminLoadTransfer) : mainType;
                    break;
                case 2:
                    mainType = mainType.isEmpty() ? getString(R.string.txt_transac_main_type_AdminLoadDeduct) : mainType;
                    break;
                case 3:
                    mainType = mainType.isEmpty() ? getString(R.string.txt_transac_main_type_TopupSale) : mainType;
                    break;
                case 4:
                    mainType = mainType.isEmpty() ? getString(R.string.txt_transac_main_type_VoucherSale) : mainType;
                    break;
                case 5:
                    mainType = mainType.isEmpty() ? getString(R.string.txt_transac_main_type_LoadTransfer) : mainType;
                    mainType = mainType + " : " + transactionInfo.getTopupSmsNumber();
                    break;
                default:
                    mainType = mainType.isEmpty() ? getString(R.string.txt_transac_main_type_none) : mainType;
                    break;
            }
            binding.txtTransacMainType.setText(mainType);

            switch (transactionInfo.getType()) {
                case 1:
                    binding.txtTransacType.setText(getString(R.string.txt_credit_plus));
                    binding.txtTransacType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_green_1));
                    break;
                case 2:
                    binding.txtTransacType.setText(getString(R.string.txt_debit_minus));
                    binding.txtTransacType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_red_1));
                    break;
                default:
                    binding.txtTransacType.setText(getString(R.string.txt_transac_main_type_none));
                    binding.txtTransacType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_yellow_1));
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return transactionList.size();
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

