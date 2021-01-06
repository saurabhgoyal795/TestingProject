package com.atlaaya.evdrecharge.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.activities.VoucherSuccessPrintActivity;
import com.atlaaya.evdrecharge.apiPresenter.PurchaseVoucherPresenter;
import com.atlaaya.evdrecharge.apiPresenter.VoucherPlansPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.FragmentVoucherOrderBinding;
import com.atlaaya.evdrecharge.databinding.ItemMyVoucherBinding;
import com.atlaaya.evdrecharge.listener.PurchaseVoucherListener;
import com.atlaaya.evdrecharge.listener.VoucherPlanListener;
import com.atlaaya.evdrecharge.model.ModelFilterConfig;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ModelVoucherPlan;
import com.atlaaya.evdrecharge.model.ModelVoucherPurchased;
import com.atlaaya.evdrecharge.model.ResponseTempVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherPlan;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchase;
import com.atlaaya.evdrecharge.model.ResponseVoucherSingle;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.MyCustomToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;


public class FragmentSingleVoucherOrder extends BaseFragment implements VoucherPlanListener, PurchaseVoucherListener,
        SwipeRefreshLayout.OnRefreshListener {

    private FragmentVoucherOrderBinding binding;

    private VoucherListAdapter adapter;
    private VoucherPlansPresenter presenter;
    private PurchaseVoucherPresenter voucherDetailPresenter;

    private ModelFilterConfig filterConfig;
    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        filterConfig = SessionManager.getFilterConfig(mActivity);

        presenter = new VoucherPlansPresenter();
        presenter.setView(this);

        voucherDetailPresenter = new PurchaseVoucherPresenter();
        voucherDetailPresenter.setView(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voucher_order, container, false);

        binding.txtNoRecord.setText("");

        adapter = new VoucherListAdapter(mActivity);
        binding.recyclerView.setAdapter(adapter);

        binding.swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        binding.swipeRefreshLayout.setOnRefreshListener(this);

        onRefresh();

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        callVoucherListAPI();
    }

    @Override
    public void onSuccess(ResponseVoucherPlan body) {
        adapter.setPlanList(body.getVoucherPlanList());
        if (body.getVoucherPlanList().isEmpty()) {
            binding.txtNoRecord.setText(body.getRESPONSE_MSG());
        } else {
            binding.txtNoRecord.setText("");
        }
    }

    @Override
    public void onSuccessTempVoucher(ResponseTempVoucherPurchase body) {

    }

    @Override
    public void onSuccessVoucher(ResponseVoucherPurchase body) {
        if (body.isRESPONSE()) {
            mActivity.runOnUiThread(() -> {
                Intent intent = new Intent(mActivity, VoucherSuccessPrintActivity.class);

                ArrayList<ModelVoucherPurchased> printDataList = new ArrayList<>();
                printDataList.add(body.getRESPONSE_DATA());

                VoucherSuccessPrintActivity.setPrintVoucherContentArrayList(printDataList);
//                intent.putParcelableArrayListExtra("printDataList", printDataList);
//            intent.putExtra("printData", body.getRESPONSE_DATA());

                intent.putExtra("fromHistory", true);
                startActivity(intent);
            });
        } else {
            MyCustomToast.showToast(mActivity, body.getRESPONSE_MSG());
        }
    }

    @Override
    public void onSuccessSinglePrintVoucher(ResponseVoucherPurchase body) {

    }


    @Override
    public Context getContext() {
        return mActivity;
    }

    private void callVoucherListAPI() {
        binding.swipeRefreshLayout.setRefreshing(false);
        if (CheckInternetConnection.isInternetConnection(mActivity)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(mActivity);
            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(mActivity, SessionManager.KEY_EMAIL)));
                map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(mActivity, SessionManager.KEY_PASSWORD)));

                presenter.myRetailVouchers(mActivity, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(mActivity);
        }
    }

    private void callVoucherDetail(int id) {
        if (CheckInternetConnection.isInternetConnection(mActivity)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(mActivity);
            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(mActivity, SessionManager.KEY_EMAIL)));
                map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(mActivity, SessionManager.KEY_PASSWORD)));
                map.put("vid", RequestBody.create(MediaType.parse("multipart/form-data"), "" + id));

                voucherDetailPresenter.purchasedVoucherPrintDetail(mActivity, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(mActivity);
        }
    }

    public class VoucherListAdapter extends RecyclerView.Adapter<VoucherListAdapter.ViewHolder> {

        private Context mContext;

        private List<ModelVoucherPlan> planList;

        private VoucherListAdapter(Context mContext) {
            this.mContext = mContext;
            planList = new ArrayList<>();
        }

        void setPlanList(List<ModelVoucherPlan> planList) {
            this.planList = planList == null ? new ArrayList<>() : planList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_my_voucher, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            ItemMyVoucherBinding binding = (ItemMyVoucherBinding) holder.getBinding();

            ModelVoucherPlan planInfo = planList.get(position);

            String s = "Hello Everyone";
            SpannableString ss1 = new SpannableString(s);
            ss1.setSpan(new RelativeSizeSpan(2f), 0, 5, 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);// set color

//            String price = PriceFormat.decimalTwoDigit1(planInfo.getAmount());
            String price = String.valueOf(planInfo.getAmount());
            String currency = getString(R.string.currency_ethiopia_unit);

            SpannableString span1 = new SpannableString(price);
            span1.setSpan(new AbsoluteSizeSpan(getResources().getDimensionPixelSize(R.dimen.text_size_24sp)), 0, price.length(), SPAN_INCLUSIVE_INCLUSIVE);

            SpannableString span2 = new SpannableString(currency);
            span2.setSpan(new AbsoluteSizeSpan(getResources().getDimensionPixelSize(R.dimen.text_size_14sp)), 0, currency.length(), SPAN_INCLUSIVE_INCLUSIVE);
            // let's put both spans together with a separator and all
            CharSequence finalText = TextUtils.concat(span1, "\n", span2);
            binding.txtAmount.setText(finalText);

//            binding.txtAmount.setText(Html.fromHtml("<html><body>" +
//                    "<font size=20>" + price + "</font>" +
//                    "<br>" + currency + " </body><html>"));
//            binding.txtAmount.setText(getString(R.string.balance_, PriceFormat.decimalTwoDigit1(planInfo.getAmount())));

            binding.txtPinGroup.setText(Html.fromHtml(getString(R.string.text_pin_group, planInfo.getPin_group_no())));
            binding.txtPinSerial.setText(Html.fromHtml(getString(R.string.text_pin_serial, planInfo.getPin_serial_no())));
            binding.txtOperator.setText(Html.fromHtml(getString(R.string.text_pin_operator, planInfo.getOperator().getTitle())));
            binding.txtSoldDate.setText(Html.fromHtml(getString(R.string.text_pin_sold_date, planInfo.getCreated())));
            binding.txtExpiryDate.setText(Html.fromHtml(getString(R.string.text_pin_expiry_date, planInfo.getPin_expiry_date())));

            switch (planInfo.getVoucher_status()) {
                case 1:
                    binding.txtStatus.setText(filterConfig.getFilters().getVoucherStatus().get$1());
                    binding.txtStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_green));
                    break;
                case 2:
                    binding.txtStatus.setText(filterConfig.getFilters().getVoucherStatus().get$2());
                    binding.txtStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_yellow));
                    break;
                case 3:
                    binding.txtStatus.setText(filterConfig.getFilters().getVoucherStatus().get$3());
                    binding.txtStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_yellow));
                    break;
                case 4:
                    binding.txtStatus.setText(filterConfig.getFilters().getVoucherStatus().get$4());
                    binding.txtStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_red));
                    break;
            }

            binding.cardView.setOnClickListener(v -> callVoucherDetail(planInfo.getId()));
        }

        @Override
        public int getItemCount() {
            return planList.size();
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
