package com.atlaaya.evdrecharge.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.apiPresenter.UsersPresenter;
import com.atlaaya.evdrecharge.constant.AppConstants;
import com.atlaaya.evdrecharge.databinding.ActivityMyUsersBinding;
import com.atlaaya.evdrecharge.databinding.ItemUserBinding;
import com.atlaaya.evdrecharge.listener.UsersListener;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ResponseUsers;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.CheckInternetConnection;
import com.atlaaya.evdrecharge.utils.DialogClasses;
import com.atlaaya.evdrecharge.utils.MyCustomToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MyUsersActivity extends BaseActivity implements UsersListener {

    private ActivityMyUsersBinding binding;

    private UserListAdapter adapter;
    private UsersPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_users);

        presenter = new UsersPresenter();
        presenter.setView(this);

        setSupportActionBar(binding.toolbar);
        binding.txtNoRecord.setText("");

        adapter = new UserListAdapter(this);
        binding.recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        callUserListAPI();
    }

    @Override
    public void onSuccess(ResponseUsers body) {
        adapter.setUserList(body.getUserList());
        if (body.getUserList().isEmpty()) {
            binding.txtNoRecord.setText(body.getRESPONSE_MSG());
        } else {
            binding.txtNoRecord.setText("");
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    private void callUserListAPI() {
        if (CheckInternetConnection.isInternetConnection(this)) {
            ModelUserInfo userInfo = SessionManager.getUserDetail(this);
            if (userInfo != null) {
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), AppConstants.App_TOKEN));
                map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_EMAIL)));
                map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), SessionManager.getString(this, SessionManager.KEY_PASSWORD)));

                presenter.retailUsers(this, map);
            }
        } else {
            DialogClasses.showDialogInternetAlert(this);
        }
    }

    public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

        private Context mContext;

        private List<ModelUserInfo> userList;

        private UserListAdapter(Context mContext) {
            this.mContext = mContext;
            userList = new ArrayList<>();
        }

        public void setUserList(List<ModelUserInfo> userList) {
            this.userList = userList == null ? new ArrayList<>() : userList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_user, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            ItemUserBinding binding = (ItemUserBinding) holder.getBinding();

            ModelUserInfo userInfo = userList.get(position);

            binding.faceWidget.setInitials(userInfo.getInitials());
            binding.faceWidget.photo(userInfo.getImage());

            binding.txtName.setText(String.format("%s (%s)", userInfo.getFullName(), userInfo.getUsername()));
            binding.txtDetail.setText(String.format("%s/%s", userInfo.getRole().getTitle(), userInfo.getPlan().getPlan_name()));

            String contactDetail = userInfo.getEmail();
            if (contactDetail.isEmpty()) {
                contactDetail = userInfo.getMobile();
            } else if (!userInfo.getMobile().isEmpty()) {
                contactDetail = contactDetail + "\n" + userInfo.getMobile();
            }
            binding.txtContactDetail.setText(contactDetail);

            if (userInfo.isStatus()) {
                binding.ivStatus.setImageResource(R.drawable.ic_check_circle);
            } else {
                binding.ivStatus.setImageResource(R.drawable.ic_info);
            }
        }

        @Override
        public int getItemCount() {
            return userList.size();
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

