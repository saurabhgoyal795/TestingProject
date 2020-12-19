package com.atlaaya.evdrecharge.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ModelBank implements Parcelable {

    public static final Creator<ModelBank> CREATOR = new Creator<ModelBank>() {
        @Override
        public ModelBank createFromParcel(Parcel in) {
            return new ModelBank(in);
        }

        @Override
        public ModelBank[] newArray(int size) {
            return new ModelBank[size];
        }
    };
    @SerializedName("id")
    private int id;
    @SerializedName("bank_name")
    private String bankName;
    @SerializedName("bank_code")
    private String bankCode;
    @SerializedName("icon")
    private String icon;
    @SerializedName("status")
    private boolean status;
    @SerializedName("modified")
    private String modified;
    @SerializedName("created")
    private String created;

    public ModelBank() {

    }

    protected ModelBank(Parcel in) {
        id = in.readInt();
        bankName = in.readString();
        bankCode = in.readString();
        icon = in.readString();
        status = in.readByte() != 0;
        modified = in.readString();
        created = in.readString();
    }

    public int getId() {
        return id;
    }

    public String getBankName() {
        return bankName == null ? "" : bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getIcon() {
        return icon;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(bankName);
        dest.writeString(bankCode);
        dest.writeString(icon);
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeString(modified);
        dest.writeString(created);
    }
}
