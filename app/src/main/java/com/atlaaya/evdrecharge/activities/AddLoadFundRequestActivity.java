package com.atlaaya.evdrecharge.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.apiPresenter.AddFundRequestPresenter;
import com.atlaaya.evdrecharge.apiPresenter.BankAccountListPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityAddLoadFundRequestBinding;
import com.atlaaya.evdrecharge.listener.AddBankAccountListener;
import com.atlaaya.evdrecharge.listener.BankAccountListener;
import com.atlaaya.evdrecharge.model.ModelBankAccount;
import com.atlaaya.evdrecharge.model.ModelFundRequest;
import com.atlaaya.evdrecharge.model.ResponseBankAccount;
import com.atlaaya.evdrecharge.model.ResponseDefault;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.DisableEmojiInEditText;
import com.atlaaya.evdrecharge.utils.MyCustomToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AddLoadFundRequestActivity extends BaseActivity implements View.OnClickListener, AddBankAccountListener, BankAccountListener {

    private ActivityAddLoadFundRequestBinding binding;

    private AddFundRequestPresenter presenter;
    private BankAccountListPresenter bankListPresenter;

    private List<ModelBankAccount> bankList;

    private ModelFundRequest fundRequest;

    private int user_bank_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_load_fund_request);
        setSupportActionBar(binding.toolbar);

        bankList = new ArrayList<>();

        if (getIntent().hasExtra("fundRequest")) {
            fundRequest = getIntent().getParcelableExtra("fundRequest");
            if (fundRequest != null) {
                binding.etAmount.setText(String.valueOf(fundRequest.getAmount()));

                String bankDetail = "Bank : " + fundRequest.getUserBankAccount().getBank().getBankName()
                        + "\n" + "Branch : " + fundRequest.getUserBankAccount().getBranchName()
                        + "\n" + "Swift Code : " + fundRequest.getUserBankAccount().getIfsc();
                binding.etBankName.setText(bankDetail);
                binding.etTransactionDetail.setText(fundRequest.getTransactionNumber());
                binding.etRemark.setText(fundRequest.getUserRemark());

                binding.toolbar.setTitle(getString(R.string.title_update_fund_requests));

                binding.btnSubmit.setText(getString(R.string.btn_update_load_request));

                user_bank_id = fundRequest.getUserBankerId();
            }
        }

        bankListPresenter = new BankAccountListPresenter();
        bankListPresenter.setView(this);

        presenter = new AddFundRequestPresenter();
        presenter.setView(this);

        binding.etAmount.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etBankName.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etTransactionDetail.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etRemark.setFilters(new InputFilter[]{new DisableEmojiInEditText()});

        callUserBankListAPI();

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
        switch (v.getId()) {
            case R.id.btnSubmit:
                validateAccountDetail();
                break;
            case R.id.etBankName:
                if (bankList.isEmpty()) {
                    callUserBankListAPI();
                } else {
                    dialogBankList();
                }
                break;
        }
    }

    private void dialogBankList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.hint_select_bank));

        String[] banks = new String[bankList.size()];
        for (int i = 0; i < bankList.size(); i++) {

            String bankDetail = "Bank : " + bankList.get(i).getBank().getBankName()
                    + "\n" + "Branch : " + bankList.get(i).getBranchName()
                    + "\n" + "Swift Code : " + bankList.get(i).getIfsc()
                    +"\n\n";

            banks[i] = bankDetail;
        }

        builder.setItems(banks, (dialog, which) -> {
            ModelBankAccount bankAccount = bankList.get(which);
            user_bank_id = bankAccount.getId();
            String bankDetail = "Bank : " + bankAccount.getBank().getBankName()
                    + "\n" + "Branch : " + bankAccount.getBranchName()
                    + "\n" + "Swift Code : " + bankAccount.getIfsc();

            binding.etBankName.setText(bankDetail);
            binding.etBankName.setError(null);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void validateAccountDetail() {
        String amount = binding.etAmount.getText().toString().trim();
        String bankName = binding.etBankName.getText().toString().trim();
        String transactionDetail = binding.etTransactionDetail.getText().toString().trim();
        String remark = binding.etRemark.getText().toString().trim();

        if (amount.isEmpty() || Integer.parseInt(amount) == 0) {
            binding.etAmount.requestFocus();
            binding.etAmount.setError(getText(R.string.alert_enter_load_request_amount));
        } else if (bankName.isEmpty()) {
            binding.etBankName.requestFocus();
            binding.etBankName.setError(getText(R.string.alert_select_bank_name));
            MyCustomToast.showErrorAlert(this, getString(R.string.alert_select_bank_name));
        } else if (transactionDetail.isEmpty()) {
            binding.etTransactionDetail.requestFocus();
            binding.etTransactionDetail.setError(getText(R.string.alert_enter_transaction_number));
        } else if (remark.isEmpty()) {
            binding.etRemark.requestFocus();
            binding.etRemark.setError(getText(R.string.alert_enter_remark));
        } else if (CheckInternetConnection.isInternetConnection(this)) {
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
            map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_EMAIL)));
            map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_PASSWORD)));
            map.put("amount", RequestBody.create(MediaType.parse("multipart/form-data"), amount));
            map.put("user_banker_id", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(user_bank_id)));
            map.put("transaction_number", RequestBody.create(MediaType.parse("multipart/form-data"), transactionDetail));
            map.put("user_remark", RequestBody.create(MediaType.parse("multipart/form-data"), remark));

            if (fundRequest != null) {
                map.put("ubid", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(fundRequest.getId())));
            }

            presenter.loadFundRequestAddEdit(this, map);
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    private void callUserBankListAPI() {
        if (CheckInternetConnection.isInternetConnection(this)) {
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
            map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_EMAIL)));
            map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_PASSWORD)));
            map.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "1"));

            bankListPresenter.bankAccountUser(this, map);
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }


    @Override
    public void onSuccess(ResponseDefault body) {
        if (body.isRESPONSE()) {
            MyCustomToast.showToast(this, body.getRESPONSE_MSG());
            finish();
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onSuccess(ResponseBankAccount body) {
        bankList.clear();
        if (body.isRESPONSE()) {
            bankList.addAll(body.getBankAccountList());
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
    }
}

