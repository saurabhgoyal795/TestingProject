package com.atlaaya.evdrecharge.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelVoucherOrder implements Parcelable {


    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("operator_id")
    private int operatorId;
    @SerializedName("total_order_amount")
    private double totalOrderAmount;
    @SerializedName("total_order_quantity")
    private int totalOrderQuantity;
    @SerializedName("transaction_id")
    private int transactionId;
    @SerializedName("transaction_order_id")
    private String transactionOrderId;
    @SerializedName("status")
    private int status;
    @SerializedName("created")
    private String created;
    @SerializedName("modified")
    private String modified;

    @SerializedName("voucher_order_details")
    private List<ModelVoucherPlan> voucherPlanList;


    protected ModelVoucherOrder(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        operatorId = in.readInt();
        totalOrderAmount = in.readDouble();
        totalOrderQuantity = in.readInt();
        transactionId = in.readInt();
        transactionOrderId = in.readString();
        status = in.readInt();
        created = in.readString();
        modified = in.readString();
        voucherPlanList = in.createTypedArrayList(ModelVoucherPlan.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeInt(operatorId);
        dest.writeDouble(totalOrderAmount);
        dest.writeInt(totalOrderQuantity);
        dest.writeInt(transactionId);
        dest.writeString(transactionOrderId);
        dest.writeInt(status);
        dest.writeString(created);
        dest.writeString(modified);
        dest.writeTypedList(voucherPlanList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ModelVoucherOrder> CREATOR = new Creator<ModelVoucherOrder>() {
        @Override
        public ModelVoucherOrder createFromParcel(Parcel in) {
            return new ModelVoucherOrder(in);
        }

        @Override
        public ModelVoucherOrder[] newArray(int size) {
            return new ModelVoucherOrder[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public double getTotalOrderAmount() {
        return totalOrderAmount;
    }

    public int getTotalOrderQuantity() {
        return totalOrderQuantity;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getTransactionOrderId() {
        return transactionOrderId;
    }

    public int getStatus() {
        return status;
    }

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }

    public List<ModelVoucherPlan> getVoucherPlanList() {
        return voucherPlanList;
    }
}
