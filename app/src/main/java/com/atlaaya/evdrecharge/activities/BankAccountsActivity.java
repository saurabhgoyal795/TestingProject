package com.atlaaya.evdrecharge.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.databinding.ActivityManageBankAccountsBinding;
import com.google.android.material.tabs.TabLayout;

public class BankAccountsActivity extends BaseActivity {

    private ActivityManageBankAccountsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_bank_accounts);

        setSupportActionBar(binding.toolbar);
        binding.companyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.companyAccount.setAlpha(1);
                binding.myAccount.setAlpha((float) 0.5);
                binding.viewSwitcher.setDisplayedChild(0);
            }
        });
        binding.myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.myAccount.setAlpha(1);
                binding.companyAccount.setAlpha((float) 0.5);
                binding.viewSwitcher.setDisplayedChild(1);
            }
        });
//        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//
//                if (tab.getPosition() == 0) {
//                    binding.viewSwitcher.setDisplayedChild(0);
//                } else {
//                    binding.viewSwitcher.setDisplayedChild(1);
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}

