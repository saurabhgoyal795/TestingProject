package com.atlaaya.evdrecharge.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.databinding.ActivityMyVouchersBinding;
import com.google.android.material.tabs.TabLayout;

public class MyVouchersActivity extends BaseActivity {

    private ActivityMyVouchersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_vouchers);

        setSupportActionBar(binding.toolbar);
        binding.singlePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.singlePin.setAlpha(1);
                binding.bulkPin.setAlpha((float) 0.5);
                binding.viewSwitcher.setDisplayedChild(0);
            }
        });
        binding.bulkPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bulkPin.setAlpha(1);
                binding.singlePin.setAlpha((float) 0.5);
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

