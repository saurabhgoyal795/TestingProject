package com.atlaaya.evdrecharge.utils;

import java.text.DecimalFormat;
import java.util.Locale;

public class PriceFormat {

    public static String decimalTwoDigit1(double input) {
//        DecimalFormat df2 = new DecimalFormat("#.##");
//        return df2.format(input);
        return String.format(Locale.getDefault(), "%.2f", input);
    }

    public static String decimalTwoDigit(double value) {
        DecimalFormat formatter = new DecimalFormat("#.##");
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        return formatter.format(value);
    }

    public static String pointOneDouble(double value) {
        DecimalFormat formatter = new DecimalFormat("#.#");
        formatter.setMaximumFractionDigits(1);
        formatter.setMinimumFractionDigits(1);
        return formatter.format(value);
    }

}
