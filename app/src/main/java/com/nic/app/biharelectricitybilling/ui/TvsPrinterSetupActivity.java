package com.nic.app.biharelectricitybilling.ui;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.andprn.port.android.USBPort;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.util.CommonPref;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class TvsPrinterSetupActivity extends AppCompatActivity {
    Context context;
    Spinner sp_pname;
    Button btn_connect;
    UsbManager mUsbManager;
    private USBPort port;
    private PendingIntent mPermissionIntent;
    BluetoothAdapter badapter;
    BluetoothAdapter mBluetoothAdapter;
    String btname = "";
    volatile boolean stopWorker;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    byte[] readBuffer;
    int readBufferPosition;
    Thread workerThread;
    Boolean TODO=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.blue900));
        }
        setContentView(R.layout.activity_tvs_printer_setup);
/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        context = this;
        btn_connect = (Button) findViewById(R.id.btn_connect);
        sp_pname = (Spinner) findViewById(R.id.sp_pname);
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        port = new USBPort(mUsbManager, context);
        this.mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent("com.android.example.USB_PERMISSION"), Intent.FILL_IN_ACTION);
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
     /*   Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {


            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {

                finish();
            }
        });*/

        if (!isbluetoothconnection()) {
            Toast.makeText(getApplicationContext(), "Bluetooth Not Connect", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth Connected", Toast.LENGTH_SHORT).show();
        }

        List<String> list = new ArrayList<String>(loadbtpname());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
        sp_pname.setAdapter(adapter);
        btn_connect.setOnClickListener(v -> {
            if (btn_connect.getTag().toString().trim().equals("0")) {
                if (sp_pname.getCount() > 0) {
                    btname = sp_pname.getSelectedItem().toString();
                    Log.e("btname", btname);
                    new ConnectBT().execute();
                }
            } else {
                try {
                    closeBT();
                    btn_connect.setTag("0");
                    btn_connect.setText("connect");
                    toast("Printer DisConnected");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isbluetoothconnection() {
        try {
            badapter = BluetoothAdapter.getDefaultAdapter();
            if (badapter != null) {
                if (ContextCompat.checkSelfPermission(TvsPrinterSetupActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    {
                        ActivityCompat.requestPermissions(TvsPrinterSetupActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                        return false;
                    }
                }
                badapter.enable();
                if (!badapter.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public List<String> loadbtpname() {
        List<String> list = new ArrayList<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ContextCompat.checkSelfPermission(TvsPrinterSetupActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                ActivityCompat.requestPermissions(TvsPrinterSetupActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return null;
            }
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                list.add(device.getName());
            }
        }

        return list;
    }

    void closeBT() throws IOException {
        try {
            stopWorker = true;
            if (mmOutputStream != null) {
                mmOutputStream.close();
                mmInputStream.close();
                mmSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toast(String msg) {
        Toast toast2 = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast2.setGravity(Gravity.CENTER, 0, 0);
        toast2.show();
    }

    public class ConnectBT extends AsyncTask<Void, Void, String> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TvsPrinterSetupActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Connecting...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                findBT();
                openBT();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (mmSocket != null) {
                if (mmSocket.isConnected()) {
                    btn_connect.setTag("1");
                    btn_connect.setText("disconnect");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("btname", btname);
                    CommonPref.setPrinterMacAddress(TvsPrinterSetupActivity.this, btname, btname);
                    CommonPref.setPrinterType(TvsPrinterSetupActivity.this, "T");
                    startActivity(intent);
                    try {
                        closeBT();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    toast("Printer Not Connect");
                }
            } else {
                toast("Printer Not Connect");
            }
        }
    }

    public void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                toast("No Bluetooth SurveyAdapter Available");
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals(btname)) {
                        mmDevice = device;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openBT() throws IOException {
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

        } catch (Exception e) {
            Log.e("exception", e.getMessage());
            e.printStackTrace();
        }
    }

    public void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()
                        && !stopWorker) {

                    try {

                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0,
                                            encodedBytes, 0,
                                            encodedBytes.length);
                                    final String data = new String(
                                            encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(() -> {
                                        //       myLabel.setText(data);
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }

                    } catch (IOException ex) {
                        stopWorker = true;
                    }

                }
            });

            workerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}