package com.atlaaya.evdrecharge.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ModelVoucherPlan implements Parcelable {

    @SerializedName("id")
    private int id;
    @SerializedName("operator_id")
    private int operator_id;
    @SerializedName("amount")
    private double amount;
    @SerializedName("status")
    private boolean status;
    @SerializedName("modified")
    private String modified;
    @SerializedName("created")
    private String created;

    @SerializedName("pin_group_no")
    private String pin_group_no;

    @SerializedName("pin_serial_no")
    private String pin_serial_no;

    @SerializedName("pin_no")
    private String pin_no;

    @SerializedName("pin_expiry_date")
    private String pin_expiry_date;

    @SerializedName("voucher_status")
    private int voucher_status;

    @SerializedName("type")
    private int type;

    @SerializedName("operator")
    private ModelOperator operator;

    @SerializedName("quantity")
    private int selectedQty;

    protected ModelVoucherPlan(Parcel in) {
        id = in.readInt();
        operator_id = in.readInt();
        amount = in.readDouble();
        status = in.readByte() != 0;
        modified = in.readString();
        created = in.readString();
        pin_group_no = in.readString();
        pin_serial_no = in.readString();
        pin_no = in.readString();
        pin_expiry_date = in.readString();
        voucher_status = in.readInt();
        type = in.readInt();
        operator = in.readParcelable(ModelOperator.class.getClassLoader());
        selectedQty = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(operator_id);
        dest.writeDouble(amount);
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeString(modified);
        dest.writeString(created);
        dest.writeString(pin_group_no);
        dest.writeString(pin_serial_no);
        dest.writeString(pin_no);
        dest.writeString(pin_expiry_date);
        dest.writeInt(voucher_status);
        dest.writeInt(type);
        dest.writeParcelable(operator, flags);
        dest.writeInt(selectedQty);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ModelVoucherPlan> CREATOR = new Creator<ModelVoucherPlan>() {
        @Override
        public ModelVoucherPlan createFromParcel(Parcel in) {
            return new ModelVoucherPlan(in);
        }

        @Override
        public ModelVoucherPlan[] newArray(int size) {
            return new ModelVoucherPlan[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getOperator_id() {
        return operator_id;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isStatus() {
        return status;
    }

    public String getModified() {
        return modified;
    }

    public String getCreated() {
        return created;
    }

    public String getPin_group_no() {
        return pin_group_no;
    }

    public String getPin_serial_no() {
        return pin_serial_no;
    }

    public String getPin_no() {
        return pin_no;
    }

    public String getPin_expiry_date() {
        return pin_expiry_date;
    }

    public int getVoucher_status() {
        return voucher_status;
    }

    public int getType() {
        return type;
    }

    public ModelOperator getOperator() {
        return operator;
    }

    public int getSelectedQty() {
//        return selectedQty == 0 ? 1 : selectedQty;
        return selectedQty;
    }

    public void setSelectedQty(int selectedQty) {
        this.selectedQty = selectedQty;
    }
}
