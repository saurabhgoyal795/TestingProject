package com.atlaaya.evdrecharge.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.activities.VoucherSuccessPrintActivity;
import com.atlaaya.evdrecharge.apiPresenter.PurchaseVoucherBulkPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.FragmentVoucherOrderBinding;
import com.atlaaya.evdrecharge.databinding.ItemRechargeAmountConfirmBinding;
import com.atlaaya.evdrecharge.databinding.ItemVoucherOrderBinding;
import com.atlaaya.evdrecharge.listener.PurchaseVoucherBulkListener;
import com.atlaaya.evdrecharge.model.ModelFilterConfig;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ModelVoucherOrder;
import com.atlaaya.evdrecharge.model.ModelVoucherPlan;
import com.atlaaya.evdrecharge.model.ResponseVoucherOrderHistory;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchaseBulk;
import com.atlaaya.evdrecharge.model.ResponseVoucherPurchaseBulkOrder;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.MyCustomToast;
import com.atlaaya.evdrecharge.utils.PriceFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;


public class FragmentBulkVoucherOrder extends BaseFragment implements PurchaseVoucherBulkListener, SwipeRefreshLayout.OnRefreshListener {

    boolean isLoading = false;
    boolean isRefresh = false;
    private FragmentVoucherOrderBinding binding;
    private VoucherListAdapter adapter;
    private PurchaseVoucherBulkPresenter voucherBulkPresenter;
    private ModelFilterConfig filterConfig;
    private Activity mActivity;
    private int page_no = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        filterConfig = SessionManager.getFilterConfig(mActivity);

        voucherBulkPresenter = new PurchaseVoucherBulkPresenter();
        voucherBulkPresenter.setView(this);
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

        initScrollListener();

        onRefresh();

