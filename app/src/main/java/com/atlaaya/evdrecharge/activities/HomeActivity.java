package com.atlaaya.evdrecharge.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.apiPresenter.BalancePresenter;
import com.atlaaya.evdrecharge.apiPresenter.FilterConfigPresenter;
import com.atlaaya.evdrecharge.apiPresenter.ServicesPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityHomeBinding;
import com.atlaaya.evdrecharge.databinding.ItemServiceBinding;
import com.atlaaya.evdrecharge.databinding.NavHeaderNavigationBinding;
import com.atlaaya.evdrecharge.listener.BalanceListener;
import com.atlaaya.evdrecharge.listener.FilterConfigListener;
import com.atlaaya.evdrecharge.listener.ServiceListener;
import com.atlaaya.evdrecharge.model.ModelBalance;
import com.atlaaya.evdrecharge.model.ModelFilterConfig;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ResponseBalance;
import com.atlaaya.evdrecharge.model.ResponseFilterConfig;
import com.atlaaya.evdrecharge.model.ResponseServices;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.MyCustomToast;
import com.atlaaya.evdrecharge.utils.PriceFormat;
import com.atlaaya.evdrecharge.utils.Utility;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class HomeActivity extends BaseActivity implements LifecycleObserver,
        NavigationView.OnNavigationItemSelectedListener, ServiceListener, BalanceListener, FilterConfigListener {

    private ActivityHomeBinding binding;
    private NavHeaderNavigationBinding headerBinding;
    //    private AppBarConfiguration mAppBarConfiguration;
    private List<ModelService> serviceList;

    private ServicesPresenter presenter;
    private BalancePresenter balancePresenter;
    private FilterConfigPresenter filterConfigPresenter;

    CountDownTimer timer = new CountDownTimer(5 * 60 * 1000, 1000) {

        public void onTick(long millisUntilFinished) {
            //Some code
        }

        public void onFinish() {
            ModelUserInfo userInfo = SessionManager.getUserDetail(getApplicationContext());
            if (userInfo != null) {
                //Logout
//            BaseActivity.clearCache(getApplicationContext());
                UserLogoutDeleteAccount();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        View headerView = binding.navView.getHeaderView(0);
        headerBinding = NavHeaderNavigationBinding.bind(headerView);

//        Utility.getDeviceDensityString(this);

        serviceList = new ArrayList<>();

        balancePresenter = new BalancePresenter();
        balancePresenter.setView(this);

        presenter = new ServicesPresenter();
        presenter.setView(this);

        filterConfigPresenter = new FilterConfigPresenter();
        filterConfigPresenter.setView(this);

        setSupportActionBar(binding.contentHome.toolbar);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder( R.id.nav_gallery, R.id.nav_slideshow,
//                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
//                .setDrawerLayout(binding.drawerLayout)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);

        binding.navView.setNavigationItemSelectedListener(this);

        binding.contentHome.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_my_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_balance:
                startActivity(new Intent(getApplicationContext(), WalletActivity.class));
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_transaction_history:
                startActivity(new Intent(getApplicationContext(), TransactionHistoryActivity.class));
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_my_statements:
                startActivity(new Intent(getApplicationContext(), MyStatementsActivity.class));
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_my_banks:
                startActivity(new Intent(getApplicationContext(), BankAccountsActivity.class));
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_my_vouchers:
                startActivity(new Intent(getApplicationContext(), MyVouchersActivity.class));
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_my_users:
                startActivity(new Intent(getApplicationContext(), MyUsersActivity.class));
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_bluetooth_test:
                startActivity(new Intent(getApplicationContext(), BluetoothMainActivity.class));
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_logout:
                dialogLogout();
                return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        callFilterConfigAPI();
        callServiceAPI();
        callBalanceAPI();
        setProfileData();
    }

    @Override
    protected void onDestroy() {
        clearCache(this);
        super.onDestroy();
    }

    private void callServiceAPI() {
        if (CheckInternetConnection.isInternetConnection(this)) {
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));

            presenter.servicesList(this, map);
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    private void callBalanceAPI() {
        ModelUserInfo userInfo = SessionManager.getUserDetail(this);
        if (userInfo != null) {
            if (CheckInternetConnection.isInternetConnection(this)) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_EMAIL)));
                map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_PASSWORD)));

                balancePresenter.androidBalance(this, map);
            }
        }
    }

    private void callFilterConfigAPI() {
        ModelFilterConfig filterConfig = SessionManager.getFilterConfig(this);
        if (filterConfig == null) {
            if (CheckInternetConnection.isInternetConnection(this)) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                filterConfigPresenter.filterConfig(this, map);
            }
        }
    }

    @Override
    public void onSuccess(ResponseServices body) {
        serviceList.clear();

        if (body.isRESPONSE()) {
            serviceList.addAll(body.getServiceList());

     /*       ModelService modelService =  new ModelService();
            modelService.setId(3);
            modelService.setService_name("Loan Payment");
            serviceList.add(modelService);*/

        } else {
            MyCustomToast.showErrorAlert(this, body.getRESPONSE_MSG());
        }

        setServiceData();
    }

    @Override
    public Context getContext() {
        return this;
    }

    private void setServiceData() {
        binding.contentHome.recyclerView.setAdapter(new ServiceAdapter(this));
    }

    private void setProfileData() {

        binding.contentHome.txtUsername.setText(getString(R.string.txt_welcome_user, ""));

        headerBinding.txtName.setText("");
        headerBinding.txtEmail.setText("");
        headerBinding.faceWidget.setInitials("");

        ModelUserInfo userInfo = SessionManager.getUserDetail(this);
        if (userInfo != null) {
            binding.contentHome.txtUsername.setText(getString(R.string.txt_welcome_user, userInfo.getCompany_name()));
            binding.contentHome.txtAvlBalanceAmount.setText(getString(R.string.balance, PriceFormat.decimalTwoDigit1(userInfo.getWallet_amount())));

            headerBinding.txtName.setText(userInfo.getFullName());
            headerBinding.txtEmail.setText(userInfo.getEmail());
            headerBinding.faceWidget.setInitials(userInfo.getInitials());
            headerBinding.faceWidget.photo(userInfo.getImage());
        }
        setBalanceInfo();
    }

    private void setBalanceInfo() {
        String balance = "0.00";
        ModelBalance modelBalance = SessionManager.getBalance(this);
        if (modelBalance != null) {
            balance = PriceFormat.decimalTwoDigit1(modelBalance.getEffective_balance());
        }
        binding.contentHome.txtAvlBalanceAmount.setText(getString(R.string.balance, balance));
    }

    @Override
    public void onBalanceSuccess(ResponseBalance body) {
        if (body.isRESPONSE()) {
            SessionManager.saveBalance(this, body.getBalanceInfo());
            setBalanceInfo();
        }
    }

    @Override
    public void onFilterConfigSuccess(ResponseFilterConfig body) {
        SessionManager.saveFilterConfig(this, body.getFilterConfig());
    }

    public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

        private Context mContext;

        private ServiceAdapter(Context mContext) {
            this.mContext = mContext;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_service, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            ItemServiceBinding binding = (ItemServiceBinding) holder.getBinding();

            if (serviceList.get(position).getService_name().equalsIgnoreCase("Voucher")) {
                binding.ivLogo.setImageResource(R.drawable.voucher_1);
            } else if (serviceList.get(position).getService_name().equalsIgnoreCase("Topup")) {
                binding.ivLogo.setImageResource(R.drawable.mc_charger_1);
            } else if (serviceList.get(position).getService_name().equalsIgnoreCase("Loan Payment")) {
                binding.ivLogo.setImageResource(R.drawable.loan_payment);
            } else if (serviceList.get(position).getService_name().equalsIgnoreCase("Electricity bill")) {
                binding.ivLogo.setImageResource(R.drawable.electri);
            } else if (serviceList.get(position).getService_name().equalsIgnoreCase("Water bill")) {
                binding.ivLogo.setImageResource(R.drawable.water_bill);
            } else {
                binding.ivLogo.setImageResource(R.drawable.voucher);
            }

            binding.txtName.setText(serviceList.get(position).getService_name());

            binding.layoutMain.setOnClickListener(v -> {

                Intent intent;
                if (serviceList.get(position).getService_name().equalsIgnoreCase("Loan Payment")) {
                    intent = new Intent(HomeActivity.this, LoanPaymentRequestActivity.class);
                }else{
//                intent = new Intent(HomeActivity.this, VoucherListActivity.class);
                    intent = new Intent(HomeActivity.this, RechargeOperatorActivity.class);
                }
//                intent.putExtra("type", AppConstants.KEY_TYPE_VOUCHER);
                intent.putExtra("service", serviceList.get(position));
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return serviceList.size();
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


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onAppBackgrounded() {
        Log.d("MyApplication", "App in background");
        timer.start();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onAppForegrounded() {
        Log.d("MyApplication", "App in foreground");
        timer.cancel();
    }
}


