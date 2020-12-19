package com.atlaaya.evdrecharge.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ModelUserRole implements Parcelable {

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("alias")
    private String alias;
    @SerializedName("level")
    private int level;
    @SerializedName("status")
    private boolean status;
    @SerializedName("updated")
    private String updated;
    @SerializedName("created")
    private String created;

    protected ModelUserRole(Parcel in) {
        id = in.readInt();
        title = in.readString();
        alias = in.readString();
        level = in.readInt();
        status = in.readByte() != 0;
        updated = in.readString();
        created = in.readString();
    }

    public static final Creator<ModelUserRole> CREATOR = new Creator<ModelUserRole>() {
        @Override
        public ModelUserRole createFromParcel(Parcel in) {
            return new ModelUserRole(in);
        }

        @Override
        public ModelUserRole[] newArray(int size) {
            return new ModelUserRole[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAlias() {
        return alias;
    }

    public int getLevel() {
        return level;
    }

    public boolean isStatus() {
        return status;
    }

    public String getUpdated() {
        return updated;
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
        dest.writeString(alias);
        dest.writeInt(level);
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeString(updated);
        dest.writeString(created);
    }
}