        return binding.getRoot();
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        page_no = 1;
        isRefresh = true;
        callVoucherOrderHistory();
    }

    private void initScrollListener() {
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null
                            && linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.getVoucherOrderList().size() - 1) {
                        //bottom of list!
                        isRefresh = false;
                        page_no++;
                        callVoucherOrderHistory();
                        isLoading = true;
                    }
                }
            }
        });
    }


    @Override
    public void onSuccessVoucherBulkOrder(ResponseVoucherPurchaseBulk body) {
    }

    @Override
    public void onSuccessVoucherBulkOrderPrintDetail(ResponseVoucherPurchaseBulkOrder body) {
        if (body.isRESPONSE()) {
            mActivity.runOnUiThread(() -> {
                Intent intent = new Intent(mActivity, VoucherSuccessPrintActivity.class);
                VoucherSuccessPrintActivity.setPrintVoucherContentArrayList(body.getVoucherPurchasedList());
//            intent.putParcelableArrayListExtra("printDataList", body.getVoucherPurchasedList());
                intent.putExtra("fromHistory", true);
                startActivity(intent);
            });
        } else {
            MyCustomToast.showToast(mActivity, body.getRESPONSE_MSG());
        }
    }

    @Override
    public void onSuccessVoucherBulkOrderHistory(ResponseVoucherOrderHistory body) {
        adapter.setVoucherOrderList(body.getVoucherOrderList());
        if (adapter.getVoucherOrderList().isEmpty()) {
            binding.txtNoRecord.setText(body.getRESPONSE_MSG());
        } else {
            binding.txtNoRecord.setText("");
        }
        isLoading = false;
    }

    @Override
    public Context getContext() {
        return mActivity;
    }

    private void callVoucherOrderHistory() {
        binding.swipeRefreshLayout.setRefreshing(false);
        if (CheckInternetConnection.isInternetConnection(mActivity)) {
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
            map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(mActivity, SessionManager.KEY_EMAIL)));
            map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(mActivity, SessionManager.KEY_PASSWORD)));
            map.put("page", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(page_no)));

            voucherBulkPresenter.voucherOrderHistory(mActivity, map);
        } else {
            DialogClasses.showDialogInternetAlert(mActivity);
        }
    }

    private void callVoucherPurchaseBulkOrderDetail(int orderId) {
        if (CheckInternetConnection.isInternetConnection(mActivity)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(mActivity);
            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                map.put("username", RequestBody.create(MediaType.parse("multipart/form-data"), userInfo.getUsername()));
                map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(mActivity, SessionManager.KEY_PASSWORD)));
                map.put("order_id", RequestBody.create(MediaType.parse("multipart/form-data"), "" + orderId));

                voucherBulkPresenter.voucherBulkOrderDetail(mActivity, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(mActivity);
        }
    }

    public class VoucherListAdapter extends RecyclerView.Adapter<VoucherListAdapter.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        private Context mContext;

        private List<ModelVoucherOrder> voucherOrderList;

        private VoucherListAdapter(Context mContext) {
            this.mContext = mContext;
            voucherOrderList = new ArrayList<>();
        }

        public List<ModelVoucherOrder> getVoucherOrderList() {
            return voucherOrderList;
        }

        void setVoucherOrderList(List<ModelVoucherOrder> voucherOrderList) {
            if (isRefresh) {
                this.voucherOrderList.clear();
                this.voucherOrderList = voucherOrderList == null ? new ArrayList<>() : voucherOrderList;
            } else {
                if (voucherOrderList != null) {
                    this.voucherOrderList.addAll(voucherOrderList);
                }
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VoucherListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_voucher_order, parent, false);
                return new VoucherListAdapter.ViewHolder(binding);
            } else {
                ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_loading, parent, false);
                return new VoucherListAdapter.ViewHolder(binding);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull final VoucherListAdapter.ViewHolder holder, int position) {

            if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
                ItemVoucherOrderBinding binding = (ItemVoucherOrderBinding) holder.getBinding();

                ModelVoucherOrder voucherOrderInfo = voucherOrderList.get(position);

                binding.txtTransactionOrderId.setText(Html.fromHtml(getString(R.string.text_transac_order_id, voucherOrderInfo.getTransactionOrderId())));
                binding.txtSoldDate.setText(Html.fromHtml(getString(R.string.text_pin_sold_date, voucherOrderInfo.getCreated())));

                switch (voucherOrderInfo.getStatus()) {
                    case 2:
                        binding.txtStatus.setText(getString(R.string.txt_status_success));
                        binding.txtStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_green));
                        break;
                    case 1:
                        binding.txtStatus.setText(getString(R.string.txt_status_pending));
                        binding.txtStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_yellow));
                        break;
                    case 3:
                        binding.txtStatus.setText(getString(R.string.txt_status_failed));
                        binding.txtStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_red));
                        break;
                }

                RechargeAmountAdapter adapter = new RechargeAmountAdapter(mContext);
                binding.recyclerView.setAdapter(adapter);
                adapter.setRechargePlanList(voucherOrderInfo.getVoucherPlanList());

                binding.txtTtlAmount.setText(getString(R.string.txt_grand_ttl, PriceFormat.decimalTwoDigit1(voucherOrderInfo.getTotalOrderAmount())));
                binding.txtTtlQuantity.setText(getString(R.string.txt_ttl_qty, voucherOrderInfo.getTotalOrderQuantity()));

                binding.btnPrint.setOnClickListener(v -> callVoucherPurchaseBulkOrderDetail(voucherOrderInfo.getId()));
            } else {
//                showLoadingView((LoadingViewHolder) viewHolder, position);
            }
        }

        @Override
        public int getItemCount() {
            return voucherOrderList.size();
        }


        /**
         * The following method decides the type of ViewHolder to display in the RecyclerView
         *
         * @param position
         * @return
         */
        @Override
        public int getItemViewType(int position) {
            return voucherOrderList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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


    public class RechargeAmountAdapter extends RecyclerView.Adapter<RechargeAmountAdapter.ViewHolder> {

        private Context mContext;

        private List<ModelVoucherPlan> rechargePlanList;

        private RechargeAmountAdapter(Context mContext) {
            this.mContext = mContext;
            rechargePlanList = new ArrayList<>();
        }

        void setRechargePlanList(List<ModelVoucherPlan> rechargePlanList) {
            this.rechargePlanList = rechargePlanList == null ? new ArrayList<>() : rechargePlanList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RechargeAmountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_recharge_amount_confirm, parent, false);
            return new RechargeAmountAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final RechargeAmountAdapter.ViewHolder holder, int position) {
            ItemRechargeAmountConfirmBinding binding = (ItemRechargeAmountConfirmBinding) holder.getBinding();

            binding.txtQty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            binding.txtAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            binding.txtTtlAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            binding.txtQty.setText(String.format("x %s", String.valueOf(rechargePlanList.get(holder.getAdapterPosition()).getSelectedQty())));
            binding.txtAmount.setText(String.format("%s %s", PriceFormat.decimalTwoDigit1(rechargePlanList.get(position).getAmount()), getString(R.string.currency_ethiopia_unit)));

            int ttlQuantity = rechargePlanList.get(holder.getAdapterPosition()).getSelectedQty();
            binding.txtTtlAmount.setText(PriceFormat.decimalTwoDigit1(rechargePlanList.get(position).getAmount() * ttlQuantity));

        }

        @Override
        public int getItemCount() {
            return rechargePlanList.size();
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