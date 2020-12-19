package com.atlaaya.evdrecharge.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.atlaaya.evdrecharge.utils.StringUtils;
import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;

public class ModelOperator implements Parcelable {


    public static final Creator<ModelOperator> CREATOR = new Creator<ModelOperator>() {
        @Override
        public ModelOperator createFromParcel(Parcel in) {
            return new ModelOperator(in);
        }

        @Override
        public ModelOperator[] newArray(int size) {
            return new ModelOperator[size];
        }
    };
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("service_id")
    private int service_id;
    @SerializedName("operator_code")
    private String operator_code;
    @SerializedName("min_transaction_amt")
    private int min_transaction_amt;
    @SerializedName("max_transaction_amt")
    private int max_transaction_amt;
    @SerializedName("min_subscriber_len")
    private int min_subscriber_len;
    @SerializedName("max_subscriber_len")
    private int max_subscriber_len;
    @SerializedName("api_id")
    private int api_id;
    @SerializedName("default_discount_type")
    private String default_discount_type;
    @SerializedName("default_commission_type")
    private String default_commission_type;
    @SerializedName("duplicate_transaction_allowed_after_min")
    private int duplicate_transaction_allowed_after_min;
    @SerializedName("icon")
    private String icon;
    @SerializedName("status")
    private boolean status;
    @SerializedName("modified")
    private String modified;
    @SerializedName("created")
    private String created;

    public  ModelOperator(){

    }

    protected ModelOperator(Parcel in) {
        id = in.readInt();
        title = in.readString();
        service_id = in.readInt();
        operator_code = in.readString();
        min_transaction_amt = in.readInt();
        max_transaction_amt = in.readInt();
        min_subscriber_len = in.readInt();
        max_subscriber_len = in.readInt();
        api_id = in.readInt();
        default_discount_type = in.readString();
        default_commission_type = in.readString();
        duplicate_transaction_allowed_after_min = in.readInt();
        icon = in.readString();
        status = in.readByte() != 0;
        modified = in.readString();
        created = in.readString();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        if (!Strings.isNullOrEmpty(title)) {
            title = StringUtils.capitalizeFirstChar(title.trim());
        }
        return title == null ? "" : title;
    }

    public int getService_id() {
        return service_id;
    }

    public String getOperator_code() {
        return operator_code;
    }

    public int getMin_transaction_amt() {
        return min_transaction_amt;
    }

    public int getMax_transaction_amt() {
        return max_transaction_amt;
    }

    public int getMin_subscriber_len() {
        return min_subscriber_len;
    }

    public int getMax_subscriber_len() {
        return max_subscriber_len;
    }

    public int getApi_id() {
        return api_id;
    }

    public String getDefault_discount_type() {
        return default_discount_type;
    }

    public String getDefault_commission_type() {
        return default_commission_type;
    }

    public int getDuplicate_transaction_allowed_after_min() {
        return duplicate_transaction_allowed_after_min;
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
        dest.writeString(title);
        dest.writeInt(service_id);
        dest.writeString(operator_code);
        dest.writeInt(min_transaction_amt);
        dest.writeInt(max_transaction_amt);
        dest.writeInt(min_subscriber_len);
        dest.writeInt(max_subscriber_len);
        dest.writeInt(api_id);
        dest.writeString(default_discount_type);
        dest.writeString(default_commission_type);
        dest.writeInt(duplicate_transaction_allowed_after_min);
        dest.writeString(icon);
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeString(modified);
        dest.writeString(created);
    }
}
