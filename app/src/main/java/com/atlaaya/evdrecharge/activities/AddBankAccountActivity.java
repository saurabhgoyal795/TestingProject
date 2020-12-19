package com.atlaaya.evdrecharge.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.apiPresenter.AddBankAccountPresenter;
import com.atlaaya.evdrecharge.apiPresenter.BankListPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityAddBankAccountBinding;
import com.atlaaya.evdrecharge.listener.AddBankAccountListener;
import com.atlaaya.evdrecharge.listener.BankListener;
import com.atlaaya.evdrecharge.model.ModelBank;
import com.atlaaya.evdrecharge.model.ModelBankAccount;
import com.atlaaya.evdrecharge.model.ResponseBank;
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

public class AddBankAccountActivity extends BaseActivity implements View.OnClickListener, AddBankAccountListener, BankListener {

    private ActivityAddBankAccountBinding binding;

    private AddBankAccountPresenter presenter;
    private BankListPresenter bankListPresenter;

    private List<ModelBank> bankList;

    private ModelBankAccount bankAccountInfo;

    private int bank_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_bank_account);
        setSupportActionBar(binding.toolbar);

        bankList = new ArrayList<>();

        if (getIntent().hasExtra("bankAccount")) {
            bankAccountInfo = getIntent().getParcelableExtra("bankAccount");
            if (bankAccountInfo != null) {
                binding.etAccountName.setText(bankAccountInfo.getAccountName());
                binding.etAccountNumber.setText(bankAccountInfo.getAccountNumber());
                binding.etBankName.setText(bankAccountInfo.getBank().getBankName());
                binding.etBranchName.setText(bankAccountInfo.getBranchName());
                binding.etSwiftCode.setText(bankAccountInfo.getIfsc());

                binding.toolbar.setTitle(getString(R.string.title_edit_bank_account));

                binding.btnSubmit.setText(getString(R.string.btn_update));

                bank_id = bankAccountInfo.getBankId();
            }
        }

        bankListPresenter = new BankListPresenter();
        bankListPresenter.setView(this);

        presenter = new AddBankAccountPresenter();
        presenter.setView(this);

        binding.etAccountName.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etAccountNumber.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etBankName.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etBranchName.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etSwiftCode.setFilters(new InputFilter[]{new DisableEmojiInEditText()});

        callBankListAPI();

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
                    callBankListAPI();
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
            banks[i] = bankList.get(i).getBankName();
        }

        builder.setItems(banks, (dialog, which) -> {
            ModelBank bank = bankList.get(which);
            bank_id = bank.getId();
            binding.etBankName.setText(bank.getBankName());
            binding.etBankName.setError(null);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void validateAccountDetail() {
        String accountName = binding.etAccountName.getText().toString().trim();
        String accountNumber = binding.etAccountNumber.getText().toString().trim();
        String bankName = binding.etBankName.getText().toString().trim();
        String branchName = binding.etBranchName.getText().toString().trim();
        String swiftCode = binding.etSwiftCode.getText().toString().trim();

        if (accountName.isEmpty()) {
            binding.etAccountName.requestFocus();
            binding.etAccountName.setError(getText(R.string.alert_enter_bank_account_name));
        } else if (accountNumber.isEmpty()) {
            binding.etAccountNumber.requestFocus();
            binding.etAccountNumber.setError(getText(R.string.alert_enter_bank_account_number));
        } else if (bankName.isEmpty()) {
            binding.etBankName.requestFocus();
            binding.etBankName.setError(getText(R.string.alert_select_bank_name));
            MyCustomToast.showErrorAlert(this, getString(R.string.alert_select_bank_name));
        } else if (branchName.isEmpty()) {
            binding.etBranchName.requestFocus();
            binding.etBranchName.setError(getText(R.string.alert_enter_bank_branch_name));
        } else if (swiftCode.isEmpty()) {
            binding.etSwiftCode.requestFocus();
            binding.etSwiftCode.setError(getText(R.string.alert_enter_bank_branch_swift));
        } else if (CheckInternetConnection.isInternetConnection(this)) {
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
            map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_EMAIL)));
            map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_PASSWORD)));
            map.put("account_name", RequestBody.create(MediaType.parse("multipart/form-data"), accountName));
            map.put("account_number", RequestBody.create(MediaType.parse("multipart/form-data"), accountNumber));
            map.put("bank_id", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(bank_id)));
            map.put("branch_name", RequestBody.create(MediaType.parse("multipart/form-data"), branchName));
            map.put("ifsc", RequestBody.create(MediaType.parse("multipart/form-data"), swiftCode));

            if (bankAccountInfo != null) {
                map.put("ubid", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(bankAccountInfo.getId())));
            }

            presenter.bankAccountAddEdit(this, map);
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    private void callBankListAPI() {
        if (CheckInternetConnection.isInternetConnection(this)) {
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));

            bankListPresenter.bankList(this, map);
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
    public void onSuccess(ResponseBank body) {
        bankList.clear();
        if (body.isRESPONSE()) {
            bankList.addAll(body.getBankList());
        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }
    }
}

