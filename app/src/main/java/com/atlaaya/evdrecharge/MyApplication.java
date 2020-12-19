package com.atlaaya.evdrecharge;

import androidx.multidex.MultiDexApplication;

import com.atlaaya.evdrecharge.api.APIInterface;
import com.atlaaya.evdrecharge.api.RestService;
import com.atlaaya.evdrecharge.utils.SunmiPrintHelper;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;


public class MyApplication extends MultiDexApplication {

    private static MyApplication mInstance;
    private APIInterface service;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        installTls12();
        init();
        service = RestService.createRetrofitService(APIInterface.class, APIInterface.BASE_URL);
    }
    private void init(){
        SunmiPrintHelper.getInstance().initSunmiPrinterService(this);
    }
    public APIInterface getAPIInterface() {
        return service;
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
