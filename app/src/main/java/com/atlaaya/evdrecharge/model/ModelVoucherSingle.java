package com.atlaaya.evdrecharge.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ModelVoucherSingle implements Parcelable {

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

    public ModelVoucherSingle(){

    }

    protected ModelVoucherSingle(Parcel in) {
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

    public static final Creator<ModelVoucherSingle> CREATOR = new Creator<ModelVoucherSingle>() {
        @Override
        public ModelVoucherSingle createFromParcel(Parcel in) {
            return new ModelVoucherSingle(in);
        }

        @Override
        public ModelVoucherSingle[] newArray(int size) {
            return new ModelVoucherSingle[size];
        }
    };

    public String getPrintable_text() {
        return printable_text;
    }

    public void setPrintable_text(String printable_text) {
        this.printable_text = printable_text;
    }

    public String getNon_printable_text() {
        return non_printable_text;
    }

    public void setNon_printable_text(String non_printable_text) {
        this.non_printable_text = non_printable_text;
    }

    public String getSaleid() {
        return saleid;
    }

    public void setSaleid(String saleid) {
        this.saleid = saleid;
    }

    public String getVchr_odr_id() {
        return vchr_odr_id;
    }

    public void setVchr_odr_id(String vchr_odr_id) {
        this.vchr_odr_id = vchr_odr_id;
    }

    public ModelVoucher getVoucher() {
        return voucher;
    }

    public void setVoucher(ModelVoucher voucher) {
        this.voucher = voucher;
    }

}
