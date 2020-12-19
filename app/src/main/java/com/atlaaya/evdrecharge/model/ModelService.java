package com.atlaaya.evdrecharge.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.atlaaya.evdrecharge.utils.StringUtils;
import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;

public class ModelService implements Parcelable {

    public static final Creator<ModelService> CREATOR = new Creator<ModelService>() {
        @Override
        public ModelService createFromParcel(Parcel in) {
            return new ModelService(in);
        }

        @Override
        public ModelService[] newArray(int size) {
            return new ModelService[size];
        }
    };
    @SerializedName("id")
    private int id;

    @SerializedName("service_name")
    private String service_name;

    public ModelService(){

    }

    protected ModelService(Parcel in) {
        id = in.readInt();
        service_name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(service_name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getService_name() {
        if (!Strings.isNullOrEmpty(service_name)) {
            service_name = StringUtils.capitalizeFirstChar(service_name.trim());
        }
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }
}
