package com.atlaaya.evdrecharge.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;

public class ModelVoucher implements Parcelable {

    @SerializedName("date")
    private String date;
    @SerializedName("is_print")
    private int is_print;
    @SerializedName("operator_id")
    private int operator_id;
    @SerializedName("service_id")
    private int service_id;
    @SerializedName("order_id")
    private String order_id;
    @SerializedName("pin_group_no")
    private String pin_group_no;
    @SerializedName("pin_no")
    private String pin_no;
    @SerializedName("pin_serial_no")
    private String pin_serial_no;
    @SerializedName("txn_ref_num")
    private String txn_ref_num;
    @SerializedName("transaction_id")
    private long transaction_id;
    @SerializedName("user_id")
    private long user_id;
    @SerializedName("voucher_amount")
    private float voucher_amount;
    @SerializedName("voucher_mrp")
    private float voucher_mrp;
    @SerializedName("voucher_id")
    private long voucher_id;


    public ModelVoucher(){

    }

    protected ModelVoucher(Parcel in) {
        date = in.readString();
        is_print = in.readInt();
        operator_id = in.readInt();
        service_id = in.readInt();
        order_id = in.readString();
        pin_group_no = in.readString();
        pin_no = in.readString();
        pin_serial_no = in.readString();
        txn_ref_num = in.readString();
        transaction_id = in.readLong();
        user_id = in.readLong();
        voucher_amount = in.readFloat();
        voucher_mrp = in.readFloat();
        voucher_id = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeInt(is_print);
        dest.writeInt(operator_id);
        dest.writeInt(service_id);
        dest.writeString(order_id);
        dest.writeString(pin_group_no);
        dest.writeString(pin_no);
        dest.writeString(pin_serial_no);
        dest.writeString(txn_ref_num);
        dest.writeLong(transaction_id);
        dest.writeLong(user_id);
        dest.writeFloat(voucher_amount);
        dest.writeFloat(voucher_mrp);
        dest.writeLong(voucher_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ModelVoucher> CREATOR = new Creator<ModelVoucher>() {
        @Override
        public ModelVoucher createFromParcel(Parcel in) {
            return new ModelVoucher(in);
        }

        @Override
        public ModelVoucher[] newArray(int size) {
            return new ModelVoucher[size];
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIs_print() {
        return is_print;
    }

    public void setIs_print(int is_print) {
        this.is_print = is_print;
    }

    public int getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(int operator_id) {
        this.operator_id = operator_id;
    }

    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPin_group_no() {
        return pin_group_no;
    }

    public void setPin_group_no(String pin_group_no) {
        this.pin_group_no = pin_group_no;
    }

    public String getPin_no() {
        if(!Strings.isNullOrEmpty(pin_no)){
            pin_no = pin_no.replaceAll(" ", "");
            StringBuilder sb = new StringBuilder();
            return sb.append(pin_no.substring(0,4)).append(" ")
                    .append(pin_no.substring(4,8)).append(" ").append(pin_no.substring(8)).toString();
        }
        return pin_no;
    }

    public void setPin_no(String pin_no) {
        if(Strings.isNullOrEmpty(pin_no)){
            pin_no = pin_no.replaceAll(" ", "");
        }
        this.pin_no = pin_no;
    }

    public String getPin_serial_no() {
        return pin_serial_no;
    }

    public void setPin_serial_no(String pin_serial_no) {
        this.pin_serial_no = pin_serial_no;
    }

    public String getTxn_ref_num() {
        return txn_ref_num;
    }

    public void setTxn_ref_num(String txn_ref_num) {
        this.txn_ref_num = txn_ref_num;
    }

    public long getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(long transaction_id) {
        this.transaction_id = transaction_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public float getVoucher_amount() {
        return voucher_amount;
    }

    public void setVoucher_amount(float voucher_amount) {
        this.voucher_amount = voucher_amount;
    }

    public float getVoucher_mrp() {
        return voucher_mrp;
    }

    public void setVoucher_mrp(float voucher_mrp) {
        this.voucher_mrp = voucher_mrp;
    }

    public long getVoucher_id() {
        return voucher_id;
    }

    public void setVoucher_id(long voucher_id) {
        this.voucher_id = voucher_id;
    }
}
