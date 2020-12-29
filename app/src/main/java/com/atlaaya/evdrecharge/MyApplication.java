package com.atlaaya.evdrecharge;

import androidx.multidex.MultiDexApplication;

import com.atlaaya.evdrecharge.api.APIInterface;
import com.atlaaya.evdrecharge.api.RestService;
import com.atlaaya.evdrecharge.firebase.references.DocumentRefrence;
import com.atlaaya.evdrecharge.model.ModelUserInfo;
import com.atlaaya.evdrecharge.storage.SessionManager;
import com.atlaaya.evdrecharge.utils.SunmiPrintHelper;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;


public class MyApplication extends MultiDexApplication {

    private static MyApplication mInstance;
    private APIInterface service;

    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration listenerRegistration;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
    public static String BASE_URL = "https://demo77.mallxs.com/evdlive/webservices/"; // Live

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        installTls12();

        service = RestService.createRetrofitService(APIInterface.class, MyApplication.BASE_URL);

//        final FirebaseDatabase instance = FirebaseDatabase.getInstance();
//        instance.setPersistenceEnabled(true);
//        final DatabaseReference databaseReference = instance.getReference();
//        databaseReference.keepSynced(true);

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings.Builder builder = new FirebaseFirestoreSettings.Builder();
        builder.setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED);
        builder.setPersistenceEnabled(true);
//        builder.setSslEnabled(false);
        firebaseFirestore.setFirestoreSettings(builder.build());

//        FirebaseApp firebaseApp =  firebaseFirestore.getApp();
//        firebaseApp.setDataCollectionDefaultEnabled(true);
//        firebaseApp.setAutomaticResourceManagementEnabled(true);

        enableVoucherSync();

    }

    public APIInterface getAPIInterface() {
        return service;
    }

    public void setAPIInterface(APIInterface service) {
        this.service = service;
    }

    public void enableVoucherSync() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        ModelUserInfo userInfo = SessionManager.getUserDetail(this);
        if (userInfo != null) {
            listenerRegistration = DocumentRefrence.vouchers(firebaseFirestore, userInfo.getId()).addSnapshotListener((queryDocumentSnapshots, e) -> {
            });
        }
    }

    private void installTls12() {
        try {
            ProviderInstaller.installIfNeeded(this);
        /*    try {
                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                sslContext.createSSLEngine();
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }*/

        } catch (GooglePlayServicesRepairableException exception) {
            // Prompt the user to install/update/enable Google Play services.
            GoogleApiAvailability.getInstance()
                    .showErrorNotification(this, exception.getConnectionStatusCode());
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates a non-recoverable error: let the user know.
        }
    }

}
