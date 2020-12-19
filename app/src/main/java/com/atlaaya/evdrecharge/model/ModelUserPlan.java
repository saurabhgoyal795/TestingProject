package com.atlaaya.evdrecharge.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.atlaaya.evdrecharge.utils.StringUtils;
import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;

public class ModelUserPlan implements Parcelable {

    @SerializedName("id")
    private int id;
    @SerializedName("role_id")
    private int role_id;
    @SerializedName("plan_name")
    private String plan_name;
    @SerializedName("plan_unique_code")
    private String plan_unique_code;
    @SerializedName("registration_fee")
    private int registration_fee;
    @SerializedName("lock_load_amount")
    private int lock_load_amount;
    @SerializedName("sponser_income_amount")
    private int sponser_income_amount;
    @SerializedName("free_bonus_amount")
    private int free_bonus_amount;
    @SerializedName("status")
    private boolean status;
    @SerializedName("modified")
    private String modified;
    @SerializedName("created")
    private String created;

    protected ModelUserPlan(Parcel in) {
        id = in.readInt();
        role_id = in.readInt();
        plan_name = in.readString();
        plan_unique_code = in.readString();
        registration_fee = in.readInt();
        lock_load_amount = in.readInt();
        sponser_income_amount = in.readInt();
        free_bonus_amount = in.readInt();
        status = in.readByte() != 0;
        modified = in.readString();
        created = in.readString();
    }

    public static final Creator<ModelUserPlan> CREATOR = new Creator<ModelUserPlan>() {
        @Override
        public ModelUserPlan createFromParcel(Parcel in) {
            return new ModelUserPlan(in);
        }

        @Override
        public ModelUserPlan[] newArray(int size) {
            return new ModelUserPlan[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getRole_id() {
        return role_id;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public String getPlan_unique_code() {
        return plan_unique_code;
    }

    public int getRegistration_fee() {
        return registration_fee;
    }

    public int getLock_load_amount() {
        return lock_load_amount;
    }

    public int getSponser_income_amount() {
        return sponser_income_amount;
    }

    public int getFree_bonus_amount() {
        return free_bonus_amount;
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
        dest.writeInt(role_id);
        dest.writeString(plan_name);
        dest.writeString(plan_unique_code);
        dest.writeInt(registration_fee);
        dest.writeInt(lock_load_amount);
        dest.writeInt(sponser_income_amount);
        dest.writeInt(free_bonus_amount);
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeString(modified);
        dest.writeString(created);
    }
}
