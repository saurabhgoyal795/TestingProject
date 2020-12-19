package com.atlaaya.evdrecharge.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.apiPresenter.BankAccountListPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.FragmentBankAccountListBinding;
import com.atlaaya.evdrecharge.databinding.ItemBankAccountBinding;
import com.atlaaya.evdrecharge.listener.BankAccountListener;
import com.atlaaya.evdrecharge.model.ModelBankAccount;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ResponseBankAccount;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;


public class FragmentCompanyBankAccounts extends BaseFragment implements BankAccountListener {

    private FragmentBankAccountListBinding binding;
    private BankAccountListPresenter presenter;
    private BankAccountListAdapter adapter;

    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        presenter = new BankAccountListPresenter();
        presenter.setView(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bank_account_list, container, false);

        binding.txtNoRecord.setText("");
        binding.btnAddNewAccount.setVisibility(View.GONE);

        adapter = new BankAccountListAdapter(mActivity);
        binding.recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        callListAPI();
    }

    @Override
    public void onSuccess(ResponseBankAccount body) {
        adapter.setList(body.getBankAccountList());
        if (body.getBankAccountList().isEmpty()) {
            binding.txtNoRecord.setText(body.getRESPONSE_MSG());
        } else {
            binding.txtNoRecord.setText("");
        }
    }


    private void callListAPI() {
        if (CheckInternetConnection.isInternetConnection(mActivity)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(mActivity);
            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(mActivity, SessionManager.KEY_EMAIL)));
                map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(mActivity, SessionManager.KEY_PASSWORD)));
                map.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "1"));

                presenter.bankAccountCompany(mActivity, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(mActivity);
        }
    }

    public class BankAccountListAdapter extends RecyclerView.Adapter<BankAccountListAdapter.ViewHolder> {

        private Context mContext;

        private List<ModelBankAccount> bankAccountList;

        private BankAccountListAdapter(Context mContext) {
            this.mContext = mContext;
            bankAccountList = new ArrayList<>();
        }

        public void setList(List<ModelBankAccount> bankAccountList) {
            this.bankAccountList = bankAccountList == null ? new ArrayList<>() : bankAccountList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public BankAccountListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_bank_account, parent, false);
            return new BankAccountListAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final BankAccountListAdapter.ViewHolder holder, int position) {
            ItemBankAccountBinding binding = (ItemBankAccountBinding) holder.getBinding();

            binding.ivEdit.setVisibility(View.GONE);

            ModelBankAccount bankAccountInfo = bankAccountList.get(position);

            binding.txtAccountName.setText(Html.fromHtml(getString(R.string.text_bank_account_name, bankAccountInfo.getAccountName())));
            binding.txtAccountNumber.setText(Html.fromHtml(getString(R.string.text_bank_account_number, bankAccountInfo.getAccountNumber())));
            binding.txtBankName.setText(Html.fromHtml(getString(R.string.text_bank_name, bankAccountInfo.getBank().getBankName())));
            binding.txtBranchName.setText(Html.fromHtml(getString(R.string.text_bank_branch, bankAccountInfo.getBranchName())));
            binding.txtIFSCCode.setText(Html.fromHtml(getString(R.string.text_bank_ifsc, bankAccountInfo.getIfsc())));

            if (bankAccountInfo.isAdminVerifyStatus()) {
                binding.ivStatus.setImageResource(R.drawable.ic_check_circle);
            } else {
                binding.ivStatus.setImageResource(R.drawable.ic_info);
            }
        }

        @Override
        public int getItemCount() {
            return bankAccountList.size();
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
