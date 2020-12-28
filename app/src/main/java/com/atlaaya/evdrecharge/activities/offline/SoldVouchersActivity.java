package com.atlaaya.evdrecharge.activities.offline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.activities.BaseActivity;
import com.atlaaya.evdrecharge.activities.VoucherSuccessPrintActivity;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityUnsoldVouchersBinding;
import com.atlaaya.evdrecharge.databinding.ItemUnsoldVouchersBinding;
import com.atlaaya.evdrecharge.firebase.database.MapTypedValueEventListener;
import com.atlaaya.evdrecharge.firebase.references.Database;
import com.atlaaya.evdrecharge.firebase.references.DocumentRefrence;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ModelVoucher;
import com.atlaaya.evdrecharge.model.ModelVoucherPurchased;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.ChangeDateFormat;
import com.google.common.base.Strings;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SoldVouchersActivity extends BaseActivity {

    private ActivityUnsoldVouchersBinding binding;
    private ModelUserInfo userInfo;
    private UnsoldVoucherAdapter adapter;

    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_unsold_vouchers);
        userInfo = SessionManager.getUserDetail(this);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(getString(R.string.title_sold_vouchers));
        binding.txtNoRecord.setText("");

        adapter = new UnsoldVoucherAdapter(this);
        binding.recyclerView.setAdapter(adapter);

