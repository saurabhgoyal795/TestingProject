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
import com.atlaaya.evdrecharge.apiPresenter.StatementsPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityTransactionHistoryBinding;
import com.atlaaya.evdrecharge.databinding.ItemStatementBinding;
import com.atlaaya.evdrecharge.listener.StatementsListener;
import com.atlaaya.evdrecharge.model.ModelStatement;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ResponseStatements;
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

public class MyStatementsActivity extends BaseActivity implements StatementsListener {

    private ActivityTransactionHistoryBinding binding;

    private TransactionListAdapter adapter;
    private StatementsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_history);

        presenter = new StatementsPresenter();
        presenter.setView(this);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(getString(R.string.menu_my_statements));
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
    public void onStatementListSuccess(ResponseStatements body) {
        adapter.setList(body.getStatementList());
        if (body.getStatementList().isEmpty()) {
            binding.txtNoRecord.setText(body.getRESPONSE_MSG());
        } else {
            binding.txtNoRecord.setText("");
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

                presenter.retailStatements(this, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.ViewHolder> {

        private Context mContext;

        private List<ModelStatement> statementList;

        private TransactionListAdapter(Context mContext) {
            this.mContext = mContext;
            statementList = new ArrayList<>();
        }

        public void setList(List<ModelStatement> statementList) {
            this.statementList = statementList == null ? new ArrayList<>() : statementList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_statement, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            ItemStatementBinding binding = (ItemStatementBinding) holder.getBinding();

            ModelStatement statementInfo = statementList.get(position);

            binding.txtOpeningBalance.setText(String.format("%s %s", getString(R.string.txt_opening_balance,
                    PriceFormat.decimalTwoDigit1(statementInfo.getOpening())), getString(R.string.currency_ethiopia_unit)));
            binding.txtClosingBalance.setText(String.format("%s %s", getString(R.string.txt_closing_balance,
                    PriceFormat.decimalTwoDigit1(statementInfo.getClossing())), getString(R.string.currency_ethiopia_unit)));
            binding.txtTransactionId.setText(getString(R.string.txt_transaction_id, String.valueOf(statementInfo.getTransactionId())));
            binding.txtRemark.setText(statementInfo.getApiResponse());
            binding.txtDate.setText(statementInfo.getCreated());
            binding.txtAmount.setText(getString(R.string.balance, PriceFormat.decimalTwoDigit1(statementInfo.getTransactionAmount())));

            binding.txtStatus.setText("");
            binding.txtStatus.setVisibility(ViewGroup.GONE);

            switch (statementInfo.getType()) {
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
            return statementList.size();
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

