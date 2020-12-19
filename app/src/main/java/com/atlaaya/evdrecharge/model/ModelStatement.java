package com.atlaaya.evdrecharge.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ModelStatement implements Parcelable {

    public static final Creator<ModelStatement> CREATOR = new Creator<ModelStatement>() {
        @Override
        public ModelStatement createFromParcel(Parcel in) {
            return new ModelStatement(in);
        }

        @Override
        public ModelStatement[] newArray(int size) {
            return new ModelStatement[size];
        }
    };
    @SerializedName("id")
    private int id;
    @SerializedName("transaction_id")
    private int transactionId;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("user_parent_id")
    private int userParentId;
    @SerializedName("wallet_id")
    private int walletId;
    @SerializedName("maintype")
    private int maintype;
    @SerializedName("opening")
    private double opening;
    @SerializedName("type")
    private int type;
    @SerializedName("transaction_amount")
    private double transactionAmount;
    @SerializedName("clossing")
    private double clossing;
    @SerializedName("api_url")
    private String apiUrl;
    @SerializedName("api_headers")
    private String apiHeaders;
    @SerializedName("api_request_body")
    private String apiRequestBody;
    @SerializedName("api_response")
    private String apiResponse;
    @SerializedName("modified")
    private String modified;
    @SerializedName("created")
    private String created;
    @SerializedName("user")
    private ModelUserInfo userInfo;

    protected ModelStatement(Parcel in) {
        id = in.readInt();
        transactionId = in.readInt();
        userId = in.readInt();
        userParentId = in.readInt();
        walletId = in.readInt();
        maintype = in.readInt();
        opening = in.readDouble();
        type = in.readInt();
        transactionAmount = in.readDouble();
        clossing = in.readDouble();
        apiUrl = in.readString();
        apiHeaders = in.readString();
        apiRequestBody = in.readString();
        apiResponse = in.readString();
        modified = in.readString();
        created = in.readString();
        userInfo = in.readParcelable(ModelUserInfo.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public int getUserParentId() {
        return userParentId;
    }

    public int getWalletId() {
        return walletId;
    }

    public int getMaintype() {
        return maintype;
    }

    public double getOpening() {
        return opening;
    }

    public int getType() {
        return type;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public double getClossing() {
        return clossing;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getApiHeaders() {
        return apiHeaders;
    }

    public String getApiRequestBody() {
        return apiRequestBody;
    }

    public String getApiResponse() {
        return apiResponse;
    }

    public String getModified() {
        return modified;
    }

    public String getCreated() {
        return created;
    }

    public ModelUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(transactionId);
        dest.writeInt(userId);
        dest.writeInt(userParentId);
        dest.writeInt(walletId);
        dest.writeInt(maintype);
        dest.writeDouble(opening);
        dest.writeInt(type);
        dest.writeDouble(transactionAmount);
        dest.writeDouble(clossing);
        dest.writeString(apiUrl);
        dest.writeString(apiHeaders);
        dest.writeString(apiRequestBody);
        dest.writeString(apiResponse);
        dest.writeString(modified);
        dest.writeString(created);
        dest.writeParcelable(userInfo, flags);
    }
}