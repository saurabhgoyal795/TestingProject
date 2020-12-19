package com.atlaaya.evdrecharge.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ModelFundRequest implements Parcelable {


    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("user_parent_id")
    private int userParentId;
    @SerializedName("user_banker_id")
    private int userBankerId;
    @SerializedName("amount")
    private int amount;
    @SerializedName("transaction_number")
    private String transactionNumber;
    @SerializedName("user_remark")
    private String userRemark;
    @SerializedName("admin_remark")
    private String adminRemark;
    @SerializedName("requested_by")
    private String requestedBy;
    @SerializedName("transaction_id")
    private int transactionId;
    @SerializedName("status")
    private int status;
    @SerializedName("modified")
    private String modified;
    @SerializedName("created")
    private String created;
    @SerializedName("user_banker")
    private ModelBankAccount userBankAccount;

    protected ModelFundRequest(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        userParentId = in.readInt();
        userBankerId = in.readInt();
        amount = in.readInt();
        transactionNumber = in.readString();
        userRemark = in.readString();
        adminRemark = in.readString();
        requestedBy = in.readString();
        transactionId = in.readInt();
        status = in.readInt();
        modified = in.readString();
        created = in.readString();
        userBankAccount = in.readParcelable(ModelBankAccount.class.getClassLoader());
    }

    public static final Creator<ModelFundRequest> CREATOR = new Creator<ModelFundRequest>() {
        @Override
        public ModelFundRequest createFromParcel(Parcel in) {
            return new ModelFundRequest(in);
        }

        @Override
        public ModelFundRequest[] newArray(int size) {
            return new ModelFundRequest[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getUserParentId() {
        return userParentId;
    }

    public int getUserBankerId() {
        return userBankerId;
    }

    public int getAmount() {
        return amount;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public String getUserRemark() {
        return userRemark;
    }

    public String getAdminRemark() {
        return adminRemark;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getStatus() {
        return status;
    }

    public String getModified() {
        return modified;
    }

    public String getCreated() {
        return created;
    }

    public ModelBankAccount getUserBankAccount() {
        return userBankAccount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeInt(userParentId);
        dest.writeInt(userBankerId);
        dest.writeInt(amount);
        dest.writeString(transactionNumber);
        dest.writeString(userRemark);
        dest.writeString(adminRemark);
        dest.writeString(requestedBy);
        dest.writeInt(transactionId);
        dest.writeInt(status);
        dest.writeString(modified);
        dest.writeString(created);
        dest.writeParcelable(userBankAccount, flags);
    }
}
