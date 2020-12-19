package com.atlaaya.evdrecharge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.databinding.ActivityLoanPaymentBinding;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.DisableEmojiInEditText;

public class LoanPaymentRequestActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLoanPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loan_payment);
        setSupportActionBar(binding.toolbar);

        binding.etAccountName.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etAccountNumber.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etAmountToPay.setFilters(new InputFilter[]{new DisableEmojiInEditText()});
        binding.etRefNum.setFilters(new InputFilter[]{new DisableEmojiInEditText()});

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
        }
    }

    private void validateAccountDetail() {
        String accountName = binding.etAccountName.getText().toString().trim();
        String accountNumber = binding.etAccountNumber.getText().toString().trim();
        String amountToPay = binding.etAmountToPay.getText().toString().trim();
//        String refNum = binding.etRefNum.getText().toString().trim();
        String refNum = "OS4534745754712978";

        if (accountName.isEmpty()) {
            binding.etAccountName.requestFocus();
            binding.etAccountName.setError("Please enter client name");
        } else if (accountNumber.isEmpty()) {
            binding.etAccountNumber.requestFocus();
            binding.etAccountNumber.setError("Please enter bank account number");
        } else if (amountToPay.isEmpty()) {
            binding.etAmountToPay.requestFocus();
            binding.etAmountToPay.setError("Please enter amount");
        } /*else if (refNum.isEmpty()) {
            binding.etRefNum.requestFocus();
            binding.etRefNum.setError("Please enter reference number");
        } */else if (CheckInternetConnection.isInternetConnection(this)) {

            Intent intent = new Intent(this, LoanPaymentSuccessPrintActivity.class);
            intent.putExtra("accountName", accountName);
            intent.putExtra("accountNumber", accountNumber);
            intent.putExtra("amountToPay", amountToPay);
            intent.putExtra("refNum", refNum);
            startActivity(intent);
            finish();
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }
}

