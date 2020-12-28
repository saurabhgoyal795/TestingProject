package com.atlaaya.evdrecharge.firebase.references;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DocumentRefrence {
    private static final String PRINT_FORMAT = "ptext";
    private static final String VOUCHERS = "vouchers";

    public static DocumentReference printFormat(final FirebaseFirestore firebaseFirestore) {
        return firebaseFirestore.collection(PRINT_FORMAT).document("print_text");
    }

    public static CollectionReference updateVouchers(final FirebaseFirestore firebaseFirestore, final int userId) {
        return firebaseFirestore.collection(VOUCHERS);
    }

    public static Query vouchers(final FirebaseFirestore firebaseFirestore, final int userId) {
        return firebaseFirestore.collection(VOUCHERS).whereEqualTo("user_id", userId);
    }

    public static Query vouchersNonPrinted(final FirebaseFirestore firebaseFirestore, final int userId) {
        return firebaseFirestore.collection(VOUCHERS).whereEqualTo("user_id", userId)
                .whereEqualTo("is_print", 0);
    }

    public static Query vouchersPrinted(final FirebaseFirestore firebaseFirestore, final int userId) {
        return firebaseFirestore.collection(VOUCHERS).whereEqualTo("user_id", userId)
                .whereEqualTo("is_print", 1);
    }

}
