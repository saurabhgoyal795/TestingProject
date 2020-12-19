package com.atlaaya.evdrecharge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.databinding.ActivityTopupSuccessBinding;
import com.atlaaya.evdrecharge.model.ModelOperator;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelVoucherPlan;
import com.atlaaya.evdrecharge.model.ModelVoucherPurchased;

import java.io.File;

public class TopUpSuccessActivity extends BaseActivity {

    private ActivityTopupSuccessBinding binding;
    private ModelService selectedService;
    private ModelOperator selectedOperator;
    private ModelVoucherPlan selectedPlan;
    private ModelVoucherPurchased voucherPrintData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_topup_success);

        if (getIntent().hasExtra("plan")) {
            selectedPlan = getIntent().getParcelableExtra("plan");
        }
        if (getIntent().hasExtra("service")) {
            selectedService = getIntent().getParcelableExtra("service");
        }
        if (getIntent().hasExtra("operator")) {
            selectedOperator = getIntent().getParcelableExtra("operator");
        }
        if (getIntent().hasExtra("printData")) {
            voucherPrintData = getIntent().getParcelableExtra("printData");
        }
        setSupportActionBar(binding.toolbar);

        if (selectedService != null) {
            binding.toolbar.setTitle(selectedService.getService_name());
        } else {
            binding.toolbar.setTitle(getString(R.string.txt_mobile_recharge));
        }

        binding.text1.setText(getString(R.string.txt_successful_recharge));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}

