package com.atlaaya.evdrecharge.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ModelBankAccount implements Parcelable {

    public static final Creator<ModelBankAccount> CREATOR = new Creator<ModelBankAccount>() {
        @Override
        public ModelBankAccount createFromParcel(Parcel in) {
            return new ModelBankAccount(in);
        }

        @Override
        public ModelBankAccount[] newArray(int size) {
            return new ModelBankAccount[size];
        }
    };
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("bank_id")
    private int bankId;
    @SerializedName("account_name")
    private String accountName;
    @SerializedName("account_number")
    private String accountNumber;
    @SerializedName("ifsc")
    private String ifsc;
    @SerializedName("branch_name")
    private String branchName;
    @SerializedName("admin_verify_status")
    private boolean adminVerifyStatus;
    @SerializedName("modified")
    private String modified;
    @SerializedName("created")
    private String created;
    @SerializedName("bank")
    private ModelBank bank;

    protected ModelBankAccount(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        bankId = in.readInt();
        accountName = in.readString();
        accountNumber = in.readString();
        ifsc = in.readString();
        branchName = in.readString();
        adminVerifyStatus = in.readByte() != 0;
        modified = in.readString();
        created = in.readString();
        bank = in.readParcelable(ModelBank.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getBankId() {
        return bankId;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getIfsc() {
        return ifsc;
    }

    public String getBranchName() {
        return branchName;
    }

    public boolean isAdminVerifyStatus() {
        return adminVerifyStatus;
    }

    public String getModified() {
        return modified;
    }

    public String getCreated() {
        return created;
    }

    public ModelBank getBank() {
        return bank == null ? new ModelBank() : bank;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeInt(bankId);
        dest.writeString(accountName);
        dest.writeString(accountNumber);
        dest.writeString(ifsc);
        dest.writeString(branchName);
        dest.writeByte((byte) (adminVerifyStatus ? 1 : 0));
        dest.writeString(modified);
        dest.writeString(created);
        dest.writeParcelable(bank, flags);
    }
}
