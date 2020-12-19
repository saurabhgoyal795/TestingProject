package com.atlaaya.evdrecharge.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ModelVoucherPurchased implements Parcelable {

    @SerializedName("printable_text")
    private String printable_text;

    @SerializedName("non_printable_text")
    private String non_printable_text;

    @SerializedName("saleid")
    private String saleid;

    @SerializedName("vchr_odr_id")
    private String vchr_odr_id;

    @SerializedName("voucher")
    private ModelVoucher voucher;

    public ModelVoucherPurchased() {

    }

    protected ModelVoucherPurchased(Parcel in) {
        printable_text = in.readString();
        non_printable_text = in.readString();
        saleid = in.readString();
        vchr_odr_id = in.readString();
        voucher = in.readParcelable(ModelVoucher.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(printable_text);
        dest.writeString(non_printable_text);
        dest.writeString(saleid);
        dest.writeString(vchr_odr_id);
        dest.writeParcelable(voucher, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ModelVoucherPurchased> CREATOR = new Creator<ModelVoucherPurchased>() {
        @Override
        public ModelVoucherPurchased createFromParcel(Parcel in) {
            return new ModelVoucherPurchased(in);
        }

        @Override
        public ModelVoucherPurchased[] newArray(int size) {
            return new ModelVoucherPurchased[size];
        }
    };

    public String getPrintable_text() {
        return printable_text;
    }

    public String getNon_printable_text() {
        return non_printable_text;
    }

    public String getSaleid() {
        return saleid;
    }

    public String getVchr_odr_id() {
        return vchr_odr_id;
    }

    public ModelVoucher getVoucher() {
        return voucher;
    }

}