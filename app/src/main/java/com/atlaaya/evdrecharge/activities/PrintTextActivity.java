package com.atlaaya.evdrecharge.activities;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.print.PrintHelper;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.databinding.ActivityPrintTestBinding;
import com.atlaaya.evdrecharge.usbprinter.PrintOrder;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;

public class PrintTextActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static Boolean forceCLaim = true;
    HashMap<String, UsbDevice> mDeviceList;
    Iterator<UsbDevice> mDeviceIterator;
    int protocol;
    PrintOrder printer;
    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbInterface mInterface;
    private UsbDeviceConnection mConnection;
    private UsbEndpoint mEndPoint;

    // Broadcast receiver to obtain permission from user for connection
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            // call method to set up device communication
                            mInterface = device.getInterface(0);
                            mEndPoint = mInterface.getEndpoint(0);
                            mConnection = mUsbManager.openDevice(device);

                            startPrinting(device);

                            // setup();
                        }
                    } else {
                        // Log.d("SUB", "permission denied for device " + device);
                        Toast.makeText(context, "PERMISSION DENIED FOR THIS DEVICE", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };
    private PendingIntent mPermissionIntent;
    private ActivityPrintTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_print_test);

//        printer = new PrintOrder();

//        initialiseUSBDevices();

        binding.btnLogin.setOnClickListener(this);

    }

    private void initialiseUSBDevices() {
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mDeviceList = mUsbManager.getDeviceList();
        mDeviceIterator = mDeviceList.values().iterator();

        String usbDevice = "";
        // This is just testing what devices are connected
        while (mDeviceIterator.hasNext()) {
            UsbDevice usbDevice1 = mDeviceIterator.next();

//            for(int i = 0; i < usbDevice1.getInterfaceCount(); i++){
//               UsbInterface _interface = usbDevice1.getInterface(i);
//                if(_interface.getInterfaceClass() == UsbConstants.USB_CLASS_PRINTER){

            usbDevice += "\n" + "DeviceID: " + usbDevice1.getDeviceId()
                    + "\n" + "DeviceName: " + usbDevice1.getDeviceName()
                    + "\n" + "DeviceClass: " + usbDevice1.getDeviceClass() + " - "
                    + translateDeviceClass(usbDevice1.getDeviceClass())
                    + "\n" + "DeviceSubClass: " + usbDevice1.getDeviceSubclass()
                    + "\n" + "VendorID: " + usbDevice1.getVendorId()
                    + "\n" + "ProductID: " + usbDevice1.getProductId()
                    + "\n";

            protocol = usbDevice1.getDeviceProtocol();

            int interfaceCount = usbDevice1.getInterfaceCount();
            Toast.makeText(this, "INTERFACE COUNT: " + interfaceCount, Toast.LENGTH_SHORT).show();

            mDevice = usbDevice1;

            if (mDevice == null) {
                Toast.makeText(this, "mDevice is null", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(this, "mDevice is not null", Toast.LENGTH_SHORT).show();
            }
            binding.textView.setText(usbDevice);

//                    break;
//                }
//            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogin) {

//            Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
//            startActivity(intent);

            /*String msg = "This is a test message";
            printer.Print(this,msg);*/

/*            mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            registerReceiver(mUsbReceiver, filter);
            if (mDevice != null)
                mUsbManager.requestPermission(mDevice, mPermissionIntent);
            else
                Toast.makeText(this, "USB mDevice is null", Toast.LENGTH_SHORT).show();*/


//            doPhotoPrint();

        }
    }

    public void onClickPrint() {
        String textToPrint = "Your text here";
        try {

            Intent intent = new Intent("pe.diegoveloper.printing");
            //intent.setAction(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, textToPrint);
            startActivityForResult(intent, 2000);

        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=pe.diegoveloper.printerserverapp"));
            startActivity(intent);
        }
    }

    private void doPhotoPrint() {
        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.logo_main);
        photoPrinter.printBitmap("droids.jpg - test print", bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2000) {
            if (resultCode == RESULT_OK) {
                //Printing is ok
            } else {
                if (data != null) {
                    String errorMessage = data.getStringExtra("errorMessage");
                    //Printing with error
                }
            }
        }
    }

    private String translateDeviceClass(int deviceClass) {
        switch (deviceClass) {
            case UsbConstants.USB_CLASS_APP_SPEC:
                return "Application specific USB class";
            case UsbConstants.USB_CLASS_AUDIO:
                return "USB class for audio devices";
            case UsbConstants.USB_CLASS_CDC_DATA:
                return "USB class for CDC devices (communications device class)";
            case UsbConstants.USB_CLASS_COMM:
                return "USB class for communication devices";
            case UsbConstants.USB_CLASS_CONTENT_SEC:
                return "USB class for content security devices";
            case UsbConstants.USB_CLASS_CSCID:
                return "USB class for content smart card devices";
            case UsbConstants.USB_CLASS_HID:
                return "USB class for human interface devices (for example, mice and keyboards)";
            case UsbConstants.USB_CLASS_HUB:
                return "USB class for USB hubs";
            case UsbConstants.USB_CLASS_MASS_STORAGE:
                return "USB class for mass storage devices";
            case UsbConstants.USB_CLASS_MISC:
                return "USB class for wireless miscellaneous devices";
            case UsbConstants.USB_CLASS_PER_INTERFACE:
                return "USB class indicating that the class is determined on a per-interface basis";
            case UsbConstants.USB_CLASS_PHYSICA:
                return "USB class for physical devices";
            case UsbConstants.USB_CLASS_PRINTER:
                return "USB class for printers";
            case UsbConstants.USB_CLASS_STILL_IMAGE:
                return "USB class for still image devices (digital cameras)";
            case UsbConstants.USB_CLASS_VENDOR_SPEC:
                return "Vendor specific USB class";
            case UsbConstants.USB_CLASS_VIDEO:
                return "USB class for video devices";
            case UsbConstants.USB_CLASS_WIRELESS_CONTROLLER:
                return "USB class for wireless controller devices";
            default:
                return "Unknown USB class!";
        }
    }

    public void startPrinting(final UsbDevice printerDevice) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            UsbDeviceConnection conn;
            UsbInterface usbInterface;

            @Override
            public void run() {
                try {
                    Log.i("Info", "Bulk transfer started");
                    // usbInterface = printerDevice.getInterface(0);

                    for (int i = 0; i < printerDevice.getInterfaceCount(); i++) {
                        usbInterface = printerDevice.getInterface(i);

                        if (usbInterface.getInterfaceClass() == UsbConstants.USB_CLASS_PRINTER) {
                            // usbInterface = mDevice;
                        }
                    }

                    UsbEndpoint endPoint = usbInterface.getEndpoint(0);
                    conn = mUsbManager.openDevice(mDevice);
                    conn.claimInterface(usbInterface, true);

                    String myStringData = "TEXT";
                    myStringData += "\n";
                    byte[] array = myStringData.getBytes();
                    ByteBuffer output_buffer = ByteBuffer.allocate(array.length);

                    UsbRequest request = new UsbRequest();
                    request.initialize(conn, endPoint);
                    request.queue(output_buffer, array.length);
                    if (conn.requestWait() == request) {
                        Log.i("Info", output_buffer.getChar(0) + "");
                        Message m = new Message();
                        m.obj = output_buffer.array();
                        output_buffer.clear();
                    } else {
                        Log.i("Info", "No request recieved");
                    }
                    int transfered = conn.bulkTransfer(endPoint, myStringData.getBytes(), myStringData.getBytes().length, 5000);
                    Log.i("Info", "Amount of data transferred : " + transfered);

                } catch (Exception e) {
                    Log.e("Exception", "Unable to transfer bulk data");
                    e.printStackTrace();
                } finally {
                    try {
                        conn.releaseInterface(usbInterface);
                        Log.i("Info", "Interface released");
                        conn.close();
                        Log.i("Info", "Usb connection closed");
                        unregisterReceiver(mUsbReceiver);
                        Log.i("Info", "Brodcast reciever unregistered");
                    } catch (Exception e) {
                        Log.e("Exception", "Unable to release resources because : " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
