package com.atlaaya.evdrecharge.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ModelBalance implements Parcelable {

    public static final Creator<ModelBalance> CREATOR = new Creator<ModelBalance>() {
        @Override
        public ModelBalance createFromParcel(Parcel in) {
            return new ModelBalance(in);
        }

        @Override
        public ModelBalance[] newArray(int size) {
            return new ModelBalance[size];
        }
    };
    @SerializedName("total_balance")
    private double total_balance;

    @SerializedName("effective_balance")
    private double effective_balance;

    @SerializedName("lockfund")
    private double lockfund;


    protected ModelBalance(Parcel in) {
        total_balance = in.readDouble();
        effective_balance = in.readDouble();
        lockfund = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(total_balance);
        dest.writeDouble(effective_balance);
        dest.writeDouble(lockfund);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public double getTotal_balance() {
        return total_balance;
    }

    public double getEffective_balance() {
        return effective_balance;
    }

    public double getLockfund() {
        return lockfund;
    }
}
