package com.atlaaya.evdrecharge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.databinding.ActivitySendingRequestBinding;
import com.atlaaya.evdrecharge.model.ModelOperator;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelVoucherPlan;
import com.atlaaya.evdrecharge.model.ModelVoucherPurchased;

public class SendingRequestActivity extends BaseActivity {

    private ActivitySendingRequestBinding binding;

    private ModelService selectedService;
    private ModelOperator selectedOperator;

    private ModelVoucherPlan selectedPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sending_request);

        if (getIntent().hasExtra("plan")) {
            selectedPlan = getIntent().getParcelableExtra("plan");
        }
        if (getIntent().hasExtra("service")) {
            selectedService = getIntent().getParcelableExtra("service");
        }
        if (getIntent().hasExtra("operator")) {
            selectedOperator = getIntent().getParcelableExtra("operator");
        }
        setSupportActionBar(binding.toolbar);

        if (selectedService != null) {
            binding.toolbar.setTitle(selectedService.getService_name());
        } else {
            binding.toolbar.setTitle(getString(R.string.txt_voucher_recharge));
        }

        new Handler().postDelayed(() -> {
            Intent intent;

            if (getIntent().hasExtra("top_up_amount")) {
                intent = new Intent(getApplicationContext(), TopUpSuccessActivity.class);
                intent.putExtra("top_up_amount", getIntent().getStringExtra("top_up_amount"));
            } else {
                intent = new Intent(getApplicationContext(), VoucherSuccessPrintActivity.class);

                runOnUiThread(() -> {
                    if (getIntent().hasExtra("printData"))
                        intent.putExtra("printData", (ModelVoucherPurchased) getIntent().getParcelableExtra("printData"));

                    if (getIntent().hasExtra("printDataList")){
                        intent.putParcelableArrayListExtra("printDataList", getIntent().getParcelableArrayListExtra("printDataList"));
                    }
                });
            }

            intent.putExtra("service", selectedService);
            intent.putExtra("operator", selectedOperator);
            if (selectedPlan != null) {
                intent.putExtra("plan", selectedPlan);
            }
            if (getIntent().hasExtra("mobile_number"))
                intent.putExtra("mobile_number", getIntent().getStringExtra("mobile_number"));

//            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
//            startActivity(intent);
            startActivityForResult(intent, 200);
//            finish();
        }, 3000);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
//            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 200 && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

