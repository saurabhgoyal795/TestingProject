package com.atlaaya.evdrecharge.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.atlaaya.evdrecharge.enums.EnumPrintAllowed;
import com.atlaaya.evdrecharge.utils.StringUtils;
import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;

public class ModelUserInfo implements Parcelable {


    public static final Creator<ModelUserInfo> CREATOR = new Creator<ModelUserInfo>() {
        @Override
        public ModelUserInfo createFromParcel(Parcel in) {
            return new ModelUserInfo(in);
        }

        @Override
        public ModelUserInfo[] newArray(int size) {
            return new ModelUserInfo[size];
        }
    };
    @SerializedName("id")
    private int id;
    @SerializedName("role_id")
    private int role_id;
    @SerializedName("plan_id")
    private int plan_id;
    @SerializedName("parent_id")
    private int parent_id;
    @SerializedName("first_name")
    private String first_name;
    @SerializedName("last_name")
    private String last_name;
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("langugae")
    private String language;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("image")
    private String image;
    @SerializedName("address")
    private String address;
    @SerializedName("address2")
    private String address2;
    @SerializedName("country_id")
    private int country_id;
    @SerializedName("country_name")
    private String country_name;
    @SerializedName("region_id")
    private int region_id;
    @SerializedName("region_name")
    private String region_name;
    @SerializedName("company_name")
    private String company_name;
    @SerializedName("postcode")
    private String postcode;
    @SerializedName("city")
    private String city;
    @SerializedName("wallet_amount")
    private double wallet_amount;
    @SerializedName("transaction_block_status")
    private int transaction_block_status;
    @SerializedName("status")
    private boolean status;
    @SerializedName("created")
    private String created;
    @SerializedName("modified")
    private String modified;
    @SerializedName("user_balance")
    private double user_balance;
    @SerializedName("effective_balance")
    private double effective_balance;
    @SerializedName("lockfund")
    private int lockfund;
    @SerializedName("terminal_id")
    private int terminal_id;
    @SerializedName("print_allowed")
    private EnumPrintAllowed print_allowed;

    @SerializedName("plan")
    private ModelUserPlan plan;

    @SerializedName("role")
    private ModelUserRole role;

    protected ModelUserInfo(Parcel in) {
        id = in.readInt();
        role_id = in.readInt();
        plan_id = in.readInt();
        parent_id = in.readInt();
        first_name = in.readString();
        last_name = in.readString();
        username = in.readString();
        email = in.readString();
        language = in.readString();
        mobile = in.readString();
        image = in.readString();
        address = in.readString();
        address2 = in.readString();
        country_id = in.readInt();
        country_name = in.readString();
        region_id = in.readInt();
        region_name = in.readString();
        company_name = in.readString();
        postcode = in.readString();
        city = in.readString();
        wallet_amount = in.readDouble();
        transaction_block_status = in.readInt();
        status = in.readByte() != 0;
        created = in.readString();
        modified = in.readString();
        user_balance = in.readDouble();
        effective_balance = in.readDouble();
        lockfund = in.readInt();
        terminal_id = in.readInt();
        plan = in.readParcelable(ModelUserPlan.class.getClassLoader());
        role = in.readParcelable(ModelUserRole.class.getClassLoader());
    }

    public int getId() {
        return id;
    }


    public String getFirstName() {
        if (!Strings.isNullOrEmpty(first_name)) {
            first_name = StringUtils.capitalizeFirstChar(first_name.trim());
        }
        return first_name;
    }

    public String getLastName() {
        if (!Strings.isNullOrEmpty(last_name)) {
            last_name = StringUtils.capitalizeFirstChar(last_name.trim());
        }
        return last_name;
    }

    public String getEmail() {
        return email == null ? "" : email;
    }
    public String getLanguage() {
        return language == null ? "" : language;
    }

    public String getMobile() {
        return mobile == null ? "" : mobile;
    }