//        loadVouchers();
        loadVouchersFirestore();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadVouchersFirestore() {

        showProgressDialog();
       /* DocumentRefrence.vouchersPrinted(firebaseFirestore(),  userInfo.getId())
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<ModelVoucher> finalList = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("vouchers", document.getId() + " => " + document.getData());
                            ModelVoucher voucher = document.toObject(ModelVoucher.class);
                            finalList.add(voucher);
                        }
                    } else {
                        Log.d("vouchers", "Error getting documents: ", task.getException());
                    }
                    adapter.setList(finalList);
                    binding.txtNoRecord.setText("");

                    dismissProgressDialog();
                })
        .addOnFailureListener(e -> dismissProgressDialog())
        .addOnCanceledListener(this::dismissProgressDialog);*/

        listenerRegistration = DocumentRefrence.vouchersPrinted(firebaseFirestore(), userInfo.getId())
                .addSnapshotListener(MetadataChanges.INCLUDE, (querySnapshot, e) -> {
                    if (e != null) {
                        Log.w("vouchers", "Listen error", e);
                        dismissProgressDialog();
                        return;
                    }

                /*    for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                        if (change.getType() == DocumentChange.Type.ADDED) {
                            Log.d("vouchers", change.getDocument().getId() + " => " + change.getDocument().getData());
                        }
                        String source = querySnapshot.getMetadata().isFromCache() ?
                                "local cache" : "server";
                        Log.d("vouchers", "Data fetched from " + source);
                    }*/

                    ArrayList<ModelVoucher> finalList = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                        Log.d("vouchers", document.getId() + " => " + document.getData());
                        ModelVoucher voucher = document.toObject(ModelVoucher.class);
                        finalList.add(voucher);
                    }
                    Log.d("vouchers", "total vouchers:  " + finalList.size());

                    adapter.setList(finalList);
                    binding.txtNoRecord.setText("");

                    dismissProgressDialog();
                });
    }

    private void loadVouchers() {
        Database.voucherNonPrints(databaseReference(), "" + userInfo.getId())
                .addValueEventListener(new MapTypedValueEventListener<ModelVoucher>(ModelVoucher.class) {
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                    @Override
                    public void onDataChange(Map data) {

                        ArrayList<ModelVoucher> vouchers = null;
                        if (data != null) {
                            vouchers = new ArrayList<>(data.values());
                        }
                        ArrayList<ModelVoucher> finalList = new ArrayList<>();
                        if (vouchers != null && !vouchers.isEmpty()) {
                            for (ModelVoucher voucher : vouchers) {
                                if (voucher.getIs_print() == 1) {
                                    finalList.add(voucher);
                                }
                            }
                        }
                        adapter.setList(finalList);
                        binding.txtNoRecord.setText("");

                      /*  for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            // TODO: handle the post
                        }*/

                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (listenerRegistration != null)
            listenerRegistration.remove();
        super.onDestroy();
    }

    public class UnsoldVoucherAdapter extends RecyclerView.Adapter<UnsoldVoucherAdapter.ViewHolder> {

        private Context mContext;

        private List<ModelVoucher> voucherList;

        private UnsoldVoucherAdapter(Context mContext) {
            this.mContext = mContext;
            voucherList = new ArrayList<>();
        }

        public void setList(List<ModelVoucher> voucherList) {
            this.voucherList = voucherList == null ? new ArrayList<>() : voucherList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public UnsoldVoucherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_unsold_vouchers, parent, false);
            return new UnsoldVoucherAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final UnsoldVoucherAdapter.ViewHolder holder, int position) {
            ItemUnsoldVouchersBinding binding = (ItemUnsoldVouchersBinding) holder.getBinding();

            ModelVoucher voucherInfo = voucherList.get(position);

            binding.txtPinGroup.setText(Html.fromHtml(getString(R.string.text_pin_group, voucherInfo.getPin_group_no())));
            binding.txtPinSerial.setText(Html.fromHtml(getString(R.string.text_pin_serial, voucherInfo.getPin_serial_no())));
            binding.txtTransactionId.setText(getString(R.string.txt_transaction_id, String.valueOf(voucherInfo.getTransaction_id())));
            binding.txtTransactionRefNo.setText(getString(R.string.txt_transaction_ref_no, String.valueOf(voucherInfo.getTxn_ref_num())));
            binding.txtAmount.setText(getString(R.string.balance, ""+(int)voucherInfo.getVoucher_amount()));
            binding.txtDate.setText(Html.fromHtml(getString(R.string.text_pin_sold_date,
                    Strings.isNullOrEmpty(voucherInfo.getOffline_sold_date()) ? voucherInfo.getDate() : voucherInfo.getOffline_sold_date())));

            binding.txtSoldStatus.setVisibility(View.VISIBLE);
            binding.btnPrint.setVisibility(View.VISIBLE);

            binding.btnPrint.setOnClickListener(v -> {

                ArrayList<ModelVoucher> finalPurchasedVoucherList = new ArrayList<>();
                finalPurchasedVoucherList.add(voucherInfo);
                ArrayList<ModelVoucherPurchased> voucherPurchasedList = new ArrayList<>();

                long currentTimeMillis = System.currentTimeMillis();
                String offlineSoldDate = ChangeDateFormat.getDateTimeFromMillisecond("yyyy-MM-dd HH:mm:ss", currentTimeMillis);
                for (ModelVoucher voucher : finalPurchasedVoucherList) {
                    voucher.setOffline_sold_date(offlineSoldDate);

                    ModelVoucherPurchased modelVoucherPurchased = new ModelVoucherPurchased();
                    modelVoucherPurchased.setPrintable_text(AppConstants.getPinPrintableData(voucher, userInfo));
                    modelVoucherPurchased.setNon_printable_text(AppConstants.getPinNonPrintableData(voucher, userInfo));
                    modelVoucherPurchased.setSaleid(voucher.getOrder_id());
                    modelVoucherPurchased.setVchr_odr_id(voucher.getOffline_order_id());
                    modelVoucherPurchased.setVoucher(voucher);

                    voucherPurchasedList.add(modelVoucherPurchased);
                }

                Intent intent = new Intent(getApplicationContext(), VoucherSuccessPrintActivity.class);
                VoucherSuccessPrintActivity.setPrintVoucherContentArrayList(voucherPurchasedList);
                intent.putExtra("fromHistory", true);
                startActivity(intent);

            });

        }

        @Override
        public int getItemCount() {
            return voucherList.size();
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

