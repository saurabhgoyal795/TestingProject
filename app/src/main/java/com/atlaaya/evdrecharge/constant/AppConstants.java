package com.atlaaya.evdrecharge.constant;

import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.model.ModelVoucher;

public class AppConstants {

    public static String App_TOKEN = "sksRTUUIIKaydkshaks826387623629369";

    public static boolean IS_POS_APK = false;

    public static String KEY_TYPE_VOUCHER = "Voucher";
    public static String KEY_TYPE_AIRTIME = "Airtime";

    public static String getPinPrintableData(ModelVoucher voucher, ModelUserInfo userInfo) {

        String date = voucher.getDate();

        return "<p><strong>" +
                (int)voucher.getVoucher_amount() + " Birr Airtime</strong><br/>" +
                " E-Voucher Pin :<br/>" +
                "--------------------------------<br/>" +
                "<big><font size=\"6\"><strong>" + voucher.getPin_no() + "</strong></font></big><br/>" +
                "--------------------------------<br/>" +
                "E-voucher serial number :<br>\n  " +
                "<strong>" + voucher.getPin_serial_no() + "</strong><br> " +
                "Txn Ref No: " + voucher.getTxn_ref_num() + "<br> " +
                "Date: " + date + "<br> " +
                "To Recharge your mobile :<br>\n  Dial *805*e-voucher pin#<br>" +
                "Agent : " + userInfo.getFullName() + "<br> Powered By Alami Telecom" +
                "</p>";


    }

    public static String getPinNonPrintableData(ModelVoucher voucher, ModelUserInfo userInfo) {

        String date = voucher.getDate();

        return "<p><strong>" +
                (int)voucher.getVoucher_amount() + " Birr Airtime</strong><br/>" +
                " E-Voucher Pin :<br/>" +
                "--------------------------------<br/>" +
                "<big><font size=\"6\"><strong>##PIN##</strong></font></big><br/>" +
                "--------------------------------<br>" +
                "E-voucher serial number :<br>\n  " +
                "<strong>" + voucher.getPin_serial_no() + "</strong><br> " +
                "Txn Ref No: " + voucher.getTxn_ref_num() + "<br> " +
                "Date: " + date + "<br> " +
                "To Recharge your mobile :<br>\n  Dial *805*e-voucher pin#<br>" +
                "Agent : " + userInfo.getFullName() + "<br> Powered By Alami Telecom" +
                "</p>";

    }
    public static String getPinNonPrintableText(ModelVoucher voucher, ModelUserInfo userInfo) {

        String date = voucher.getDate();

        return "<p><b>" +
                (int)voucher.getVoucher_amount() + " Birr Airtime</b><br/>" +
                " E-Voucher Pin :<br/>" +
                "--------------------------------<br/>" +
                "<big><font size=\"6\"><strong>##PIN##</strong></font></big><br/>" +
                "--------------------------------<br>" +
                "E-voucher serial number :<br>\n  " +
                "<strong>" + voucher.getPin_serial_no() + "</strong><br> " +
                "Txn Ref No: " + voucher.getTxn_ref_num() + "<br> " +
                "Date: " + date + "<br> " +
                "To Recharge your mobile :<br>\n  Dial *805*e-voucher pin#<br>" +
                "Agent : " + userInfo.getFullName() + "<br> Powered By Alami Telecom" +
                "</p>";

    }


}