    public String getFullName() {
        final StringBuilder sb = new StringBuilder(50);

        if (!Strings.isNullOrEmpty(first_name)) {
            sb.append(StringUtils.capitalizeFirstChar(first_name.trim()));
        }

        if (!Strings.isNullOrEmpty(last_name)) {
            sb.append(" ");
            sb.append(StringUtils.capitalizeFirstChar(last_name.trim()));
        }
        return sb.toString().trim();
    }

    public String getInitials() {
        final StringBuilder sb = new StringBuilder(2);

        if (!Strings.isNullOrEmpty(first_name)) {
            sb.append(first_name.toUpperCase().charAt(0));
        }

        if (!Strings.isNullOrEmpty(last_name)) {
            sb.append(last_name.toUpperCase().charAt(0));
        }

        return sb.toString();
    }

    public String getProfileName() {
        final StringBuilder sb = new StringBuilder(50);
        if (!Strings.isNullOrEmpty(first_name)) {
            sb.append(StringUtils.capitalizeFirstChar(first_name.trim()));
        }
        return sb.toString();
    }

    public int getRole_id() {
        return role_id;
    }

    public int getPlan_id() {
        return plan_id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image == null || image.isEmpty() ? null : image;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress2() {
        return address2;
    }

    public int getCountry_id() {
        return country_id;
    }

    public String getCountry_name() {
        return country_name;
    }

    public int getRegion_id() {
        return region_id;
    }

    public String getRegion_name() {
        return region_name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCity() {
        return city;
    }

    public String getFullAddress() {
        final StringBuilder sb = new StringBuilder(100);
        if (!Strings.isNullOrEmpty(address)) {
            sb.append(StringUtils.capitalizeFirstChar(address.trim()));
        }
        if (!Strings.isNullOrEmpty(address2)) {
            sb.append(" ").append(StringUtils.capitalizeFirstChar(address2.trim()));
        }
        if (!Strings.isNullOrEmpty(city)) {
            sb.append(", ").append(StringUtils.capitalizeFirstChar(city.trim()));
        }
        if (!Strings.isNullOrEmpty(region_name)) {
            sb.append(", ").append(StringUtils.capitalizeFirstChar(region_name.trim()));
        }
        if (!Strings.isNullOrEmpty(country_name)) {
            sb.append(", ").append(StringUtils.capitalizeFirstChar(country_name.trim()));
        }
        if (!Strings.isNullOrEmpty(postcode)) {
            sb.append(" ").append(StringUtils.capitalizeFirstChar(postcode.trim()));
        }
        return sb.toString();
    }

    public double getWallet_amount() {
        return wallet_amount;
    }

    public int getTransaction_block_status() {
        return transaction_block_status;
    }

    public boolean isStatus() {
        return status;
    }

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }

    public double getUser_balance() {
        return user_balance;
    }

    public double getEffective_balance() {
        return effective_balance;
    }

    public int getLockfund() {
        return lockfund;
    }

    public int getTerminal_id() {
        return terminal_id;
    }

    public EnumPrintAllowed getPrint_allowed() {
        return print_allowed;
    }

    public ModelUserPlan getPlan() {
        return plan;
    }

    public ModelUserRole getRole() {
        return role;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(role_id);
        dest.writeInt(plan_id);
        dest.writeInt(parent_id);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(language);
        dest.writeString(mobile);
        dest.writeString(image);
        dest.writeString(address);
        dest.writeString(address2);
        dest.writeInt(country_id);
        dest.writeString(country_name);
        dest.writeInt(region_id);
        dest.writeString(region_name);
        dest.writeString(company_name);
        dest.writeString(postcode);
        dest.writeString(city);
        dest.writeDouble(wallet_amount);
        dest.writeInt(transaction_block_status);
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeString(created);
        dest.writeString(modified);
        dest.writeDouble(user_balance);
        dest.writeDouble(effective_balance);
        dest.writeInt(lockfund);
        dest.writeInt(terminal_id);
        dest.writeParcelable(plan, flags);
        dest.writeParcelable(role, flags);
    }
}
