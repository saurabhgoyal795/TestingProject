package com.atlaaya.evdrecharge.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.apiPresenter.FundRequestsPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityWalletBinding;
import com.atlaaya.evdrecharge.databinding.ItemFundRequestBinding;
import com.atlaaya.evdrecharge.listener.FundRequestsListener;
import com.atlaaya.evdrecharge.model.ModelBalance;
import com.atlaaya.evdrecharge.model.ModelBankAccount;
import com.atlaaya.evdrecharge.model.ModelFilterConfig;
import com.atlaaya.evdrecharge.model.ModelFundRequest;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ResponseFundRequests;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.LanguageUtil;
import com.atlaaya.evdrecharge.utils.MyCustomToast;
import com.atlaaya.evdrecharge.utils.PriceFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class WalletActivity extends BaseActivity implements View.OnClickListener, FundRequestsListener {

    private ActivityWalletBinding binding;

    private FundRequestsPresenter presenter;
    private FundRequestListAdapter adapter;

    private ModelFilterConfig filterConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet);
        LanguageUtil.setTextViewTextByLanguage(getContext(),binding.txtAvlBalance, "txt_avl_balance");
        LanguageUtil.setTextViewTextByLanguage(getContext(),binding.txtMyLoadRequest, "title_load_fund_requests");
        LanguageUtil.setButtonTextByLanguage(getContext(),binding.btnAddNewRequest, "btn_make_load_request");
        filterConfig = SessionManager.getFilterConfig(this);

        presenter = new FundRequestsPresenter();
        presenter.setView(this);

        setSupportActionBar(binding.toolbar);
        binding.txtNoRecord.setText("");

        setBalanceInfo();

        adapter = new FundRequestListAdapter(this);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setBalanceInfo() {
        String balance = "0.00";
        ModelBalance modelBalance = SessionManager.getBalance(this);
        if (modelBalance != null) {
            balance = PriceFormat.decimalTwoDigit1(modelBalance.getEffective_balance());
        }
        binding.txtAvlBalanceAmount.setText(getString(R.string.balance, balance));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddNewRequest:
                Intent intent = new Intent(this, AddLoadFundRequestActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        callListAPI();
    }

    @Override
    public void onSuccess(ResponseFundRequests body) {
        adapter.setList(body.getFundRequestList());
        if (body.getFundRequestList().isEmpty()) {
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

                presenter.loadFundRequests(this, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    public class FundRequestListAdapter extends RecyclerView.Adapter<FundRequestListAdapter.ViewHolder> {

        private Context mContext;

        private List<ModelFundRequest> fundRequestList;

        private FundRequestListAdapter(Context mContext) {
            this.mContext = mContext;
            fundRequestList = new ArrayList<>();
        }

        public void setList(List<ModelFundRequest> fundRequestList) {
            this.fundRequestList = fundRequestList == null ? new ArrayList<>() : fundRequestList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_fund_request, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            ItemFundRequestBinding binding = (ItemFundRequestBinding) holder.getBinding();

            ModelFundRequest fundRequestInfo = fundRequestList.get(position);
            ModelBankAccount bankAccountInfo = fundRequestInfo.getUserBankAccount();

            binding.txtRequestAmount.setText(Html.fromHtml(getString(R.string.text_load_request_amount, "" + fundRequestInfo.getAmount())));
            binding.txtTranscNumber.setText(Html.fromHtml(getString(R.string.text_load_transac_number, fundRequestInfo.getTransactionNumber())));
            binding.txtUserRemark.setText(Html.fromHtml(getString(R.string.text_load_fund_user_remark, fundRequestInfo.getUserRemark())));
            binding.txtAdminRemark.setText(Html.fromHtml(getString(R.string.text_load_fund_admin_remark, fundRequestInfo.getAdminRemark())));

            binding.txtAccountName.setText(Html.fromHtml(getString(R.string.text_bank_account_name, bankAccountInfo.getAccountName())));
            binding.txtAccountNumber.setText(Html.fromHtml(getString(R.string.text_bank_account_number, bankAccountInfo.getAccountNumber())));
            binding.txtBankName.setText(Html.fromHtml(getString(R.string.text_bank_name, bankAccountInfo.getBank().getBankName())));
            binding.txtBranchName.setText(Html.fromHtml(getString(R.string.text_bank_branch, bankAccountInfo.getBranchName())));
            binding.txtIFSCCode.setText(Html.fromHtml(getString(R.string.text_bank_ifsc, bankAccountInfo.getIfsc())));


            binding.ivEdit.setVisibility(View.GONE);

            switch (fundRequestInfo.getStatus()) {
                case 1:
                    binding.txtStatus.setText(filterConfig.getFilters().getLoadRequestStatus().get$1());
                    binding.txtStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_yellow));
                    binding.ivEdit.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    binding.txtStatus.setText(filterConfig.getFilters().getLoadRequestStatus().get$2());
                    binding.txtStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_green));
                    break;
                case 3:
                    binding.txtStatus.setText(filterConfig.getFilters().getLoadRequestStatus().get$3());
                    binding.txtStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_red));
                    break;
                case 4:
                    binding.txtStatus.setText(filterConfig.getFilters().getLoadRequestStatus().get$4());
                    binding.txtStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_red));
                    break;
            }

         /*   if (fundRequestInfo.getStatus() == 1) {
                binding.ivStatus.setImageResource(R.drawable.ic_check_circle);
                binding.ivEdit.setVisibility(View.GONE);
            } else {
                binding.ivStatus.setImageResource(R.drawable.ic_info);
                binding.ivEdit.setVisibility(View.VISIBLE);
            }*/

            binding.ivEdit.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, AddLoadFundRequestActivity.class);
                intent.putExtra("fundRequest", fundRequestInfo);
                startActivity(intent);
            });


        }

        @Override
        public int getItemCount() {
            return fundRequestList.size();
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

