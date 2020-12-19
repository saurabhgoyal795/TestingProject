package com.atlaaya.evdrecharge.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelFilterConfig {

    @SerializedName("system_name")
    private String systemName;
    @SerializedName("admin_email")
    private String adminEmail;
    @SerializedName("contactus_email")
    private String contactusEmail;
    @SerializedName("support_email")
    private String supportEmail;
    @SerializedName("system_phone")
    private String systemPhone;
    @SerializedName("system_address")
    private String systemAddress;
    @SerializedName("mobile_lenght")
    private String mobileLenght;
    @SerializedName("system_currency")
    private String systemCurrency;
    @SerializedName("system_base_url")
    private String systemBaseUrl;
    @SerializedName("social_url")
    private SocialUrlBean socialUrl;
    @SerializedName("filters")
    private FiltersBean filters;

    public String getSystemName() {
        return systemName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public String getContactusEmail() {
        return contactusEmail;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public String getSystemPhone() {
        return systemPhone;
    }

    public String getSystemAddress() {
        return systemAddress;
    }

    public String getMobileLenght() {
        return mobileLenght;
    }

    public String getSystemCurrency() {
        return systemCurrency;
    }

    public String getSystemBaseUrl() {
        return systemBaseUrl;
    }

    public SocialUrlBean getSocialUrl() {
        return socialUrl;
    }

    public FiltersBean getFilters() {
        return filters;
    }

    public static class SocialUrlBean  {

        @SerializedName("facebook_url")
        private String facebookUrl;
        @SerializedName("twitter")
        private String twitter;
        @SerializedName("google_url")
        private String googleUrl;
        @SerializedName("cyprus_url")
        private String cyprusUrl;

        public String getFacebookUrl() {
            return facebookUrl;
        }

        public String getTwitter() {
            return twitter;
        }

        public String getGoogleUrl() {
            return googleUrl;
        }

        public String getCyprusUrl() {
            return cyprusUrl;
        }
    }

    public static class FiltersBean {

        @SerializedName("voucher_request_status")
        private VoucherRequestStatusBean voucherRequestStatus;
        @SerializedName("load_request_status")
        private LoadRequestStatusBean loadRequestStatus;
        @SerializedName("voucher_status")
        private VoucherStatusBean voucherStatus;
        @SerializedName("normal_status")
        private List<String> normalStatus;
        @SerializedName("transaction_maintype")
        private List<String> transactionMaintype;
        @SerializedName("transaction_status")
        private List<String> transactionStatus;
        @SerializedName("transaction_type")
        private List<String> transactionType;
        @SerializedName("commission_status")
        private List<String> commissionStatus;

        public VoucherRequestStatusBean getVoucherRequestStatus() {
            return voucherRequestStatus;
        }

        public LoadRequestStatusBean getLoadRequestStatus() {
            return loadRequestStatus;
        }

        public VoucherStatusBean getVoucherStatus() {
            return voucherStatus;
        }

        public List<String> getNormalStatus() {
            return normalStatus==null?new ArrayList<>():normalStatus;
        }

        public List<String> getTransactionMaintype() {
            return transactionMaintype==null?new ArrayList<>():transactionMaintype;
        }


        public List<String> getTransactionStatus() {
            return transactionStatus==null?new ArrayList<>():transactionStatus;
        }

        public List<String> getTransactionType() {
            return transactionType==null?new ArrayList<>():transactionType;
        }

        public List<String> getCommissionStatus() {
            return commissionStatus==null?new ArrayList<>():commissionStatus;
        }

        public static class VoucherRequestStatusBean{

            @SerializedName("1")
            private String $1;
            @SerializedName("2")
            private String $2;
            @SerializedName("3")
            private String $3;

            public String get$1() {
                return $1;
            }

            public String get$2() {
                return $2;
            }

            public String get$3() {
                return $3;
            }
        }

        public static class LoadRequestStatusBean {

            @SerializedName("1")
            private String $1;
            @SerializedName("2")
            private String $2;
            @SerializedName("3")
            private String $3;
            @SerializedName("4")
            private String $4;

            public String get$1() {
                return $1;
            }

            public String get$2() {
                return $2;
            }

            public String get$3() {
                return $3;
            }

            public String get$4() {
                return $4;
            }
        }

        public static class VoucherStatusBean  {

            @SerializedName("1")
            private String $1;
            @SerializedName("2")
            private String $2;
            @SerializedName("3")
            private String $3;
            @SerializedName("4")
            private String $4;

            public String get$1() {
                return $1;
            }

            public String get$2() {
                return $2;
            }


            public String get$3() {
                return $3;
            }


            public String get$4() {
                return $4;
            }

        }
    }
}
