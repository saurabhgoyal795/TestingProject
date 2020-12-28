package com.atlaaya.evdrecharge.firebase.references;

import com.google.firebase.database.DatabaseReference;

public class Database {
    public static final String PRINT_FORMAT = "ptext";
    public static final String VOUCHERS_NON_PRINT = "vouchers_nonprint";

    public static DatabaseReference printFormat(final DatabaseReference databaseReference) {
        return databaseReference.child(PRINT_FORMAT);
    }

    public static DatabaseReference voucherNonPrints(final DatabaseReference databaseReference, final String userId) {
        return databaseReference.child(VOUCHERS_NON_PRINT).child(userId);
    }




}
