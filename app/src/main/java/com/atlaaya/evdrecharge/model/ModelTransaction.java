package com.atlaaya.evdrecharge.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ModelTransaction implements Parcelable {

    public static final Creator<ModelTransaction> CREATOR = new Creator<ModelTransaction>() {
        @Override
        public ModelTransaction createFromParcel(Parcel in) {
            return new ModelTransaction(in);
        }

        @Override
        public ModelTransaction[] newArray(int size) {
            return new ModelTransaction[size];
        }
    };
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("user_parent_id")
    private int userParentId;
    @SerializedName("wallet_id")
    private int walletId;
    @SerializedName("operator_id")
    private int operatorId;
    @SerializedName("api_id")
    private int apiId;
    @SerializedName("maintype")
    private int maintype;
    @SerializedName("type")
    private int type;
    @SerializedName("mrp")
    private double mrp;
    @SerializedName("transaction_amount")
    private double transactionAmount;
    @SerializedName("topup_sms_number")
    private String topupSmsNumber;
    @SerializedName("api_request_number")
    private String apiRequestNumber;
    @SerializedName("api_transaction_number")
    private String apiTransactionNumber;
    @SerializedName("operator_transaction_number")
    private String operatorTransactionNumber;
    @SerializedName("transaction_status")
    private int transactionStatus;
    @SerializedName("other1")
    private String other1;
    @SerializedName("other2")
    private String other2;
    @SerializedName("other3")
    private String other3;
    @SerializedName("remark")
    private String remark;
    @SerializedName("is_manual_updated")
    private boolean isManualUpdated;
    @SerializedName("modeid")
    private int modeid;
    @SerializedName("modified")
    private String modified;
    @SerializedName("created")
    private String created;
    @SerializedName("operator")
    private ModelOperator operator;

    protected ModelTransaction(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        userParentId = in.readInt();
        walletId = in.readInt();
        operatorId = in.readInt();
        apiId = in.readInt();
        maintype = in.readInt();
        type = in.readInt();
        mrp = in.readDouble();
        transactionAmount = in.readDouble();
        topupSmsNumber = in.readString();
        apiRequestNumber = in.readString();
        apiTransactionNumber = in.readString();
        operatorTransactionNumber = in.readString();
        transactionStatus = in.readInt();
        other1 = in.readString();
        other2 = in.readString();
        other3 = in.readString();
        remark = in.readString();
        isManualUpdated = in.readByte() != 0;
        modeid = in.readInt();
        modified = in.readString();
        created = in.readString();
        operator = in.readParcelable(ModelOperator.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getUserParentId() {
        return userParentId;
    }

    public int getWalletId() {
        return walletId;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public int getApiId() {
        return apiId;
    }

    public int getMaintype() {
        return maintype;
    }

    public int getType() {
        return type;
    }

    public double getMrp() {
        return mrp;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public String getTopupSmsNumber() {
        return topupSmsNumber;
    }

    public String getApiRequestNumber() {
        return apiRequestNumber;
    }

    public String getApiTransactionNumber() {
        return apiTransactionNumber;
    }

    public String getOperatorTransactionNumber() {
        return operatorTransactionNumber;
    }

    public int getTransactionStatus() {
        return transactionStatus;
    }

    public String getOther1() {
        return other1;
    }

    public String getOther2() {
        return other2;
    }

    public String getOther3() {
        return other3;
    }

    public String getRemark() {
        return remark;
    }

    public boolean isIsManualUpdated() {
        return isManualUpdated;
    }

    public int getModeid() {
        return modeid;
    }

    public String getModified() {
        return modified;
    }

    public String getCreated() {
        return created;
    }

    public ModelOperator getOperator() {
        return operator == null ? new ModelOperator() : operator;
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
        dest.writeInt(walletId);
        dest.writeInt(operatorId);
        dest.writeInt(apiId);
        dest.writeInt(maintype);
        dest.writeInt(type);
        dest.writeDouble(mrp);
        dest.writeDouble(transactionAmount);
        dest.writeString(topupSmsNumber);
        dest.writeString(apiRequestNumber);
        dest.writeString(apiTransactionNumber);
        dest.writeString(operatorTransactionNumber);
        dest.writeInt(transactionStatus);
        dest.writeString(other1);
        dest.writeString(other2);
        dest.writeString(other3);
        dest.writeString(remark);
        dest.writeByte((byte) (isManualUpdated ? 1 : 0));
        dest.writeInt(modeid);
        dest.writeString(modified);
        dest.writeString(created);
        dest.writeParcelable(operator, flags);
    }
}
