package com.nic.app.biharelectricitybilling.ui;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.util.Size;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.analogics.thermalAPI.Bluetooth_Printer_2inch_prof_ThermalAPI;
import com.analogics.thermalprinter.AnalogicsThermalPrinter;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.epson.eposprint.Builder;
import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.Tvsprinter.Activity_DeviceList;
import com.nic.app.biharelectricitybilling.Tvsprinter.SharedPrefClass;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.entity.BillDetails;
import com.nic.app.biharelectricitybilling.entity.ConsumerAdapter;
import com.nic.app.biharelectricitybilling.entity.MRUDetails;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.GlobalVariables;
import com.nic.app.biharelectricitybilling.util.Utiilties;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import HPRTAndroidSDK.HPRTPrinterHelper;
import jpos.JposConst;
import jpos.POSPrinterConst;
import print.ShowMsg;

public class PrintHomeActivity extends Activity implements ReceiveListener {
    DataBaseHelper localDBHelper;
    ConsumerAdapter adapter;
    ArrayList<MRUDetails> mruList;
    ArrayList<String> accList;
    ListView dataList;
    BillDetails bill;
    long c1;
    Button Printall;
    Button Search;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    Bitmap bmp_full, bmp_print;
    DecimalFormat df = new DecimalFormat("0.00");
    AutoCompleteTextView accountno;
    private Printer mPrinter = null;
    private Activity mContext = null;
    static String imagesDir = Environment.getExternalStorageDirectory() + "";
    String capturedImgSave = "";
    protected static final int CAMERA_PIC_REQUEST = 1337;
    protected static final int CAMERA_IMAGE_CAPTUE_OK = -1;
    boolean multiLanguagePrint = false, createdBitmap = false;
    static final UUID MY_UUID = UUID.randomUUID();
    protected String btAddressDir = Environment.getExternalStorageDirectory()
            + "";
    BluetoothAdapter bluetoothAdapter;
    String address = null;
    AnalogicsThermalPrinter conn = new AnalogicsThermalPrinter();
    BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 99;
    private static final int REQUEST_ENABLE_BT1 = 100;
    private static final int REQUEST_ENABLE_BT2 = 200;
    TextView txttotal;
    String acNo = "";
    int c = 0;
    MRUDetails mrudetails = null;

    BluetoothDevice con_dev = null;
    String CROP_IMAGE_FILE_PATH = null;
    File myDir = null;

    volatile boolean stopWorker;
    int readBufferPosition;
    byte[] readBuffer;
    Thread workerThread;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    SharedPrefClass session;
    BluetoothAdapter btAdapt;
    String btDev_str;
    public static String toothAddress = null;
    private ProgressDialog pd;
    private Message message;
    private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_home);

        // Database Opening
        localDBHelper = new DataBaseHelper(PrintHomeActivity.this);
        localDBHelper = new DataBaseHelper(this);
        try {
            localDBHelper.createDataBase();

        } catch (IOException ioe) {
            Utiilties.writeIntoLog(Log.getStackTraceString(ioe));
            throw new Error("Unable to create database");

        }

        try {

            localDBHelper.openDataBase();

        } catch (SQLException sqle) {
            Utiilties.writeIntoLog(Log.getStackTraceString(sqle));
            throw sqle;

        }

        // -------------------------------------------------------------------------------------

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
        // For displaying title and subtitle and change text color
        actionBar.setTitle(Html
                .fromHtml("<font color='#FFFFFF'>List of Generated Bill </font>"));
        mContext = PrintHomeActivity.this;
        c = 0;
        accountno = (AutoCompleteTextView) findViewById(R.id.et_accountno);
        Search = (Button) findViewById(R.id.bt_search);
        Printall = (Button) findViewById(R.id.btn_print);
        txttotal = (TextView) findViewById(R.id.total);
        dataList = (ListView) findViewById(R.id.listConsumer);
        mruList = localDBHelper.getMRU("Is_Pending_Bill", "G");
        int total = mruList.size();

      /*  CROP_IMAGE_FILE_PATH = "/BEB/CropImage/"
                + CommonPref.getUserDetails(PrintHomeActivity.this)
                .get_MRUNo() + "/";
        String root = null;

        if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.Q) {
            root = getApplicationContext().getExternalFilesDir(CROP_IMAGE_FILE_PATH).toString();

            myDir = new File(root);
        } else {
            root = Environment.getExternalStorageDirectory().toString();
            myDir = new File(root + "/" + CROP_IMAGE_FILE_PATH);
        }*/

        txttotal.setText(txttotal.getText().toString() + " : " + total);
        if (mruList.size() > 0) {
            accList = new ArrayList<>();
            for (MRUDetails mru : mruList) {
                accList.add(mru.get_ACT_NO());
            }
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, accList);
        accountno.setThreshold(0);
        accountno.setAdapter(adapter);
        loadMruList();
   /*     if (CommonPref.getPrinterType(PrintHomeActivity.this).equalsIgnoreCase("T")) {
            session = new SharedPrefClass(PrintHomeActivity.this);
            btAdapt = BluetoothAdapter.getDefaultAdapter();
            gotoNext();
        }*/
        Search.setOnClickListener(view -> {
            String acc_no = accountno.getText().toString();
            if (acc_no.matches("")) {
                mruList = localDBHelper.getMRU("Is_Pending_Bill", "G");
                loadMruList();
            } else {
                mruList = localDBHelper.getMRUByAccNO("ACT_NO", acc_no);
                if (mruList.size() > 0) {
                    loadMruList();
                }
            }
        });
        dataList.setOnItemClickListener((parent, view, position, id) -> {
            // TODO Auto-generated method stub

            // ListView Clicked item value
            acNo = ((TextView) view.findViewById(R.id.tv_ac_no))
                    .getText().toString();
            c++;
            bill = localDBHelper.getBillDetails("ACT_NO", acNo);
            mrudetails = mruList.get(position);
          /*  if(bill.get_CATEGORY().equalsIgnoreCase("IAS2")){
                showdialog();
            }*/
            if (bill.get_Go_Green().equalsIgnoreCase("Y")) {
                showdialoggogreen();
            } else {
                if (!CommonPref.getPrinterType(mContext).equals("N")) {
                    //bluetooth();
                    showPrintLanguageDialog();
                } else {
                    Toast.makeText(mContext, "Please select Printer fisrt", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void loadMruList() {
        try {
            adapter = new ConsumerAdapter(PrintHomeActivity.this,
                    R.layout.list_row_consumer, mruList);
            dataList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception ex) {
            Utiilties.writeIntoLog(Log.getStackTraceString(ex));
            Toast.makeText(PrintHomeActivity.this, "Error in loading",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean runPrintReceiptSequence(Boolean isOnline,boolean isHindi) {
        if (!initializeObject()) {
            return false;
        }
        if (isHindi) {
            if (isOnline) {
                if (!createReceiptDatahindi()) {
                    finalizeObject();
                    return false;
                }
            } else {
                if (!createReceiptDataOfflineHIN()) {
                    finalizeObject();
                    return false;
                }
            }
        }else{
            if (isOnline) {
                if (!createReceiptData()) {
                    finalizeObject();
                    return false;
                }
            } else {
                if (!createReceiptDataOffline()) {
                    finalizeObject();
                    return false;
                }
            }
        }
        if (!printData()) {
            finalizeObject();
            return false;
        }

        return true;
    }

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(Printer.TM_P60, Printer.MODEL_ANK,
                    PrintHomeActivity.this);
            mPrinter.addCommand(new byte[]{0x1C, 0x28, 0x43, 0x02, 0x00, 0x30, 0x02});
        } catch (Exception e) {
            ShowMsg.showException(e, "Printer", mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return false;
        }

        mPrinter.setReceiveEventListener(this);

        return true;
    }


    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                //     ShowMsg.showResult(code, makeErrorMessage(status), mContext);

                if (makeErrorMessage(status).isEmpty() && code == 0) {
                    long c = localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
                    if (c > 0) {
                        Toast.makeText(PrintHomeActivity.this,
                                "Update in Local DataBase",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PrintHomeActivity.this,
                                "Error in Local Database",
                                Toast.LENGTH_LONG).show();
                    }
                }

                dispPrinterWarnings(status);


                new Thread(() -> disconnectPrinter()).start();
            }
        });
    }

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += getString(R.string.handlingmsg_err_autocutter);
            msg += getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }

    private void dispPrinterWarnings(PrinterStatusInfo status) {
        // EditText edtWarnings = (EditText) findViewById(R.id.edtWarnings);
        String warningsMsg = "";

        if (status == null) {
            return;
        }

        if (status.getPaper() == Printer.PAPER_NEAR_END) {
            warningsMsg += getString(R.string.handlingmsg_warn_receipt_near_end);
        }

        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
            warningsMsg += getString(R.string.handlingmsg_warn_battery_near_end);
        }

        // edtWarnings.setText(warningsMsg);
        Log.e("Printer Warning", warningsMsg);
    }


    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "endTransaction", mContext);
                    Utiilties.writeIntoLog(Log.getStackTraceString(e));
                }
            });
        }

        try {
            mPrinter.disconnect();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "disconnect", mContext);
                    Utiilties.writeIntoLog(Log.getStackTraceString(e));
                }
            });
        }

        finalizeObject();
    }


    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }

    private boolean printData() {
        if (mPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();

        dispPrinterWarnings(status);

        if (!isPrintable(status)) {
            ShowMsg.showMsg(makeErrorMessage(status), mContext);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                //  Utiilties.writeIntoLog(Log.getStackTraceString(ex));
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, "sendData", mContext);
            //   Utiilties.writeIntoLog(Log.getStackTraceString(e));
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                //  Utiilties.writeIntoLog(Log.getStackTraceString(ex));
                // Do nothing
            }
            return false;
        }

        return true;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;
        if (mPrinter == null) {
            return false;
        }
        try {
            mPrinter.connect(CommonPref.getPrinterMacAddress(mContext), Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, "connect", mContext);
            //  Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        } catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", mContext);
            // Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }

        if (!isBeginTransaction) {
            try {
                mPrinter.disconnect();
            } catch (Epos2Exception e) {
                // Do nothing
                Utiilties.writeIntoLog(Log.getStackTraceString(e));
                return false;
            }
        }
        return true;
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }
        if (status.getConnection() == Printer.FALSE) {
            return false;
        } else if (status.getOnline() == Printer.FALSE) {
            return false;
        } else {
            ;//print available
        }

        return true;
    }
    private void bluetooth(boolean language) {
        // TODO Auto-generated method stub
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
                Toast.makeText(PrintHomeActivity.this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(PrintHomeActivity.this, "Bluetooth about to start.", Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
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
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {

                bill = localDBHelper.getBillDetails("ACT_NO", acNo);
                if (bill != null) {
                    Toast.makeText(PrintHomeActivity.this,
                                    "Please Wait \nPrinting....", Toast.LENGTH_LONG)
                            .show();
                    if (bill.get_AMBI_Flag().toString().trim().equals("R")) {
                        if (CommonPref.getPrinterType(PrintHomeActivity.this).equals("E")) {
                            runPrintReceiptSequence(true,language);
                        } else if (CommonPref.getPrinterType(PrintHomeActivity.this).equals("A")) {
                            CheckBlueToothState(true);
                            File f = new File(btAddressDir + "/BTaddress.txt");
                            if (f.exists()) {
                                try {
                                    FileInputStream fstream = new FileInputStream(btAddressDir
                                            + "/BTaddress.txt");
                                    DataInputStream in = new DataInputStream(fstream);
                                    BufferedReader br = new BufferedReader(
                                            new InputStreamReader(in));
                                    String strLine;
                                    while ((strLine = br.readLine()) != null) {
                                        address = strLine;
                                    }

                                    in.close();
                                    open(true);
                                } catch (Exception e) {// Catch exception if any
                                    System.err.println("Error: " + e.getMessage());
                                    Utiilties.writeIntoLog(Log.getStackTraceString(e));

                                }

                            } else {

                                Intent intent = new Intent(PrintHomeActivity.this,
                                        AnalogicsPrinterSetup.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);

                                // finish();

                            }

                        } else if (CommonPref.getPrinterType(PrintHomeActivity.this).equals("T")) {
                            openfortvs(true,language);
                        }
                    } else {
                        if (CommonPref.getPrinterType(PrintHomeActivity.this).equals("E")) {
                            runPrintReceiptSequence(false,language);
                        } else if (CommonPref.getPrinterType(PrintHomeActivity.this).equals("T")) {
                            openfortvs(false,language);
                        } else if (CommonPref.getPrinterType(PrintHomeActivity.this).equals("A")) {
                            CheckBlueToothState(false);
                            File f = new File(btAddressDir + "/BTaddress.txt");

                            if (f.exists()) {

                                try {
                                    FileInputStream fstream = new FileInputStream(btAddressDir
                                            + "/BTaddress.txt");
                                    DataInputStream in = new DataInputStream(fstream);
                                    BufferedReader br = new BufferedReader(
                                            new InputStreamReader(in));
                                    String strLine;
                                    while ((strLine = br.readLine()) != null) {
                                        address = strLine;
                                    }
                                    in.close();
                                    open(false);
                                } catch (Exception e) {// Catch exception if any
                                    System.err.println("Error: " + e.getMessage());
                                    Utiilties.writeIntoLog(Log.getStackTraceString(e));

                                }

                            } else {

                                Intent intent = new Intent(PrintHomeActivity.this,
                                        AnalogicsPrinterSetup.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);

                                // finish();

                            }
                        }
                    }
                } else {
                    Toast.makeText(PrintHomeActivity.this,
                                    "Error in Local Database", Toast.LENGTH_LONG)
                            .show();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
    }
    private boolean createReceiptData() {
        String method = "";
        String gstno = "";
        // Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
        // Get our saved file into a bitmap object:
        Bitmap logoData = null;
        Uri file = Uri.parse(mrudetails.get_Meter_Photo());
        if (!Uri.EMPTY.equals(file)) {
            try {
                //    logoData = this.getContentResolver().loadThumbnail(file, new Size(350, 220), null);
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                    logoData = mContext.getContentResolver().loadThumbnail(file, new Size(200, 200), null);
                    //  azadi=mContext.getContentResolver().loadThumbnail(azadiuri, new Size(350, 220), null);
                } else {
                    logoData = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), file);
                    //   azadi = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), azadiuri);
                }
                //   Log.e("Image",logoData.toString());

                int x = logoData.getWidth();
                if (x > 220) {
                    logoData = Bitmap.createScaledBitmap(logoData, 350, 220, true);
                    //    azadi = Bitmap.createScaledBitmap(azadi, 350, 220, true);
                } else {
                    logoData = Bitmap.createScaledBitmap(logoData, 350, 220, true);
                    //   azadi = Bitmap.createScaledBitmap(azadi, 350, 220, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        StringBuilder textData = new StringBuilder();
        if (mPrinter == null) {
            return false;
        }
        try {
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addTextSize";
            mPrinter.addTextSize(2, 2);
            method = "addText";
            String header = "Energy Bill";
           double billAmt=Double.parseDouble(bill.get_SubTotalB().trim())-Double.parseDouble(bill.get_DPS_CURR().trim());
            if((bill.get_CATEGORY().startsWith("DS")||bill.get_CATEGORY().startsWith("KJ")) && Double.parseDouble(bill.get_PROMPT_AMT().trim())<=0 && billAmt<=0) {
                String zerobill = "\"ZERO BILL\"";
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addText(zerobill + "\n\n");
            }
            mPrinter.addTextFont(Printer.FONT_B);
            mPrinter.addTextSize(2, 1);
            mPrinter.addText(header + "\n");
            mPrinter.addText(bill.get_COMPANY().trim() + "\n");
            Bitmap azadi = BitmapFactory.decodeResource(getResources(), R.drawable.cm_bw1);
            if (azadi != null) {
                azadi = Bitmap.createScaledBitmap(azadi, 300, 250,  true);
                method = "addTextAlign";
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                method = "addImage";
                mPrinter.addImage(azadi, 0, 0, azadi.getWidth(), azadi.getHeight(),
                        Printer.COLOR_1,
                        Printer.MODE_MONO,
                        Printer.HALFTONE_DITHER,
                        Printer.PARAM_DEFAULT,
                        Printer.COMPRESS_AUTO);
            }
            textData.append("\nसभी घरेलू उपभोक्ताओं से अब 125 यूनिट तक\nबिजली खपत पर कोई शुल्क नहीं लिया जाएगा।\nयह लाभ जुलाई माह की खपत से लागू है।\n");
            mPrinter.addTextSize(1, 2);
            mPrinter.addTextFont(Builder.FONT_C);
            mPrinter.addTextAlign(Builder.ALIGN_LEFT);
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            mPrinter.addTextFont(Builder.FONT_C);
            mPrinter.addTextAlign(Builder.ALIGN_RIGHT);
            mPrinter.addCommand("-नीतीश कुमार,मुख्यमंत्री बिहार\n".getBytes("UTF-8"));
            mPrinter.addTextAlign(Builder.ALIGN_CENTER);
            mPrinter.addCommand("-------------------------------\n".getBytes("UTF-8"));
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextFont(Builder.FONT_C);
            mPrinter.addTextSize(2, 2);
            mPrinter.addText("\nSTATE GOV SUBSIDY\n");
            mPrinter.addTextSize(2, 1);
            mPrinter.addText("\n"+bill.get_GOV_SUBSIDY().trim()+"\n");
            textData.delete(0, textData.length());
            String header1 = CommonPref.getUserDetails(mContext).get_DivId().substring(0, 1);
            if (header1.equals("1")) {
                gstno = "10AAECN1588M2ZB";
            } else if (header1.equals("2")) {
                gstno = "10AASCS2207G2ZN";
            }
            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            mPrinter.addTextFont(Printer.FONT_B);
            textData.append("***********************************\n");
            textData.append("GSTIN-" + gstno + "\n");
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            //mPrinter.addText("OFFER" + "\n");
            //mPrinter.addText("#*#*#*#" + "\n");
            if (Float.parseFloat(bill.get_PROMPT_AMT().trim()) > 0) {
                method = "addTextSize";
                mPrinter.addTextSize(2, 1);
                method = "addText";
                String strRebateOnline = bill.get_ONLINE_REBATE().toString().trim();
                mPrinter.addText(" Get Extra Rs" + strRebateOnline + "\n" + " rebate if Rs" + bill.get_PROMPT_AMT().trim() + "\n" + " paid online" + "\n" + "upto " + bill.get_UPTO_DATE().trim() + "\n");
            }
            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            //mPrinter.addTextFont(Printer.FONT_B);
            //textData.append("Get Extra Rs." +strRebateOnline+ " rebate if Rs."+ bill.get_PROMPT_AMT().trim() +" paid"+"\n"+"online upto " + bill.get_UPTO_DATE().trim()  + "\n" );
            //textData.append("***********************************\n");
            textData.append("ELECTRICITY BILL  : " + monthYear(bill.get_BMONTH().trim(), bill.get_BYEAR().trim()) + "\n");
            textData.append("******************************\n");
            if (Float.parseFloat(bill.get_SubTotalA().trim()) > 1000) {
                //textData.append("प्रिय " + bill.get_CNAME().trim() + ",\n" + "कृपया विदयुत बकाया राशि रू" + bill.get_PROMPT_AMT().trim() + " का\nभुगतान सुचना प्राप्ति के 15 दिनों के\nभीतर सुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के आलोक में दि." + bill.get_UPTO_DATE().trim() + " के पश्चात विदयुत\nसम्बन्ध विच्छेदित कर दिया जाएगा।" + "\n" + "                 स0 वि0 अभि0" + "\n");
                textData.append("******************************\n");
            }
            textData.append("DATE: " + bill.get_BILL_DT().trim() + "     TIME: " + bill.get_BILL_TIME().trim() + "\n");
            textData.append("CONSUMER DETAILS\n");
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("BILL NO             :    " + bill.get_BILL_NO().trim() + "\n");
            textData.append("DIVISION            :    " + bill.get_DIV_NAME().trim() + "\n");
            textData.append("SUBDIVISION         :    " + bill.get_SUB_DIV_NAME().trim() + "\n");
            textData.append("CON ID              :    " + bill.get_CON_ID().trim() + "\n");
            textData.append("A/C NUMBER          :    " + bill.get_ACT_NO().trim() + "\n");
            textData.append("MRU                 :    " + CommonPref.getUserDetails(PrintHomeActivity.this).get_MRUNo() + "\n");
            textData.append("NAME          :  " + bill.get_CNAME().trim() + "\n");
            textData.append("ADDRESS       :  ");
            textData.append(bill.get_BILL_ADDRESS().trim() + "\n");
            textData.append("AREA TYPE     :  " + bill.get_AREA_TYPE().trim() + "\n");
            textData.append("POLE NO       :  " + bill.get_POLE_NO().trim() + "\n");
            textData.append("METER NO      :  " + bill.get_METER_NO().trim() + "       PH : " + bill.get_PHASE().trim() + "\n");
            textData.append("MTR OWNER     :  " + bill.get_MET_OWNER().trim() + "\n");
            textData.append("CATEGORY      :  " + bill.get_CATEGORY().trim() + "\n");
            textData.append("SL : " + bill.get_SANC_LOAD().trim() + "    CL:  " + bill.get_CONC_LOAD().trim() + "       CD:  " + bill.get_CON_DEM().trim() + "\n");
            textData.append("SD            : " + bill.get_SEC_DEP().trim() + "\n");
            textData.append("BILLED DAYS   :  " + bill.get_BILLED_MONTH().trim() + "\n");
            textData.append("     ***********************************\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            textData.append("READING DETAILS\n");
            textData.append("---------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("              PREVIOUS    CURRENT  \n");
            textData.append("READING :         " + bill.get_PREV_READ().trim() + "         " + bill.get_CUR_READ().trim() + "\n");
            textData.append("DATE    :    " + bill.get_PRE_READ_DATE().trim() + "  " + bill.get_BILL_DT().trim() + "\n");
            textData.append("STATUS  :       " + bill.get_PRE_READ_STAT().trim() + "           " + bill.get_READ_STAT().trim() + "   \n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            if (logoData != null) {
                method = "addTextAlign";
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                method = "addImage";
                mPrinter.addImage(logoData, 0, 0, logoData.getWidth(), logoData.getHeight(),
                        Printer.COLOR_1,
                        Printer.MODE_MONO,
                        Printer.HALFTONE_DITHER,
                        Printer.PARAM_DEFAULT,
                        Printer.COMPRESS_AUTO);
            }
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("\n");
            //float consumption = (Float.parseFloat(bill.get_CUR_READ().trim()) - Float.parseFloat(bill.get_PREV_READ().trim())) *Float.parseFloat(bill.get_MULTI_FACT().trim());
            textData.append("OLD CONSUMPTION :  " + bill.get_OLD_CONSUMPTION().trim() + "\n");
            textData.append("MULTIPLYING FACTOR :  " + bill.get_MULTI_FACT().trim()+ "\n");
            textData.append("CONSUMPTION : " + bill.get_UNITS_CONS() + "\n");
            textData.append("RECORDED DEMAND : " + bill.get_REC_DEMAND().trim() + "\n");
            textData.append("POWER FACTOR  : " + bill.get_POW_FACT().trim() + "\n");
            textData.append("MMC UNITS       : " + bill.get_MMC_UNIT().trim() + "         AVG : " + bill.get_AVG_UNIT().trim() + "\n");
            textData.append("BLD UNITS       : " + bill.get_BILLED_UNIT().trim() + "       TYPE: " + bill.get_BILL_TYPE().trim() + "\n");
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            //float consumption = (Float.parseFloat(bill.get_CUR_READ().trim()) - Float.parseFloat(bill.get_PREV_READ().trim())) *Float.parseFloat(bill.get_MULTI_FACT().trim());
            if (!bill.get_KEPT_AMOUNT().equalsIgnoreCase("NA")) {
                textData.append("KEPT AMOUNT   : " + "Rs." + bill.get_KEPT_AMOUNT() + "\n");
            }
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText("ARREAR DETAILS\n");
            mPrinter.addText("---------------------------\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("ENERGY DUES             :" + addBlankToString(bill.get_EC_ARREAR().trim()) + "\n");
            textData.append("ARREAR DPS              :" + addBlankToString(bill.get_DPS_ARREAR().trim()) + "\n");
            textData.append("ARREAR ED               :" + addBlankToString(bill.get_EDAREAR().trim()) + "\n");
            textData.append("OTHERS                  :" + addBlankToString(bill.get_OTH_ARREAR().trim()) + "\n");
            //float totalA = Float.parseFloat(convertBlankToZero(bill.get_EC_ARREAR())) + Float.parseFloat(convertBlankToZero(bill.get_DPS_ARREAR())) + Float.parseFloat(convertBlankToZero(bill.get_OTH_ARREAR()));
            textData.append("SUB TOTAL (A)           :" + addBlankToString(bill.get_SubTotalA().trim()) + "\n");
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            textData.append("CURRENT BILL DETAILS\n");
            textData.append("---------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            textData.append("ENERGY CHARGES          :" + addBlankToString(bill.get_EC_CURR().trim()) + "\n");
            textData.append("FIXED/DEMD CHG          :" + addBlankToString(bill.get_FIX_CURR().trim()) + "\n");
            textData.append("EXCESS DEMD CHG         :" + addBlankToString(bill.get_EXC_DEM_CURR().trim()) + "\n");
            textData.append("ELECTRICITY DUTY        :" + addBlankToString(bill.get_ED_CURRENT().trim()) + "\n");
            //textData.append("METER RENT              :" + addBlankToString(bill.get_MET_RENT().trim()) + "\n");
            //textData.append("CGST on MR @ 9%         :" + addBlankToString(bill.get_CGST_AMT().trim()) + "\n");
            //textData.append("SGST on MR @ 9%         :" + addBlankToString(bill.get_SGCT_AMT().trim()) + "\n");
            textData.append("SHUNT CAP. CHG          :" + addBlankToString(bill.get_SHUN_CURR().trim()) + "\n");
            textData.append("OTHERS CHG              :" + addBlankToString(bill.get_OTH_CURR().trim()) + "\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0,textData.length());
            textData.append("---------------------------");
            textData.append("\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText(textData.toString());
            textData.delete(0,textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            //textData.append("STATE GOV SUBSIDY       :" + addBlankToString(bill.get_GOV_SUBSIDY().trim()) + "\n");
            textData.append("STATE GOV SUBSIDY       :");
            textData.append(addBlankToString(bill.get_GOV_SUBSIDY().trim()));
            textData.append("\n");
            mPrinter.addText(textData.toString());
            textData.delete(0,textData.length());
            textData.append("---------------------------\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText(textData.toString());
            textData.delete(0,textData.length());
            mPrinter.addTextSize(2,2);
            mPrinter.addTextFont(Printer.FONT_C);
            textData.append("Current Bill Amount");
            textData.append("\n");
            textData.append(String.format("%.2f",billAmt));
            textData.append("\n");
            mPrinter.addText(textData.toString());
            textData.delete(0,textData.length());
            textData.append("***********************************\n");
            if(!(bill.get_CATEGORY().startsWith("DS")||bill.get_CATEGORY().startsWith("KJ"))) {
                textData.append("DPS                     :" + addBlankToString(bill.get_DPS_CURR().trim()) + "\n");
                textData.append("INTEREST ON SD(C)       :" + addBlankToString(bill.get_INTR_SEC_DEP().trim()) + "\n");
                textData.append("INCENTIVE               :" + addBlankToString(bill.get_INCENTIVE().trim()) + "\n");
                textData.append("REBATES ON MMC          :" + addBlankToString(bill.get_REBATE_ON_MMC().trim()) + "\n");
            }
            //float total = Float.parseFloat(convertBlankToZero(bill.get_INTR_SEC_DEP())) + Float.parseFloat(convertBlankToZero(bill.get_INCENTIVE())) + Float.parseFloat(convertBlankToZero(bill.get_REBATE_ON_MMC())) + totalA + totalB ;
            textData.append("EXTRA QUARTERLY REBATE  :" + addBlankToString1("-"+bill.getQUARTERLY_REBATE_POSTED().trim()) + "\n");
            textData.append("TOTAL (A+B+C)           :" + addBlankToString(bill.get_SubTotalC().trim()) + "\n");
            textData.append("***********************************\n");
            textData.append("REBATES                 :" + addBlankToString(bill.get_REBATE().trim()) + "\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mPrinter.addTextFont(Printer.FONT_B);
            mPrinter.addTextSize(1,1);
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            textData.append("AMOUNT PAYABLE\n");
            textData.append("---------------------------\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("UPTO        " + bill.get_UPTO_DATE().trim() + "  :" + addBlankToString(bill.get_PROMPT_AMT().trim()) + "\n");
            textData.append("BY          " + bill.get_BY_DATE().trim() + "  :" + addBlankToString(bill.get_GROSS_AMT().trim()) + "\n");
            textData.append("AFTER       " + bill.get_AFTER_DATE().trim() + "  :" + addBlankToString(bill.get_NET_AMT().trim()) + "\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            if (!bill.get_KEPT_AMOUNT().equalsIgnoreCase("NA")) {
                method = "addTextAlign";
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addText("---------------------------\n");
                method = "addTextAlign";
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append("INSTALMENT AMOUNT ( C ):" + bill.get_KEPT_INST_FLAG() + " " + bill.get_KEPT_INST_AMT().trim() + "\n");
                textData.append("AMOUNT PAYABLE   ( B+C ) : " + bill.get_KEPT_PAY_AMT() + "\n");
                method = "addText";
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                method = "addTextAlign";
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addText("---------------------------\n");
            }
            textData.append("***********************************\n");
            textData.append("DETAILS OF LAST PAYMENT \n");
            textData.append("***********************************\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("LAST PAID AMOUNT        :    " + bill.get_LAST_PAID_AMT().trim() + "\n");
            textData.append("LAST PAID DATE          :    " + bill.get_LAST_PAY_DATE().trim() + "\n");
            textData.append("RECEIPT NUMBER        :" + bill.get_LAST_RCPT_NO().trim() + "\n");
            textData.append("METER READER            :    " + CommonPref.getUserDetails(PrintHomeActivity.this).get_UserID().trim() + "\n");
            textData.append("TOLLFREE HELPLINE NO.   :    " + "1912" + "\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            Bitmap solar = BitmapFactory.decodeResource(getResources(), R.drawable.solar);
            if (solar != null) {
                solar = Bitmap.createScaledBitmap(solar, 350, 350,  true);
                method = "addTextAlign";
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                method = "addImage";
                mPrinter.addImage(solar, 0, 0, solar.getWidth(), solar.getHeight(),
                        Printer.COLOR_1,
                        Printer.MODE_MONO,
                        Printer.HALFTONE_DITHER,
                        Printer.PARAM_DEFAULT,
                        Printer.COMPRESS_AUTO);
            }
            textData.append("********************\n");
            textData.append("Install solar, save electricity.\n");
            textData.append("********************\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);


        } catch (Exception e) {
            e.printStackTrace();
            ShowMsg.showException(e, method, mContext);
            //Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return false;
        }

        textData = null;

        return true;
    }
    private boolean createReceiptDatahindi() {
        String method = "";
        String gstno = "";
        Bitmap logoData = null;
        Bitmap azadi = BitmapFactory.decodeResource(getResources(), R.drawable.cm);
         Uri file = Uri.parse(mrudetails.get_Meter_Photo());
        if (!Uri.EMPTY.equals(file)) {
            try {

                //    logoData = this.getContentResolver().loadThumbnail(file, new Size(350, 220), null);
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                    logoData = mContext.getContentResolver().loadThumbnail(file, new Size(200, 200), null);
                    //  azadi=mContext.getContentResolver().loadThumbnail(azadiuri, new Size(350, 220), null);
                } else {
                    logoData = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), file);
                    //   azadi = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), azadiuri);
                }
                //   Log.e("Image",logoData.toString());

                int x = logoData.getWidth();
                if (x > 220) {
                    logoData = Bitmap.createScaledBitmap(logoData, 350, 220, true);
                    //    azadi = Bitmap.createScaledBitmap(azadi, 350, 220, true);
                } else {
                    logoData = Bitmap.createScaledBitmap(logoData, 350, 220, true);
                    //   azadi = Bitmap.createScaledBitmap(azadi, 350, 220, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        StringBuilder textData = new StringBuilder();
        final int barcodeWidth = 2;
        final int barcodeHeight = 100;

        if (mPrinter == null) {
            return false;
        }
        try {
            method = "addTextSize";
            mPrinter.addTextSize(2, 2);
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addText";
            String header = "ऊर्जा विपत्र\n";
            double billAmt=Double.parseDouble(bill.get_SubTotalB().trim())-Double.parseDouble(bill.get_DPS_CURR().trim());
            if((bill.get_CATEGORY().startsWith("DS")||bill.get_CATEGORY().startsWith("KJ")) && Double.parseDouble(bill.get_PROMPT_AMT().trim())<=0 && billAmt<=0) {
                String zerobill = "\"शून्य बिल\"\n\n";
                mPrinter.addCommand(zerobill.getBytes("UTF-8"));
            }
            String header1 = CommonPref.getUserDetails(mContext).get_DivId().substring(0, 1);
            if (header1.equals("1")) {
                gstno = "10AAECN1588M2ZB";
            } else if (header1.equals("2")) {
                gstno = "10AASCS2207G2ZN";
            }
            mPrinter.addTextFont(Printer.FONT_C);
            mPrinter.addCommand(header.getBytes("UTF-8"));
            mPrinter.addText(bill.get_COMPANY().trim() + "\n");
            if (CommonPref.getUserDetails(PrintHomeActivity.this).get_billmsg().equalsIgnoreCase("Y") && azadi != null) {
                azadi = Bitmap.createScaledBitmap(azadi, 350, 300, true);
                method = "addTextAlign";
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                method = "addImage";
                mPrinter.addImage(azadi, 0, 0, azadi.getWidth(), azadi.getHeight(),
                        Printer.COLOR_1,
                        Printer.MODE_MONO,
                        Printer.HALFTONE_DITHER,
                        Printer.PARAM_DEFAULT,
                        Printer.COMPRESS_AUTO);
            }
            textData.append("\nसभी घरेलू उपभोक्ताओं से अब 125 यूनिट तक\nबिजली खपत पर कोई शुल्क नहीं लिया जाएगा।\nयह लाभ जुलाई माह की खपत से लागू है।\n");
            mPrinter.addTextSize(1, 2);
            mPrinter.addTextFont(Builder.FONT_C);
            mPrinter.addTextAlign(Builder.ALIGN_LEFT);
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            mPrinter.addTextFont(Builder.FONT_C);
            mPrinter.addTextAlign(Builder.ALIGN_RIGHT);
            mPrinter.addCommand("-नीतीश कुमार,मुख्यमंत्री बिहार\n".getBytes("UTF-8"));
            mPrinter.addTextAlign(Builder.ALIGN_CENTER);
            mPrinter.addCommand("----------------------------\n".getBytes("UTF-8"));
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mPrinter.addTextSize(2, 2);
            mPrinter.addTextFont(Printer.FONT_C);
            textData.append("राज्य सरकार अनुदान" + addBlankToString(bill.get_GOV_SUBSIDY().trim()) + "\n");
            //float totalB = Float.parseFloat(convertBlankToZero(bill.get_EC_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_DPS_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_FIX_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_EXC_DEM_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_ED_CURRENT())) + Float.parseFloat(convertBlankToZero(bill.get_MET_RENT())) + Float.parseFloat(convertBlankToZero(bill.get_SHUN_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_OTH_CURR()));
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            mPrinter.addTextFont(Printer.FONT_B);
            textData.append("***********************************\n");
            textData.append("GSTIN-" + gstno + "\n");
            textData.append("***********************************\n");
            if (Float.parseFloat(bill.get_PROMPT_AMT().trim()) > 0) {
                textData.append("विपत्र राशि रु." + bill.get_PROMPT_AMT() + " का दि. " + bill.get_UPTO_DATE().trim() + "\n");
                textData.append("ऑनलाइन भुगतान करें  एवं रु " + bill.get_ONLINE_REBATE().trim() + "का\n" + "अतिरिक्त छूट पाएं " + "\n");
                textData.append("******************************\n");
            }
            textData.append("ऊर्जा विपत्र  : " + monthYear(bill.get_BMONTH().trim(), bill.get_BYEAR().trim()) + "\n");
            textData.append("*****************************\n");
            if (Float.parseFloat(bill.get_SubTotalA().trim()) > 1000) {
                //textData.append("प्रिय " + bill.get_CNAME().trim() + ",\n" + "कृपया विदयुत बकाया राशि रू" + bill.get_PROMPT_AMT().trim() + " का\nभुगतान सुचना प्राप्ति के 15 दिनों के\nभीतर सुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के आलोक में दि." + bill.get_UPTO_DATE().trim() + " के पश्चात विदयुत\nसम्बन्ध विच्छेदित कर दिया जाएगा।" + "\n" + "                 स0 वि0 अभि0" + "\n");
                textData.append("******************************\n");
            }
            textData.append("तारीख: " + bill.get_BILL_DT().trim() + "     समय: " + bill.get_BILL_TIME().trim() + "\n");
            textData.append("उपभोक्ता विवरण\n");
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("विपत्र संख्या     :    " + bill.get_BILL_NO().trim() + "\n");
            textData.append("प्रमंडल         :    " + bill.get_DIV_NAME().trim() + "\n");
            textData.append("अवर प्रमंडल     :    " + bill.get_SUB_DIV_NAME().trim() + "\n");
            textData.append("उपभोक्ता  संख्या :    " + bill.get_CON_ID().trim() + "\n");
            textData.append("खाता नंबर      :    " + bill.get_ACT_NO().trim() + "\n");
            textData.append("खाता          :    " + CommonPref.getUserDetails(PrintHomeActivity.this).get_MRUNo() + "\n");
            textData.append("उपभोक्ता नाम   :    " + bill.get_CNAME().trim() + "\n");
            textData.append("पता           :    ");
            textData.append(bill.get_BILL_ADDRESS().trim() + "\n");
            textData.append("क्षेत्र            :  " + bill.get_AREA_TYPE().trim() + "\n");
            textData.append("पोल सं         :  " + bill.get_POLE_NO().trim() + "\n");
            textData.append("मीटर संख्या     :  " + bill.get_METER_NO().trim() + "       PH : " + bill.get_PHASE().trim() + "\n");
            textData.append("मीटर स्वामित्व  :  " + bill.get_MET_OWNER().trim() + "\n");
            textData.append("उपभोक्ता श्रेणी   :  " + bill.get_CATEGORY().trim() + "\n");
            if (bill.get_CATEGORY().contains("D")) {
                textData.append("संविदा मांग  :  " + bill.get_CON_DEM().trim() + "\n");

            } else {
                textData.append("स्वीकृत भार  : " + bill.get_SANC_LOAD().trim() + "\n");

            }
            textData.append("जमानत राशि     : " + bill.get_SEC_DEP().trim() + "\n");
            textData.append("विपत्र माह       :  " + bill.get_BILLED_MONTH().trim() + "\n");
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addCommand("मानपठन \n".getBytes("UTF-8"));
            mPrinter.addText("---------------------------\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("           पिछला    " + "वर्तमान  \n");
            textData.append("पठन   :     " + bill.get_PREV_READ().trim() + "         " + bill.get_CUR_READ().trim() + "\n");
            textData.append("तारीख :     " + bill.get_PRE_READ_DATE().trim() + "  " + bill.get_BILL_DT().trim() + "\n");
            textData.append("स्थिति :     " + bill.get_PRE_READ_STAT().trim() + "           " + bill.get_READ_STAT().trim() + "   \n");
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            if (logoData != null) {
                method = "addTextAlign";
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                method = "addImage";
                mPrinter.addImage(logoData, 0, 0, logoData.getWidth(), logoData.getHeight(),
                        Printer.COLOR_1,
                        Printer.MODE_MONO,
                        Printer.HALFTONE_DITHER,
                        Printer.PARAM_DEFAULT,
                        Printer.COMPRESS_AUTO);
                // Flush image
                //mPrinter.sendData(Printer.PARAM_DEFAULT);
            }
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("\n");
            //float consumption = (Float.parseFloat(bill.get_CUR_READ().trim()) - Float.parseFloat(bill.get_PREV_READ().trim())) *Float.parseFloat(bill.get_MULTI_FACT().trim());
            textData.append("गुणांक   :  " + bill.get_MULTI_FACT().trim() + "    खपत       : " + bill.get_UNITS_CONS() + "\n");
            textData.append("दर्ज मांग : " + bill.get_REC_DEMAND().trim() + "    पावर फैक्टर  : " + bill.get_POW_FACT().trim() + "\n");
            textData.append("एमएमसी : " + bill.get_MMC_UNIT().trim() + "    औसत       : " + bill.get_AVG_UNIT().trim() + "\n");
            textData.append("घोषित यूनिट : " + bill.get_BILLED_UNIT().trim() + " विपत्र प्रकार  : " + bill.get_BILL_TYPE().trim() + "\n");
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            textData.append("बक़ाया विवरण\n");
            textData.append("---------------------------\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            textData.append("ऊर्जा  बक़ाया         :" + addBlankToString(bill.get_EC_ARREAR().trim()) + "\n");
            textData.append("विद्युत कर बकाया      :" + addBlankToString1(bill.get_EDAREAR().trim()) + "\n");
            textData.append("विळम्ब अधिभार बकाया :" + addBlankToString(bill.get_DPS_ARREAR().trim()) + "\n");
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            Float i = (Float.parseFloat(convertBlankToZero(bill.get_OTH_ARREAR().trim()))) + (Float.parseFloat(convertBlankToZero(bill.getOTH_CGST_AMT().trim()))) + (Float.parseFloat(convertBlankToZero(bill.getOTH_SGCT_AMT().trim())));
            textData.append("अन्य बकाया          :" + addBlankToString(i.toString()) + "\n");
            //float totalA = Float.parseFloat(convertBlankToZero(bill.get_EC_ARREAR())) + Float.parseFloat(convertBlankToZero(bill.get_DPS_ARREAR())) + Float.parseFloat(convertBlankToZero(bill.get_OTH_ARREAR()));
            textData.append(" कुल बकाया (A)     :" + addBlankToString(bill.get_SubTotalA().trim()) + "\n");
            textData.append("*********************************\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            textData.append("वर्तमान विपत्र विवरण\n");
            textData.append("---------------------------\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            textData.append("ऊर्जा शुल्क          :" + addBlankToString(bill.get_EC_CURR().trim()) + "\n");
            textData.append("नियत / मांग शुल्क   :" + addBlankToString(bill.get_FIX_CURR().trim()) + "\n");
            textData.append("आधिक्य  मांग शुल्क  :" + addBlankToString(bill.get_EXC_DEM_CURR().trim()) + "\n");
            textData.append("विद्युत कर          :" + addBlankToString(bill.get_ED_CURRENT().trim()) + "\n");
            //textData.append("मीटर किराया       :" + addBlankToString(bill.get_MET_RENT().trim()) + "\n");
            //textData.append("सीजीएसटी  @ 9%   :" + addBlankToString(bill.get_CGST_AMT().trim()) + "\n");
            //textData.append("एसजीएसटी  @ 9%   :" + addBlankToString(bill.get_SGCT_AMT().trim()) + "\n");
            textData.append("कैपेसिटर   शुल्क     :" + addBlankToString(bill.get_SHUN_CURR().trim()) + "\n");
            textData.append("अन्य  शुल्क         :" + addBlankToString(bill.get_OTH_CURR().trim()) + "\n");
            textData.append("राज्य सरकार अनुदान :" + addBlankToString(bill.get_GOV_SUBSIDY().trim()) + "\n");
            //float totalB = Float.parseFloat(convertBlankToZero(bill.get_EC_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_DPS_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_FIX_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_EXC_DEM_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_ED_CURRENT())) + Float.parseFloat(convertBlankToZero(bill.get_MET_RENT())) + Float.parseFloat(convertBlankToZero(bill.get_SHUN_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_OTH_CURR()));
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            textData.append("---------------------------\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            textData.append("वर्तमान विपत्र राशि\n");
            textData.append(String.format("%.2f",billAmt));
            textData.append("\n");
            mPrinter.addTextSize(2, 2);
            mPrinter.addTextFont(Printer.FONT_B);
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            textData.append("******************************\n");
            if(!(bill.get_CATEGORY().startsWith("DS")||bill.get_CATEGORY().startsWith("KJ"))) {
                textData.append("विळम्ब अधिभार     :" + addBlankToString(bill.get_DPS_CURR().trim()) + "\n");
                textData.append("ब्याज  SD(C) पर   :" + addBlankToString(bill.get_INTR_SEC_DEP().trim()) + "\n");
                // textData.append("प्रोत्साहन          :" + addBlankToString(bill.get_INCENTIVE().trim()) + "\n");
                textData.append("रीमिशन           :" + addBlankToString(bill.get_Remission_charge().trim()) + "\n");
                textData.append("छूट MMC पर       :" + addBlankToString(bill.get_REBATE_ON_MMC().trim()) + "\n");
            //float total = Float.parseFloat(convertBlankToZero(bill.get_INTR_SEC_DEP())) + Float.parseFloat(convertBlankToZero(bill.get_INCENTIVE())) + Float.parseFloat(convertBlankToZero(bill.get_REBATE_ON_MMC())) + totalA + totalB ;
            textData.append("अतिरिक्त तिमाही छूट  :" + addBlankToString("-" + bill.getQUARTERLY_REBATE_POSTED().trim()) + "\n");
            }
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mPrinter.addTextSize(1, 1);
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            textData.append(" TOTAL(A+B+C): " + addBlankToString(bill.get_SubTotalC().trim()) + "\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            method = "addTextAlign";
            textData.append("***********************************\n");
            textData.append("छूट:" + addBlankToString(bill.get_REBATE().trim()) + "\n");
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            textData.append("देय राशि\n");
            textData.append("---------------------------\n");
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            textData.append("तक " + bill.get_UPTO_DATE().trim() + " :" + addBlankToString(bill.get_PROMPT_AMT().trim()) + "\n");
            textData.append("तक " + bill.get_BY_DATE().trim() + " :" + addBlankToString(bill.get_GROSS_AMT().trim()) + "\n");
            textData.append("बाद " + bill.get_AFTER_DATE().trim() + " :" + addBlankToString(bill.get_NET_AMT().trim()) + "\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            textData.append("***********************************\n");
            textData.append("अन्य बकाया  विवरण \n");
            textData.append("***********************************\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            textData.append("अन्य बकाया              :    " + bill.get_OTH_ARREAR().trim() + "\n");
//            textData.append("अन्य बकाया सीजीएसटी     :    " + bill.getOTH_CGST_AMT().trim() + "\n");
//            textData.append("अन्य बकाया  एसजीएसटी   :     " + bill.getOTH_SGCT_AMT().trim() + "\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            textData.append("***********************************\n");
            textData.append("अंतिम भुगतान का विवरण \n");
            textData.append("***********************************\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            textData.append("अंतिम भुगतान की   राशि  :  " + bill.get_LAST_PAID_AMT().trim() + "\n");
            textData.append("अंतिम भुगतान तिथि       :  " + bill.get_LAST_PAY_DATE().trim() + "\n");
            textData.append("रसीद संख्या      :  " + bill.get_LAST_RCPT_NO().trim() + "\n");
            textData.append("मीटर वाचक              :  " + CommonPref.getUserDetails(PrintHomeActivity.this).get_UserID().trim() + "\n");
            textData.append("टोलफ्री हेल्पलाइन नं        :  " + "1912" + "\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());
            Bitmap solar = BitmapFactory.decodeResource(getResources(), R.drawable.solar);
            if (solar != null) {
                solar = Bitmap.createScaledBitmap(solar, 350, 350, true);
                method = "addTextAlign";
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                method = "addImage";
                mPrinter.addImage(solar, 0, 0, solar.getWidth(), solar.getHeight(),
                        Printer.COLOR_1,
                        Printer.MODE_MONO,
                        Printer.HALFTONE_DITHER,
                        Printer.PARAM_DEFAULT,
                        Printer.COMPRESS_AUTO);
            }
            method = "addCut";
           mPrinter.addCut(Printer.CUT_FEED);
//            localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
//            Intent intent = new Intent(PrintHomeActivity.this,
//                    MainActivity.class);
//            // Bundle databundale=new Bundle();
//    /*        databundale.putString("actno",mru1.get_CON_ID());
//            databundale.putString("name",mru1.get_CNAME());
//            databundale.putString("subdivid",mru1.get_SECTION_ID().substring(0,4));
//            intent.putExtra("bundle",databundale);*/
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
//            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            if (GlobalVariables.isFinishing_MRS)
                ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return false;
        }

        textData = null;

        return true;
    }
    private boolean createReceiptDataOffline() {
        String method = "";
        String Gstno = "";
        StringBuilder textData = new StringBuilder();
        if (mPrinter == null) {
            return false;
        }
        try {
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addTextSize";
            mPrinter.addTextSize(2, 2);
            method = "addText";

            String website = "";
            String header = CommonPref.getUserDetails(mContext).get_DivId().substring(0, 1);
            if (header.equals("1")) {
                header = "NBPDCL";
                website = "www.nbpdcl.co.in";
                Gstno = "10AAECN1588M2ZB";
            } else if (header.equals("2")) {
                header = "SBPDCL";
                website = "www.sbpdcl.co.in";
                Gstno = "10AASCS2207G2ZN";
            } else {
                header = "";
            }
            mPrinter.addText(header + "\n");
            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            mPrinter.addTextFont(Printer.FONT_B);
            mPrinter.addText("******************************\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("A/C NUMBER    :       " + bill.get_ACT_NO().trim() + "\n");
            textData.append("DIVISION      :       " + CommonPref.getUserDetails(PrintHomeActivity.this).get_DivName().trim() + "\n");
            textData.append("SUBDIVISION   :       " + CommonPref.getUserDetails(PrintHomeActivity.this).get_SubdivName().trim() + "\n");
            textData.append("CON ID        :       " + bill.get_CON_ID().trim() + "\n");
            textData.append("MRU           :       " + CommonPref.getUserDetails(PrintHomeActivity.this).get_MRUNo() + "\n");
            textData.append("CONSUMER NAME :   	   " + bill.get_CNAME().trim() + "\n");
            String meterStatus = "";
            String meterReading = "";
            if (bill.get_READ_STAT().toString().trim().equalsIgnoreCase("OK")) {
                meterStatus = "Actual";
                meterReading = bill.get_CUR_READ().toString().trim();
            } else {
                meterStatus = "Average";
                meterReading = "NA";
            }
            textData.append("CURR MTR Sts  :       " + meterStatus + " ( " + bill.get_READ_STAT().toString().trim() + " ) " + "\n");
            textData.append("CURR Rdg Dt   :       " + bill.get_READ_DATE().trim() + "\n");
            textData.append("CURR Rdg (Kwh):       " + meterReading + "\n");
            textData.append("PREV Rdg (Kwh):       " + bill.get_PREV_READ().trim() + "\n");

            String msg = "";

            if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("4X")) {
                msg = " Very High Consumption Recorded. ";
            } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("NE")) {
                msg = " Negative Bill. ";
            } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("LK")) {
                /*  msg = " Reading After More Than 3 Months.  ";*/
                msg = " ";
            }

            textData.append("The Bill could not be Printed due to " + msg + "\nKindly visit our website at " + website + " \nOr Concerned Sub-division / division office. \n");
            textData.append("ERROR CODE    :        " + bill.get_AMBI_Flag().toString().trim() + "\n");


            textData.append("METER READER  :       " + CommonPref.getUserDetails(PrintHomeActivity.this).get_UserID().trim() + "\n");
            textData.append("TOLLLFREE HELPLINE NO.:  " + "1912" + "\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);
            c1 = localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
            if (c1 == 1) {
                Toast.makeText(PrintHomeActivity.this, "Update database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PrintHomeActivity.this, "Error in database", Toast.LENGTH_LONG).show();
            }


        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));

            return false;
        }

        textData = null;

        return true;
    }
    private boolean createReceiptDataOfflineHIN() {
        String method = "";
        String Gstno = "";
        StringBuilder textData = new StringBuilder();
        if (mPrinter == null) {
            return false;
        }
        try {
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addTextSize";
            mPrinter.addTextSize(2, 2);
            method = "addText";

            String website = "";
            String header = CommonPref.getUserDetails(mContext).get_DivId().substring(0, 1);
            if (header.equals("1")) {
                header = "NBPDCL";
                website = "www.nbpdcl.co.in";
                Gstno = "10AAECN1588M2ZB";
            } else if (header.equals("2")) {
                header = "SBPDCL";
                website = "www.sbpdcl.co.in";
                Gstno = "10AASCS2207G2ZN";
            } else {
                header = "";
            }
            mPrinter.addText(header + "\n");
            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            mPrinter.addText("******************************\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("खाता नंबर     :   " + bill.get_ACT_NO().trim() + "\n");
            textData.append("प्रमंडल         :   " + CommonPref.getUserDetails(PrintHomeActivity.this).get_DivName().trim() + "\n");
            textData.append("अवर प्रमंडल     :   " + CommonPref.getUserDetails(PrintHomeActivity.this).get_SubdivName().trim() + "\n");
            textData.append("उपभोक्ता  संख्या :   " + bill.get_CON_ID().trim() + "\n");
            textData.append("खाता          :   " + CommonPref.getUserDetails(PrintHomeActivity.this).get_MRUNo() + "\n");
            textData.append("उपभोक्ता नाम   :   " + bill.get_CNAME().trim() + "\n");
            String meterStatus = "";
            String meterReading = "";
            if (bill.get_READ_STAT().toString().trim().equalsIgnoreCase("OK")) {
                meterStatus = "Actual";
                meterReading = bill.get_CUR_READ().toString().trim();
            } else {
                meterStatus = "Average";
                meterReading = "NA";
            }
            textData.append("वर्तमान मीटर की स्थिति : " + meterStatus + " ( " + bill.get_READ_STAT().toString().trim() + " ) " + "\n");
            textData.append("वर्तमान पढ़ने की तारीख : " + bill.get_READ_DATE().trim() + "\n");
            textData.append("वर्तमान रीडिंग (Kwh)   :  " + meterReading + "\n");
            textData.append("पिछले  रीडिंग (Kwh)   :  " + bill.get_PREV_READ().trim() + "\n");

            String msg = "";

            if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("4X")) {
                msg = " अत्यधिक उच्च खपत रिकॉर्ड\n. ";
            } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("NE")) {
                msg = " नकारात्मक बिल\n ";
            } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("LK")) {
                /*  msg = " Reading After More Than 3 Months.  ";*/
                msg = "LK ";
            }

            // textData.append(msg + " के कारण बिल को प्रिंट नहीं किया जा सका\n " + "कृपया हमारी वेबसाइट  \n" + website + "\n पर जाएँ " + "या संबंधित अवर प्रमंडल / प्रमंडल   कार्यालय से संपर्क करें\n");
            textData.append("अद्यतन बिल के लिए " + website + "\n पर जाएँ " + "या संबंधित अवर प्रमंडल / प्रमंडल   कार्यालय से संपर्क करें\n");

            textData.append("एरर कोड    :        " + bill.get_AMBI_Flag().toString().trim() + "\n");
            textData.append("मीटर वाचक  :       " + CommonPref.getUserDetails(PrintHomeActivity.this).get_UserID().trim() + "\n");
            textData.append("हेल्पलाइन नं. :       " + "1912" + "\n");
            /*       if (CommonPref.getUserDetails(PrintHomeActivity.this).get_billmsg().equalsIgnoreCase("Y")) {
                method = "addTextAlign";
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                textData.append("***********************************\n");
                textData.append("निर्वाचक सूची  का विशेष संक्षिप्त \n पुनरीक्षण कार्यक्रम,2020 \n" +
                        "मतदाता सत्यापन  कार्यक्रम (इवीपी)  \n" +
                        "   01-09-2019 से 18-11-2019 \n" +
                        "मैं  करूंगा  अपनी प्रविष्टियां \n100 प्रतिशत सही\n");
                textData.append("***********************************\n \n");
            }*/
            method = "addText";
            mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
            textData.delete(0, textData.length());

                method = "addTextAlign";
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                textData.append("*****************************" + "\n");
                textData.append("* सोलर लगाए बिजली बिल बचाए !  *" + "\n");
                textData.append("****************************" + "\n");
                mPrinter.addCommand(textData.toString().getBytes("UTF-8"));
                textData.delete(0, textData.length());
               /* method = "addTextAlign";
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append("आवेदन हेतु https://www.pmsuryaghar.gov.in पर जाए \n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());*/

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);
            c1 = localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
            if (c1 == 1) {
                Toast.makeText(PrintHomeActivity.this, "Update database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PrintHomeActivity.this, "Error in database", Toast.LENGTH_LONG).show();
            }


        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));

            return false;
        }

        textData = null;

        return true;
    }


    private void createReceiptDataForanalogics() {
        String method = "";
        String gstno = "";

        Bitmap logoData = null;
        Uri file = Uri.parse(mrudetails.get_Meter_Photo());
        if (!Uri.EMPTY.equals(file)) {
            try {
                logoData = this.getContentResolver().loadThumbnail(file, new Size(200, 200), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        StringBuilder textData = new StringBuilder();
        Bluetooth_Printer_2inch_prof_ThermalAPI printer = new Bluetooth_Printer_2inch_prof_ThermalAPI();
        if (printer == null) {
            return;
        }
        try {
            String header;
            if (bill.get_IsAlredyPrint() != null) {
                if (bill.get_IsAlredyPrint().toString().trim().equals("Y")) {
                    header = "\n          Duplicate Bill";
                } else {
                    header = "\n          ENERGY BILL";
                }
            } else {
                header = "\n          ENERGY BILL";
            }
            String header1 = CommonPref.getUserDetails(mContext).get_DivId().substring(0, 1);
            if (header1.equals("1")) {
                gstno = "10AAECN1588M2ZB";
            } else if (header1.equals("2")) {
                gstno = "10AASCS2207G2ZN";
            }
            textData.append(header + "\n");
            textData.append("             " + bill.get_COMPANY().trim() + "\n");
            textData.append("********************************\n");
            textData.append("     GSTIN-" + gstno + "\n");

            if (Float.parseFloat(bill.get_PROMPT_AMT().trim()) > 0) {
                textData.append("*****************************\n");
                String strRebateOnline = bill.get_ONLINE_REBATE();
                textData.append("Get Extra Rs " + strRebateOnline.trim() + " rebate if" + "\n" + "Rs " + bill.get_PROMPT_AMT().trim() + " paid online upto " + "\n" + bill.get_UPTO_DATE().trim() + "\n");
            }
            textData.append("*****************************\n");
            textData.append("ELECTRICITY BILL  : " + monthYear(bill.get_BMONTH().trim(), bill.get_BYEAR().trim()) + "\n");
            textData.append("*******************************\n");
            if (Float.parseFloat(bill.get_SubTotalA().trim()) > 1000) {
                // textData.append("प्रिय " + bill.get_CNAME().trim() + ",\n" + "कृपया विदयुत बकाया राशि रू" + bill.get_PROMPT_AMT().trim() + " का\nभुगतान सुचना प्राप्ति के 15 दिनों के\nभीतर सुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के आलोक में दि." + bill.get_UPTO_DATE().trim() + " के पश्चात विदयुत\nसम्बन्ध विच्छेदित कर दिया जाएगा।" + "\n" + "                 स0 वि0 अभि0" + "\n");
                textData.append("Dear " + bill.get_CNAME().trim() + ",\n" + "Please ensure payment of\nelectricity bill dues of Rs" + bill.get_PROMPT_AMT().trim() + "\nwithin 15 days after getting \ninformation otherwise\nelectricity connection will be\ndisconnected after date " + bill.get_UPTO_DATE().trim() + " against article 56\nof electricity act 2003 ." + "\n" + "               Asst Elect Eng." + "\n");
                textData.append("******************************\n");
            }
            textData.append("DATE: " + bill.get_BILL_DT().trim() + " TIME: " + bill.get_BILL_TIME().trim() + "\n");
            textData.append("        CONSUMER DETAILS\n");
            textData.append("********************************\n");
            textData.append("BILL NO       : " + bill.get_BILL_NO().trim() + "\n");
            textData.append("DIVISION      : " + bill.get_DIV_NAME().trim() + "\n");
            textData.append("SUBDIVISION   : " + bill.get_SUB_DIV_NAME().trim() + "\n");
            textData.append("CON ID        : " + bill.get_CON_ID().trim() + "\n");
            textData.append("A/C NUMBER    : " + bill.get_ACT_NO().trim() + "\n");
            textData.append("MRU           : " + CommonPref.getUserDetails(PrintHomeActivity.this).get_MRUNo() + "\n");
            textData.append("NAME          : " + bill.get_CNAME().trim() + "\n");
            textData.append("ADDRESS       : ");
            textData.append(bill.get_BILL_ADDRESS().trim() + "\n");
        /*    conn.printData(textData.toString().getBytes());
            textData.delete(0, textData.length());*/
            textData.append("AREA TYPE     : " + bill.get_AREA_TYPE().trim() + "\n");
            textData.append("POLE NO       : " + bill.get_POLE_NO().trim() + "\n");
            textData.append("METER NO      : " + bill.get_METER_NO().trim() + " PH :" + bill.get_PHASE().trim() + "\n");
            textData.append("MTR OWNER     : " + bill.get_MET_OWNER().trim() + "\n");
            textData.append("CATEGORY      : " + bill.get_CATEGORY().trim() + "\n");
            textData.append("SL :" + bill.get_SANC_LOAD().trim() + "  CL:" + bill.get_CONC_LOAD().trim() + "  CD:" + bill.get_CON_DEM().trim() + "\n");
            textData.append("SD            : " + bill.get_SEC_DEP().trim() + "\n");
            textData.append("BILLED DAYS   : " + bill.get_BILLED_MONTH().trim() + "\n");
            textData.append("******************************\n");
            textData.append("        READING DETAILS\n");
            textData.append("------------------------------\n");
            textData.append("           PREVIOUS   CURRENT\n");
            textData.append("READING :   " + bill.get_PREV_READ().trim() + "         " + bill.get_CUR_READ().trim() + "\n");
            textData.append("DATE    : " + bill.get_PRE_READ_DATE().trim() + " " + bill.get_BILL_DT().trim() + "\n");
            textData.append("STATUS  :   " + bill.get_PRE_READ_STAT().trim() + "           " + bill.get_READ_STAT().trim() + "   \n");
            conn.printData(textData.toString());
            textData.delete(0, textData.length());
          /*  if (file.exists()) {
                byte c[] = null;
                try {
                    c = printer.prepareImageDataToPrint_VIP(address, file.getAbsolutePath());

                } catch (InterruptedException e) {
                    Utiilties.writeIntoLog(Log.getStackTraceString(e));
                    e.printStackTrace();

                }
                conn.printData(c);
                //	conn.printData(s.getBytes());
                conn.printData(printer.Reset_VIP());

                *//*byte c[] = convertBitmapToByteArray(PrintHomeActivity.this,logoData);
               //c=convertBitmapToByteArray(PrintHomeActivity.this,logoData);
                String hexstring=toHexString(c);
                byte[] imageData = printer.prepare2InchImageData_VIP(address,
                        hexstring, 0);
                conn.printData(imageData);*//*
            }*/
            textData.append("\n");
            textData.append("OLD CONSUMPTION  :  " + bill.get_OLD_CONSUMPTION().trim() + "\n");
            textData.append("MULTIPLYING FACTOR : " + bill.get_MULTI_FACT().trim() + "\n");
            textData.append("CONSUMPTION : " + bill.get_UNITS_CONS() + "\n");
            textData.append("RECORDED DEMAND  : " + bill.get_REC_DEMAND().trim() + "\n");
            textData.append("POWER FACTOR:" + bill.get_POW_FACT().trim() + "\n");
            textData.append("MMC UNITS        : " + bill.get_MMC_UNIT().trim() + "  AVG: " + bill.get_AVG_UNIT().trim() + "\n");
            textData.append("BLD UNITS        : " + bill.get_BILLED_UNIT().trim() + "  TYPE: " + bill.get_BILL_TYPE().trim() + "\n");
            textData.append("********************************\n");
            if (!bill.get_KEPT_AMOUNT().equalsIgnoreCase("NA")) {
                textData.append("KEPT AMOUNT   : " + "Rs." + bill.get_KEPT_AMOUNT().trim() + "\n");
                textData.append("********************************\n");
            }

            textData.append("        ARREAR DETAILS\n");    
            textData.append("-------------------------------\n");
            textData.append("ENERGY DUES      :" + addBlankToString1(bill.get_EC_ARREAR().trim()) + "\n");
            textData.append("ARREAR DPS       :" + addBlankToString1(bill.get_DPS_ARREAR().trim()) + "\n");
            textData.append("ARREAR ED        :" + addBlankToString1(bill.get_EDAREAR().trim()) + "\n");
            Float i = (Float.parseFloat(convertBlankToZero(bill.get_OTH_ARREAR().trim()))) + (Float.parseFloat(convertBlankToZero(bill.getOTH_CGST_AMT().trim()))) + (Float.parseFloat(convertBlankToZero(bill.getOTH_SGCT_AMT().trim())));
            textData.append("OTHERS           :" + addBlankToString1(i.toString().trim()) + "\n");
            textData.append("SUB TOTAL (A)    :" + addBlankToString1(bill.get_SubTotalA().trim()) + "\n");
            textData.append("********************************\n");
            textData.append("      CURRENT BILL DETAILS\n");
            textData.append("------------------------------\n");
            textData.append("ENERGY CHARGES   :" + addBlankToString1(bill.get_EC_CURR().trim()) + "\n");
            textData.append("DPS              :" + addBlankToString1(bill.get_DPS_CURR().trim()) + "\n");
            textData.append("FIXED/DEMD CHG   :" + addBlankToString1(bill.get_FIX_CURR().trim()) + "\n");
            textData.append("EXCESS DEMD CHG  :" + addBlankToString1(bill.get_EXC_DEM_CURR().trim()) + "\n");
            textData.append("ELECTRICITY DUTY :" + addBlankToString1(bill.get_ED_CURRENT().trim()) + "\n");
//            textData.append("METER RENT       :" + addBlankToString1(bill.get_MET_RENT().trim()) + "\n");
//            textData.append("CGST on MR @9%   :" + addBlankToString1(bill.get_CGST_AMT().trim()) + "\n");
//            textData.append("SGST on MR @9%   :" + addBlankToString1(bill.get_SGCT_AMT().trim()) + "\n");
            textData.append("SHUNT CAP. CHG   :" + addBlankToString1(bill.get_SHUN_CURR().trim()) + "\n");
            textData.append("OTHERS CHG       :" + addBlankToString1(bill.get_OTH_CURR().trim()) + "\n");
            textData.append("------------------------------\n");
            textData.append("STATE GOV SUBSIDY:" + addBlankToString1(bill.get_GOV_SUBSIDY().trim()) + "\n");
            //  printer.font_Courier_10_VIP(String.format("------------------------------\n"));
            // textData.append("STATE GOV SUBSIDY:" + addBlankToString1(bill.get_GOV_SUBSIDY().trim()) + "\n");
            textData.append("------------------------------\n");
            textData.append("SUB TOTAL (B)    :" + addBlankToString1(bill.get_SubTotalB().trim()) + "\n");
            textData.append("********************************\n");
            textData.append("INTEREST ON SD(C):" + addBlankToString1(bill.get_INTR_SEC_DEP().trim()) + "\n");
            // textData.append("INCENTIVE        :" + addBlankToString1(bill.get_INCENTIVE().trim()) + "\n");
            textData.append("REMISSION (if any) (-):" + addBlankToString(bill.get_Remission_charge().trim()) + "\n");
            textData.append("REBATES ON MMC   :" + addBlankToString1(bill.get_REBATE_ON_MMC().trim()) + "\n");
            textData.append("EXTRA QUARTERLY REBATE  :" + addBlankToString1("-"+bill.getQUARTERLY_REBATE_POSTED().trim()) + "\n");

            textData.append("TOTAL (A+B+C)    :" + addBlankToString1(bill.get_SubTotalC().trim()) + "\n");
            textData.append("********************************\n");
            textData.append("REBATES          :" + addBlankToString1(bill.get_REBATE().trim()) + "\n");
            textData.append("        AMOUNT PAYABLE\n");
            textData.append("-------------------------------\n");
            textData.append("UPTO  " + bill.get_UPTO_DATE().trim() + " :" + addBlankToString1(bill.get_PROMPT_AMT().trim()) + "\n");
            textData.append("BY    " + bill.get_BY_DATE().trim() + " :" + addBlankToString1(bill.get_GROSS_AMT().trim()) + "\n");
            textData.append("AFTER " + bill.get_AFTER_DATE().trim() + " :" + addBlankToString1(bill.get_NET_AMT().trim()) + "\n");
            if (!bill.get_KEPT_AMOUNT().equalsIgnoreCase("NA")) {
                textData.append("-----------------------");
                textData.append("Installment amount( C ):" + bill.get_KEPT_INST_FLAG() + " " + bill.get_KEPT_INST_AMT().trim() + "\n");
                textData.append("Amount Payable ( B+C ) :" + bill.get_KEPT_PAY_AMT() + "\n");
                textData.append("-----------------------");
            }

            textData.append("********************************\n");
            textData.append("     OTHER ARREAR DETAILS \n");
            textData.append("********************************\n");
            textData.append("OTHERS             :" + addBlankToString1(bill.get_OTH_ARREAR().trim()) + "\n");
//            textData.append("OTHER ARREAR CGST  : " + bill.getOTH_CGST_AMT().trim() + "\n");
//            textData.append("OTHER ARREAR SGST  : " + bill.getOTH_SGCT_AMT().trim() + "\n");
            textData.append("********************************\n");
            textData.append("     DETAILS OF LAST PAYMENT \n");
            textData.append("********************************\n");
            textData.append("LAST PAID AMOUNT   : " + bill.get_LAST_PAID_AMT().trim() + "\n");
            textData.append("LAST PAID DATE     : " + bill.get_LAST_PAY_DATE().trim() + "\n");
            textData.append("RECEIPT No.:" + bill.get_LAST_RCPT_NO().trim() + "\n");
            textData.append("METER READER       : " + CommonPref.getUserDetails(PrintHomeActivity.this).get_UserID().trim() + "\n");
            textData.append("TOLLLFREE HELPLINE NO.: " + "1912" + "\n");
    /*        if (CommonPref.getUserDetails(PrintHomeActivity.this).get_billmsg().equalsIgnoreCase("Y")) {
                textData.append("***********************************\n");
                textData.append("निर्वाचक सूची  का विशेष संक्षिप्त \n पुनरीक्षण कार्यक्रम,2020 \n" +
                        "मतदाता सत्यापन  कार्यक्रम (इवीपी)  \n" +
                        "   01-09-2019 से 18-11-2019 \n" +
                        "मैं  करूंगा  अपनी प्रविष्टियां \n100 प्रतिशत सही\n\n\n\n\n");
                textData.append("***********************************\n");
            }
*/
            conn.printData(textData.toString());
            conn.printData(printer.Reset_VIP());
            textData.delete(0, textData.length());
       /*      byte c[] = null;
            try {
                c = printer.prepareLogoImageDataToPrint(address, getbitmap(bill.get_CON_ID().trim(), bill.get_PROMPT_AMT().trim()));

          } catch (InterruptedException e) {
              Utiilties.writeIntoLog(Log.getStackTraceString(e));
              e.printStackTrace();

          }*/
           /* conn.printData(c);
            conn.printData(printer.Reset_VIP());*/
            c1 = localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
            if (c1 == 1) {
                Toast.makeText(PrintHomeActivity.this, "Update database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PrintHomeActivity.this, "Error in database", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return;
        }
        textData = null;
    }
    private String monthYear(String mm, String YYYY) {
        String monthYear = "";
        if (mm.equals("01") || mm.equals("1")) {
            return monthYear = "JAN " + YYYY;
        } else if (mm.equals("02") || mm.equals("2")) {
            return monthYear = "FEB " + YYYY;
        } else if (mm.equals("03") || mm.equals("3")) {
            return monthYear = "MAR " + YYYY;
        } else if (mm.equals("04") || mm.equals("4")) {
            return monthYear = "APR " + YYYY;
        } else if (mm.equals("05") || mm.equals("5")) {
            return monthYear = "MAY " + YYYY;
        } else if (mm.equals("06") || mm.equals("6")) {
            return monthYear = "JUNE " + YYYY;
        } else if (mm.equals("07") || mm.equals("7")) {
            return monthYear = "JULY " + YYYY;
        } else if (mm.equals("08") || mm.equals("8")) {
            return monthYear = "AUG " + YYYY;
        } else if (mm.equals("09") || mm.equals("9")) {
            return monthYear = "SEP " + YYYY;
        } else if (mm.equals("10")) {
            return monthYear = "OCT " + YYYY;
        } else if (mm.equals("11")) {
            return monthYear = "NOV " + YYYY;
        } else if (mm.equals("12")) {
            return monthYear = "DEC " + YYYY;
        }

        return monthYear;
    }

    private String convertBlankToZero(String value) {

        if (value.trim().equals("")) {
            return value = "0";
        }

        return value;
    }

    private String addBlankToString(String value) {
        int x = value.trim().length();
        for (int i = 0; i < 15 - x; i++) {
            value = " " + value;
        }
        return value;
    }
    private String addBlankToString1(String value) {
        int x = value.trim().length();
        for (int i = 0; i < 10 - x; i++) {
            value = " " + value;
        }
        return value;
    }

    private void CheckBlueToothState(Boolean flag) {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {

            Toast.makeText(PrintHomeActivity.this, "Bluetooth is NOT Enabled",
                    Toast.LENGTH_LONG).show();

        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if (bluetoothAdapter.isDiscovering()) {
                    Toast.makeText(
                            PrintHomeActivity.this,
                            "Bluetooth is currently in device discovery process.",
                            Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(PrintHomeActivity.this, "Bluetooth is Enabled.",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(PrintHomeActivity.this, "Bluetooth is NOT Enabled.",
                        Toast.LENGTH_LONG).show();
                if (flag == true) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT1);
                }

            }
        }
    }

    public void open(boolean flag) {
        try {
            conn.openBT(address);
            //  boolean t=conn.isConnected();
            if (flag) {
                createReceiptDataForanalogics();
            } else {
                createReceiptDataOfflineForanalogics();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(ex));
        }
    }

    public byte[] convertBitmapToByteArray(Activity context, Bitmap bitmap) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return buffer.toByteArray();
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private void createReceiptDataOfflineForanalogics() {
        String method = "";
        String gstno = "";
        StringBuilder textData = new StringBuilder();
        Bluetooth_Printer_2inch_prof_ThermalAPI printer = new Bluetooth_Printer_2inch_prof_ThermalAPI();
        try {
            String website = "";
            String header = CommonPref.getUserDetails(mContext).get_DivId().substring(0, 1);
            if (header.equals("1")) {
                header = "             NBPDCL";
                website = "www.nbpdcl.co.in";
                gstno = "NBGSTNO12457896";
            } else if (header.equals("2")) {
                header = "             SBPDCL";
                website = "www.sbpdcl.co.in";
                gstno = "SBGSTNO12457896";
            } else {
                header = "";
            }
            textData.append(header + "\n");
            textData.append("******************************\n");
            textData.append("A/C NUMBER   :    " + bill.get_ACT_NO().trim() + "\n");
            textData.append("DIVISION     :    " + CommonPref.getUserDetails(PrintHomeActivity.this).get_DivName().trim() + "\n");
            textData.append("SUBDIVISION  :    " + CommonPref.getUserDetails(PrintHomeActivity.this).get_SubdivName().trim() + "\n");
            textData.append("CON ID       :    " + bill.get_CON_ID().trim() + "\n");
            textData.append("MRU          :    " + CommonPref.getUserDetails(PrintHomeActivity.this).get_MRUNo() + "\n");
            textData.append("CONSUMER NAME:    " + bill.get_CNAME().trim() + "\n");
            String meterStatus = "";
            String meterReading = "";
            if (bill.get_READ_STAT().toString().trim().equalsIgnoreCase("OK")) {
                meterStatus = "Actual";
                meterReading = bill.get_CUR_READ().toString().trim();
            } else {
                meterStatus = "Average";
                meterReading = "NA";
            }
            textData.append("CURR MTR Sts :    " + meterStatus + " ( " + bill.get_READ_STAT().toString().trim() + " ) " + "\n");
            textData.append("CURR Rdg Dt  :    " + bill.get_READ_DATE().trim() + "\n");
            textData.append("CURR Rdg(Kwh):    " + meterReading + "\n");
            textData.append("PREV Rdg(Kwh):    " + bill.get_PREV_READ().trim() + "\n");

            String msg = "";

            if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("4X")) {
                msg = " Very High Consumption Recorded. ";
            } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("NE")) {
                msg = " Negative Bill. ";
            } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("LK")) {
                /* msg = " Reading After More Than 3 Months.  ";*/
                msg = "Door  Locked More Than six month";
            } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("X")) {
                /* msg = " Reading After More Than 3 Months.  ";*/
                msg = "Outsort  because of High consumption";
            }
            textData.append("The Bill could not be Printed \n" + "due to " + msg + "Kindly visit our \n website at " + website + " \nOr Concerned Sub-division / division office. \n");
            textData.append("ERROR CODE    :     " + bill.get_AMBI_Flag().toString().trim() + "\n");
            textData.append("METER READER  :     " + CommonPref.getUserDetails(PrintHomeActivity.this).get_UserID().trim() + "\n");
            textData.append("TOLLLFREE HELPLINE NO.  : " + "1912" + "\n");
/*            if (CommonPref.getUserDetails(PrintHomeActivity.this).get_billmsg().equalsIgnoreCase("Y")) {
                textData.append("***********************************\n");
                textData.append("निर्वाचक सूची  का विशेष संक्षिप्त \n पुनरीक्षण कार्यक्रम,2020 \n" +
                        "मतदाता सत्यापन  कार्यक्रम (इवीपी)  \n" +
                        "   01-09-2019 से 18-11-2019 \n" +
                        "मैं  करूंगा  अपनी प्रविष्टियां \n100 प्रतिशत सही\n\n\n\n\n");
                textData.append("***********************************\n");
            }*/
            //    boolean t=conn.isConnected();
            conn.printData(textData.toString());
            textData.delete(0, textData.length());
            c1 = localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
            if (c1 == 1) {
                Toast.makeText(PrintHomeActivity.this, "Update database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PrintHomeActivity.this, "Error in database", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return;
        }
        textData = null;
    }


    private static String getERMessage(int status) {
        switch (status) {
            case POSPrinterConst.JPOS_EPTR_COVER_OPEN:
                return "Cover open";

            case POSPrinterConst.JPOS_EPTR_REC_EMPTY:
                return "Paper empty";

            case JposConst.JPOS_SUE_POWER_OFF_OFFLINE:
                return "Power off";

            default:
                return "Unknown";
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void showdialoggogreen() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PrintHomeActivity.this);
        builder1.setMessage("Consumer Opted for e-bill So, Hard copy of bill is not printing here !!");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                (dialog, id) -> dialog.cancel());

        builder1.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    private static int RGB2Gray(int r, int g, int b) {
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b);  //灰度转化公式
        return gray;
    }

    private boolean createReceiptDataOfflineFortvsenglish() {
        String method = "";
        String gstno = "";
        StringBuilder textData = new StringBuilder();
        try {
            String website = "";
            String header = CommonPref.getUserDetails(mContext).get_DivId().substring(0, 1);
            if (header.equals("1")) {
                header = "             NBPDCL";
                website = "www.nbpdcl.co.in";
                gstno = "NBGSTNO12457896";
            } else if (header.equals("2")) {
                header = "             SBPDCL";
                website = "www.sbpdcl.co.in";
                gstno = "SBGSTNO12457896";
            } else {
                header = "";
            }
            textData.append(header + "\n");
            textData.append("******************************\n");
            textData.append("A/C NUMBER   :    " + bill.get_ACT_NO().trim() + "\n");
            textData.append("DIVISION     :    " + CommonPref.getUserDetails(PrintHomeActivity.this).get_DivName().trim() + "\n");
            textData.append("SUBDIVISION  :    " + CommonPref.getUserDetails(PrintHomeActivity.this).get_SubdivName().trim() + "\n");
            textData.append("CON ID       :    " + bill.get_CON_ID().trim() + "\n");
            textData.append("MRU          :    " + CommonPref.getUserDetails(PrintHomeActivity.this).get_MRUNo() + "\n");
            textData.append("CONSUMER NAME:    " + bill.get_CNAME().trim() + "\n");
            String meterStatus = "";
            String meterReading = "";
            if (bill.get_READ_STAT().toString().trim().equalsIgnoreCase("OK")) {
                meterStatus = "Actual";
                meterReading = bill.get_CUR_READ().toString().trim();
            } else {
                meterStatus = "Average";
                meterReading = "NA";
            }
            HPRTPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x00});
            HPRTPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            textData.delete(0, textData.length());
            textData.append("CURR MTR Sts :    " + meterStatus + " ( " + bill.get_READ_STAT().toString().trim() + " ) " + "\n");
            textData.append("CURR Rdg Dt  :    " + bill.get_READ_DATE().trim() + "\n");
            textData.append("CURR Rdg(Kwh):    " + meterReading + "\n");
            textData.append("PREV Rdg(Kwh):    " + bill.get_PREV_READ().trim() + "\n");

            String msg = "";

            if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("4X")) {
                msg = " Very High Consumption Recorded. ";
            } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("NE")) {
                msg = " Negative Bill. ";
            } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("LK")) {

                msg = "Door  Locked More Than six month";
            } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("X")) {

                msg = "Outsort  because of High consumption";
            }
            textData.append("The Bill could not be Printed \n" + "due to " + msg + "Kindly visit our \n website at " + website + " \nOr Concerned Sub-division / division office. \n");
            textData.append("ERROR CODE    :     " + bill.get_AMBI_Flag().toString().trim() + "\n");
            textData.append("METER READER  :     " + CommonPref.getUserDetails(PrintHomeActivity.this).get_UserID().trim() + "\n");
            textData.append("TOLLLFREE HELPLINE NO.  : " + "1912" + "\n");
            /* if (CommonPref.getUserDetails(PrintHomeActivity.this).get_billmsg().equalsIgnoreCase("Y")) {
                textData.append("***********************************\n");
                textData.append(("निर्वाचक सूची  का विशेष संक्षिप्त \n पुनरीक्षण कार्यक्रम,2020 \n" +
                        "मतदाता सत्यापन  कार्यक्रम (इवीपी)  \n" +
                        "   01-09-2019 से 18-11-2019 \n" +
                        "मैं  करूंगा  अपनी प्रविष्टियां \n100 प्रतिशत सही\n\n\n\n\n").getBytes("UTF-8"));
                textData.append("***********************************\n");
            }*/
            //    boolean t=conn.isConnected();
            //conn.printData(textData.toString());
            HPRTPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            textData.delete(0, textData.length());
            c1 = localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
            if (c1 == 1) {
                Toast.makeText(PrintHomeActivity.this, "Update database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PrintHomeActivity.this, "Error in database", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return false;
        }
        textData = null;

        return true;
    }

    private boolean createReceiptDatafortvsenglish() {
        try {
            Thread.sleep(100);
            String method = "";
            String gstno = "";
            Bitmap logoData = null;
            Bitmap azadi = BitmapFactory.decodeResource(getResources(), R.drawable.cm);
            //Uri azadiuri = Uri.parse("android.resource://com.nic.app.biharelectricitybilling/drawable/azadi");
            Uri file = Uri.parse(mrudetails.get_Meter_Photo());
            if (!Uri.EMPTY.equals(file)) {
                try {
                    //  logoData = this.getContentResolver().loadThumbnail(file, new Size(350, 220), null);

                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                        logoData = mContext.getContentResolver().loadThumbnail(file, new Size(200, 200), null);
                        //    azadi=mContext.getContentResolver().loadThumbnail(azadiuri, new Size(350, 220), null);

                    } else {
                        logoData = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), file);
                        //     azadi = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), azadiuri);

                    }
                    Log.e("Image", logoData.toString());
                    int x = logoData.getWidth();
                    if (x > 220) {
                        logoData = Bitmap.createScaledBitmap(logoData, 350, 220, true);
                        //    azadi = Bitmap.createScaledBitmap(azadi, 350, 220, true);

                    } else {
                        logoData = Bitmap.createScaledBitmap(logoData, 350, 220, true);
                        //  azadi = Bitmap.createScaledBitmap(azadi, 350, 220, true);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            StringBuilder textData = new StringBuilder();

            String header = "         Energy Bill";
            String zeroBill = "  \"ZERO BILL\"";
            double billAmt=Double.parseDouble(bill.get_SubTotalB().trim())-Double.parseDouble(bill.get_DPS_CURR().trim());
            if((bill.get_CATEGORY().startsWith("DS")||bill.get_CATEGORY().startsWith("KJ")) && Double.parseDouble(bill.get_PROMPT_AMT())<=0 && billAmt<=0) {
                HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x45, 0x01}); // Bold ON
                HPRTPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x11}); // Double height and width
                HPRTPrinterHelper.WriteData((zeroBill + "\n").getBytes("UTF-8"));
                HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
                Thread.sleep(100);
            }
            String header1 = CommonPref.getUserDetails(mContext).get_DivId().substring(0, 1);
            if (header1.equals("1")) {
                gstno = "10AAECN1588M2ZB";
            } else if (header1.equals("2")) {
                gstno = "10AASCS2207G2ZN";
            }
            // Reset formatting
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x45, 0x00}); // Bold OFF
            HPRTPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x00}); // Font size reset
            HPRTPrinterHelper.WriteData((header + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(("           " + bill.get_COMPANY() + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            if (CommonPref.getUserDetails(PrintHomeActivity.this).get_billmsg().equalsIgnoreCase("Y") && azadi != null) {
                azadi = Bitmap.createScaledBitmap(azadi, 350, 350, true);
                printimage(azadi);
            }
            StringBuilder ntsMsg = new StringBuilder();
            ntsMsg.append("\nसभी घरेलू उपभोक्ताओं से अब\n125 यूनिट तक बिजली खपत पर\nकोई शुल्क नहीं लिया जाएगा।\nयह लाभ जुलाई माह की\nखपत से लागू है।\n");
            HPRTPrinterHelper.WriteData((ntsMsg.toString()).getBytes("UTF-8"));
            HPRTPrinterHelper.WriteData((" -नीतीश कुमार,मुख्यमंत्री बिहार\n").getBytes("UTF-8"));
            HPRTPrinterHelper.WriteData(("-----------------------\n").getBytes("UTF-8"));
            String subsidy="  STATE GOVT." ;
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x45, 0x01}); // Bold ON
            HPRTPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x11}); // Double height and width
            HPRTPrinterHelper.WriteData((subsidy + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(("SUBSIDY: ").getBytes("gb2312"));
            //HPRTPrinterHelper.WriteData((header + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            //HPRTPrinterHelper.WriteData(("          " + bill.get_COMPANY() + "\n").getBytes("gb2312"));
            //HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            //--------------------------------------------------
            textData.append(""+bill.get_GOV_SUBSIDY().trim() + "\n");
            textData.append("    *************************\n");
            textData.append("     GSTIN-" + gstno + "\n");
            textData.append("    *************************");
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x45, 0x00}); // Bold OFF
            HPRTPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x00}); // Font size reset
            HPRTPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());
            if (Float.parseFloat(bill.get_PROMPT_AMT().trim()) > 0) {

                String strRebateOnline = bill.get_ONLINE_REBATE().toString().trim();
                String txt = " Get Extra Rs " + strRebateOnline + " rebate \n if " + bill.get_PROMPT_AMT().trim() + " paid online \n" + "upto   " + bill.get_UPTO_DATE().trim();
                HPRTPrinterHelper.WriteData((txt + "\n").getBytes("gb2312"));
                HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});

            }
            textData.append("ELECTRICITY BILL  : ").append(monthYear(bill.get_BMONTH().trim(), bill.get_BYEAR().trim())).append("\n");
            textData.append("********************\n");
            HPRTPrinterHelper.WriteData((textData + "").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());
            if (Float.parseFloat(bill.get_SubTotalA().trim()) > 1000) {
                //  textData.append("प्रिय " + bill.get_CNAME().trim() + ",\n" + "कृपया विदयुत बकाया राशि रू" + bill.get_PROMPT_AMT().trim() + " का\nभुगतान सुचना प्राप्ति के 15 दिनों के\nभीतर सुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के आलोक में दि." + bill.get_UPTO_DATE().trim() + " के पश्चात विदयुत\nसम्बन्ध विच्छेदित कर दिया जाएगा।" + "\n" + "                 स0 वि0 अभि0" + "\n");
//                String str = "प्रिय " + bill.get_CNAME().trim() + ",\n" + "कृपया विदयुत बकाया राशि \n" +
//                        bill.get_PROMPT_AMT().trim() + " रू का भुगतान सुचना\nप्राप्ति के 15 दिनों के भीतर\nसुनिश्चित करें अन्यथा विदयुत\nअधिनियम 2003 के धारा 56 के\nआलोक में दि."
//                        + bill.get_UPTO_DATE().trim() + " के\nपश्चात विदयुत सम्बन्ध\nविच्छेदित कर दिया जाएगा।" + "\n" + "                 स0 वि0 अभि0";
//                HPRTPrinterHelper.WriteData((str + "\n").getBytes("UTF-8"));
//                HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
                textData.append("***********************\n");
            }
            textData.append("DATE: " + bill.get_BILL_DT().trim() + "  TIME: " + bill.get_BILL_TIME().trim() + "\n");
            textData.append("      CONSUMER DETAILS\n");
            textData.append("************************");
            HPRTPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());
            textData.append("BILL NO      :   " + bill.get_BILL_NO().trim() + "\n");
            textData.append("DIVISION     :    " + bill.get_DIV_NAME().trim() + "\n");
            textData.append("SUBDIVISION  :    " + bill.get_SUB_DIV_NAME().trim() + "\n");
            textData.append("CON ID       :    " + bill.get_CON_ID().trim() + "\n");
            textData.append("A/C NUMBER   :    " + bill.get_ACT_NO().trim() + "\n");
            textData.append("MRU    :    " + CommonPref.getUserDetails(PrintHomeActivity.this).get_MRUNo() + "\n");
            textData.append("NAME   :     " + bill.get_CNAME().trim() + "\n");
            textData.append("ADDRESS :  ");
            textData.append(bill.get_BILL_ADDRESS().trim() + "\n");
            textData.append("AREA TYPE   :  " + bill.get_AREA_TYPE().trim() + "\n");
            textData.append("POLE NO     :  " + bill.get_POLE_NO().trim() + "\n");
            textData.append("METER NO   :  " + bill.get_METER_NO().trim() + "   PH : " + bill.get_PHASE().trim() + "\n");
            textData.append("MTR OWNER  :  " + bill.get_MET_OWNER().trim() + "\n");
            textData.append("CATEGORY   :  " + bill.get_CATEGORY().trim() + "\n");
            textData.append("SL : " + bill.get_SANC_LOAD().trim() + "  CL:  " + bill.get_CONC_LOAD().trim() + "  CD:  " + bill.get_CON_DEM().trim() + "\n");
            textData.append("SD : " + bill.get_SEC_DEP().trim() + "\n");
            textData.append("BILLED DAYS  :  " + bill.get_BILLED_MONTH().trim() + "\n");
            textData.append("**********************\n");
            textData.append("READING DETAILS\n");
            textData.append("--------------------");
            HPRTPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());
            textData.append("        PREVIOUS    CURRENT  \n");
            textData.append("READING :" + bill.get_PREV_READ().trim() + "          " + bill.get_CUR_READ().trim() + "\n");
            textData.append("DATE    :" + bill.get_PRE_READ_DATE().trim() + "  " + bill.get_BILL_DT().trim() + "\n");
            textData.append("STATUS  :" + bill.get_PRE_READ_STAT().trim() + "          " + bill.get_READ_STAT().trim());
            HPRTPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            textData.delete(0, textData.length());
            if (logoData != null) {
                printimage(logoData);
            }
            textData.append("OLD CONSUMPTION :  " + bill.get_OLD_CONSUMPTION().trim() + "\n");
            textData.append("MF :  " + bill.get_MULTI_FACT().trim() + "   CONSUMPTION : " + bill.get_UNITS_CONS() + "\n");
            textData.append("RECORDED DEMAND : " + bill.get_REC_DEMAND().trim() + " PF : " + bill.get_POW_FACT().trim() + "\n");
            textData.append("MMC UNITS       : " + bill.get_MMC_UNIT().trim() + "   AVG : " + bill.get_AVG_UNIT().trim() + "\n");
            textData.append("BLD UNITS       : " + bill.get_BILLED_UNIT().trim() + " TYPE: " + bill.get_BILL_TYPE().trim() + "\n");
            textData.append("************************");
            HPRTPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            //   HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            textData.delete(0, textData.length());

            //float consumption = (Float.parseFloat(bill.get_CUR_READ().trim()) - Float.parseFloat(bill.get_PREV_READ().trim())) *Float.parseFloat(bill.get_MULTI_FACT().trim());
            if (!bill.get_KEPT_AMOUNT().equalsIgnoreCase("NA")) {
                textData.append("KEPT AMOUNT   : " + "Rs." + bill.get_KEPT_AMOUNT());
            }

            HPRTPrinterHelper.WriteData((textData + "").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());

            textData.append("       ARREAR DETAILS\n");
            textData.append("-----------------------\n");

            textData.append("ENERGY DUES   :" + addBlankToString(bill.get_EC_ARREAR().trim()) + "\n");
            textData.append("ARREAR DPS    :" + addBlankToString(bill.get_DPS_ARREAR().trim()) + "\n");
            textData.append("ARREAR ED     :" + addBlankToString(bill.get_EDAREAR().trim()) + "\n");
            textData.append("OTHERS        :" + addBlankToString(bill.get_OTH_ARREAR().trim()) + "\n");
            //float totalA = Float.parseFloat(convertBlankToZero(bill.get_EC_ARREAR())) + Float.parseFloat(convertBlankToZero(bill.get_DPS_ARREAR())) + Float.parseFloat(convertBlankToZero(bill.get_OTH_ARREAR()));
            textData.append("SUB TOTAL (A) :" + addBlankToString(bill.get_SubTotalA().trim()) + "\n");
            textData.append("************************");
            //  method = "addText";
            HPRTPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());

            textData.append("     CURRENT BILL DETAILS\n");
            textData.append("------------------------\n");

            textData.append("ENERGY CHARGES   :" + addBlankToString1(bill.get_EC_CURR().trim()) + "\n");
            textData.append("FIXED/DEMD CHG   :" + addBlankToString1(bill.get_FIX_CURR().trim()) + "\n");
            textData.append("EXCESS DEMD CHG  :" + addBlankToString1(bill.get_EXC_DEM_CURR().trim()) + "\n");
            textData.append("ELECTRICITY DUTY :" + addBlankToString1(bill.get_ED_CURRENT().trim()) + "\n");
            //textData.append("METER RENT       :" + addBlankToString1(bill.get_MET_RENT().trim()) + "\n");
//            textData.append("CGST on MR @ 9%  :" + addBlankToString1(bill.get_CGST_AMT().trim()) + "\n");
//            textData.append("SGST on MR @ 9%  :" + addBlankToString1(bill.get_SGCT_AMT().trim()) + "\n");
            textData.append("SHUNT CAP. CHG   :" + addBlankToString1(bill.get_SHUN_CURR().trim()) + "\n");
            textData.append("OTHERS CHG       :" + addBlankToString1(bill.get_OTH_CURR().trim()) + "\n");

            textData.append("-----------------------\n");

            textData.append("STATE GOV SUBSIDY :" + addBlankToString1(bill.get_GOV_SUBSIDY().trim()));
            HPRTPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(100);
            textData.delete(0, textData.length());
            //float totalB = Float.parseFloat(convertBlankToZero(bill.get_EC_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_DPS_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_FIX_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_EXC_DEM_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_ED_CURRENT())) + Float.parseFloat(convertBlankToZero(bill.get_MET_RENT())) + Float.parseFloat(convertBlankToZero(bill.get_SHUN_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_OTH_CURR()));

            textData.append("-----------------------\n");
            HPRTPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            textData.delete(0, textData.length());
            textData.append("Current Bill Amt\n   "+ String.format("%.2f",billAmt) + "\n");
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x45, 0x01}); // Bold ON
            HPRTPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x11}); // Double height and width
            HPRTPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(100);
            textData.delete(0, textData.length());
            textData.append("**************************\n");
            if(!(bill.get_CATEGORY().startsWith("DS")||bill.get_CATEGORY().startsWith("KJ"))) {
                textData.append("DPS                :" + addBlankToString1(bill.get_DPS_CURR().trim()) + "\n");
                textData.append("INTEREST ON SD(C)  :" + addBlankToString1(bill.get_INTR_SEC_DEP().trim()) + "\n");
                textData.append("INCENTIVE          :" + addBlankToString1(bill.get_INCENTIVE().trim()) + "\n");
                textData.append("REBATES ON MMC     :" + addBlankToString1(bill.get_REBATE_ON_MMC().trim()) + "\n");
            }
            textData.append("EXTRA QUARTERLY REBATE :" + "  " + ("-" + bill.getQUARTERLY_REBATE_POSTED().trim()) + "\n");
            textData.append("TOTAL (A+B+C)      :" + addBlankToString1(bill.get_SubTotalC().trim()) + "\n");
            textData.append("***********************\n");
            textData.append("REBATES            :" + addBlankToString1(bill.get_REBATE().trim()));
            // method = "addText";
            // Reset formatting
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x45, 0x00}); // Bold OFF
            HPRTPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x00}); // Font size reset
            HPRTPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(100);
            textData.delete(0, textData.length());
            textData.append("       AMOUNT PAYABLE\n");
            textData.append("-----------------------\n");
            textData.append("UPTO    " + bill.get_UPTO_DATE().trim() + " :" + addBlankToString1(bill.get_PROMPT_AMT().trim()) + "\n");
            textData.append("BY      " + bill.get_BY_DATE().trim() + " :" + addBlankToString1(bill.get_GROSS_AMT().trim()) + "\n");
            textData.append("AFTER   " + bill.get_AFTER_DATE().trim() + " :" + addBlankToString1(bill.get_NET_AMT().trim()));
            HPRTPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());
            if (!bill.get_KEPT_AMOUNT().equalsIgnoreCase("NA")) {


                textData.append("INSTALMENT AMOUNT ( C ):" + bill.get_KEPT_INST_FLAG() + " " + bill.get_KEPT_INST_AMT().trim() + "\n");
                textData.append("AMOUNT PAYABLE   ( B+C ) : " + bill.get_KEPT_PAY_AMT());

                textData.append("----------------------\n");
            }
            //  method = "addText";
            HPRTPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(100);
            textData.delete(0, textData.length());
            textData.append("    *******************\n");
            textData.append("    DETAILS OF LAST PAYMENT \n");
            textData.append("    ********************\n");
            textData.append("LAST PAID AMOUNT  :   " + bill.get_LAST_PAID_AMT().trim() + "\n");
            textData.append("LAST PAID DATE    :   " + bill.get_LAST_PAY_DATE().trim() + "\n");
            textData.append("RECEIPT NO.:" + bill.get_LAST_RCPT_NO().trim() + "\n");
            textData.append("METER READER      :  " + CommonPref.getUserDetails(PrintHomeActivity.this).get_UserID().trim() + "\n");
            textData.append("TOLLFREE HELPLINE NO. :  " + "1912" + "\n");
            //  method = "addText";
            HPRTPrinterHelper.WriteData(textData.toString().getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            HPRTPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x00});
            textData.delete(0, textData.length());
            Bitmap solar = BitmapFactory.decodeResource(getResources(), R.drawable.solar);
            if (solar != null) {
                solar = Bitmap.createScaledBitmap(solar, 350, 350, true);
                printimage(solar);
            }
            HPRTPrinterHelper.WriteData(("" + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            HPRTPrinterHelper.WriteData(("" + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
        } catch (Exception e) {
            // ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return false;
        }
        return true;
    }

    private void createReceiptDatafortvsHindi(HPRTPrinterHelper hprtPrinterHelper) {
        try {
            String method = "";
            String gstno = "";
            Bitmap logoData = null;
            Bitmap azadi = BitmapFactory.decodeResource(getResources(), R.drawable.cm);
            Uri file = Uri.parse(mrudetails.get_Meter_Photo());
            if (!Uri.EMPTY.equals(file)) {
                try {
                    // logoData = this.getContentResolver().loadThumbnail(file, new Size(350, 220), null);
                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                        logoData = mContext.getContentResolver().loadThumbnail(file, new Size(200, 200), null);
                    } else {
                        logoData = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), file);
                    }
                    Log.e("Image", logoData.toString());
                    int x = logoData.getWidth();
                    if (x > 220) {
                        logoData = Bitmap.createScaledBitmap(logoData, 350, 220, true);
                    } else {
                        logoData = Bitmap.createScaledBitmap(logoData, 350, 220, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Thread.sleep(100);
            StringBuilder textData = new StringBuilder();

            String header = "        ऊर्जा विपत्र";
            String zeroBill="  \"शून्य बिल\"";
            double billAmt=Double.parseDouble(bill.get_SubTotalB().trim())-Double.parseDouble(bill.get_DPS_CURR().trim());
            if((bill.get_CATEGORY().startsWith("DS")||bill.get_CATEGORY().startsWith("KJ")) && Double.parseDouble(bill.get_PROMPT_AMT())<=0 && billAmt<=0) {
                HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x45, 0x01}); // Bold ON
                HPRTPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x11}); // Double height and width
                HPRTPrinterHelper.WriteData((zeroBill + "\n").getBytes("UTF-8"));
                HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
                Thread.sleep(100);
            }
            // Reset formatting
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x45, 0x00}); // Bold OFF
            HPRTPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x00}); // Font size reset
            HPRTPrinterHelper.WriteData((header + "\n").getBytes("UTF-8"));
            HPRTPrinterHelper.WriteData(("           " + bill.get_COMPANY() + "\n").getBytes("gb2312"));
            HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            if (CommonPref.getUserDetails(PrintHomeActivity.this).get_billmsg().equalsIgnoreCase("Y") && azadi != null) {
                azadi = Bitmap.createScaledBitmap(azadi, 350, 350, true);
                printimage(azadi);
            }
            StringBuilder ntsMsg = new StringBuilder();
            ntsMsg.append("\nसभी घरेलू उपभोक्ताओं से अब\n125 यूनिट तक बिजली खपत पर\nकोई शुल्क नहीं लिया जाएगा।\nयह लाभ जुलाई माह की\nखपत से लागू है।\n");
            hprtPrinterHelper.WriteData((ntsMsg.toString()).getBytes("UTF-8"));
            hprtPrinterHelper.WriteData((" -नीतीश कुमार,मुख्यमंत्री बिहार\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(("-----------------------\n").getBytes("UTF-8"));
            String subsidy=" राज्य सरकार";
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x45, 0x01}); // Bold ON
            HPRTPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x11}); // Double height and width
            hprtPrinterHelper.WriteData((subsidy + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData((" अनुदान: ").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            //---------------------------------------------------------------------------
            String header1 = CommonPref.getUserDetails(mContext).get_DivId().substring(0, 1);
            if (header1.equals("1")) {
                gstno = "10AAECN1588M2ZB";
            } else if (header1.equals("2")) {
                gstno = "10AASCS2207G2ZN";
            }
            // Reset formatting
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x45, 0x00}); // Bold OFF
            HPRTPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x00}); // Font size reset
            textData.append(""+ bill.get_GOV_SUBSIDY().trim() + "\n");
            textData.append("     ******************\n");
            textData.append("GSTIN-" + gstno + "\n");
            textData.append("     ******************");
            // method = "addText";
            hprtPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());


            //mPrinter.addText("OFFER" + "\n");
            //mPrinter.addText("#*#*#*#" + "\n");
            if (Float.parseFloat(bill.get_PROMPT_AMT().trim()) > 0) {
                // textData.append("  *********************  \n");
                textData.append("विपत्र राशि रु." + bill.get_PROMPT_AMT() + " का \nदि. " + bill.get_UPTO_DATE().trim());
                textData.append("ऑनलाइन\n भुगतान करें  एवं रु " + bill.get_ONLINE_REBATE().trim() + "का\n" + "अतिरिक्त छूट पाएं ");
                hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
                hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
                textData.delete(0, textData.length());
                textData.append("     ******************\n");

            }
            //  mPrinter.addTextSize(1, 1);
            //textData.append("Get Extra Rs." +strRebateOnline+ " rebate if Rs."+ bill.get_PROMPT_AMT().trim() +" paid"+"\n"+"online upto " + bill.get_UPTO_DATE().trim()  + "\n" );
            //  textData.append("***********************\n");
            //mmOutputStream.write(bold);

            textData.append("     ऊर्जा विपत्र  : " + monthYear(bill.get_BMONTH().trim(), bill.get_BYEAR().trim()) + "\n");
            textData.append("     ******************\n");
            hprtPrinterHelper.WriteData((textData + "").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());
            if (Float.parseFloat(bill.get_SubTotalA().trim()) > 1000) {
                //  textData.append("प्रिय " + bill.get_CNAME().trim() + ",\n" + "कृपया विदयुत बकाया राशि रू" + bill.get_PROMPT_AMT().trim() + " का\nभुगतान सुचना प्राप्ति के 15 दिनों के\nभीतर सुनिश्चित करें अन्यथा विदयुत \nअधिनियम 2003 के धारा 56 के आलोक में दि." + bill.get_UPTO_DATE().trim() + " के पश्चात विदयुत\nसम्बन्ध विच्छेदित कर दिया जाएगा।" + "\n" + "                 स0 वि0 अभि0" + "\n");
                String str = "प्रिय " + bill.get_CNAME().trim() + ",\n" + "कृपया विदयुत बकाया राशि \n" +
                        bill.get_PROMPT_AMT().trim() + " रू का भुगतान सुचना\nप्राप्ति के 15 दिनों के भीतर\nसुनिश्चित करें अन्यथा विदयुत\nअधिनियम 2003 के धारा 56 के\nआलोक में दि."
                        + bill.get_UPTO_DATE().trim() + " के\nपश्चात विदयुत सम्बन्ध\nविच्छेदित कर दिया जाएगा।" + "\n" + "                 स0 वि0 अभि0";
                //hprtPrinterHelper.WriteData((str + "\n").getBytes("UTF-8"));
                //hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
                textData.append("     ******************\n");
            }
            textData.append("तारीख:" + bill.get_BILL_DT().trim() + "समय:" + bill.get_BILL_TIME().trim() + "\n");
            textData.append("       उपभोक्ता विवरण\n");
            textData.append("     ******************");
            hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());
            textData.append("विपत्र संख्या :" + bill.get_BILL_NO().trim() + "\n");
            textData.append("प्रमंडल     :    " + bill.get_DIV_NAME().trim() + "\n");
            textData.append("अवर प्रमंडल :    " + bill.get_SUB_DIV_NAME().trim() + "\n");
            textData.append("उपभोक्ता  संख्या :" + bill.get_CON_ID().trim() + "\n");
            textData.append("खाता नंबर  :  " + bill.get_ACT_NO().trim() + "\n");
            textData.append("खाता      :  " + CommonPref.getUserDetails(PrintHomeActivity.this).get_MRUNo() + "\n");
            textData.append("उपभोक्ता नाम  :" + bill.get_CNAME().trim() + "\n");
            textData.append("पता       :    ");
            textData.append(bill.get_BILL_ADDRESS().trim() + "\n");
            textData.append("क्षेत्र       :  " + bill.get_AREA_TYPE().trim() + "\n");
            textData.append("पोल सं     :  " + bill.get_POLE_NO().trim() + "\n");
            textData.append("मीटर संख्या  : " + bill.get_METER_NO().trim() + "   PH : " + bill.get_PHASE().trim() + "\n");
            textData.append("मीटर स्वामित्व :  " + bill.get_MET_OWNER().trim() + "\n");
            textData.append("उपभोक्ता श्रेणी  :  " + bill.get_CATEGORY().trim() + "\n");
            if (bill.get_CATEGORY().contains("D")) {
                textData.append("संविदा मांग  :  " + bill.get_CON_DEM().trim() + "\n");

            } else {
                textData.append("स्वीकृत भार  : " + bill.get_SANC_LOAD().trim() + "\n");

            }
            textData.append("जमानत राशि    : " + bill.get_SEC_DEP().trim() + "\n");
            textData.append("विपत्र माह      :  " + bill.get_BILLED_MONTH().trim() + "\n");
            textData.append("     ******************\n");
            textData.append("        मानपठन \n");
            textData.append("     --------------------  ");
            //   method = "addText";
            hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());
            textData.append("           पिछला    " + "वर्तमान  \n");
            textData.append("पठन   :     " + bill.get_PREV_READ().trim() + "         " + bill.get_CUR_READ().trim() + "\n");
            textData.append("तारीख :" + bill.get_PRE_READ_DATE().trim() + "  " + bill.get_BILL_DT().trim() + "\n");
            textData.append("स्थिति :     " + bill.get_PRE_READ_STAT().trim() + "           " + bill.get_READ_STAT().trim());
            hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
            //  HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            textData.delete(0, textData.length());
            if (logoData != null) {
                printimage(logoData);
            }
            textData.append("गुणांक  : " + bill.get_MULTI_FACT().trim() + "   खपत     : " + bill.get_UNITS_CONS() + "\n");
            textData.append("दर्ज मांग:" + bill.get_REC_DEMAND().trim() + " पावर फैक्टर:" + bill.get_POW_FACT().trim() + "\n");
            textData.append("एमएमसी : " + bill.get_MMC_UNIT().trim() + "  औसत  : " + bill.get_AVG_UNIT().trim() + "\n");
            textData.append("घोषित यूनिट : " + bill.get_BILLED_UNIT().trim() + " विपत्र प्रकार:" + bill.get_BILL_TYPE().trim() + "\n");
            textData.append("     ******************");
            hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
            //   HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            textData.delete(0, textData.length());

            //float consumption = (Float.parseFloat(bill.get_CUR_READ().trim()) - Float.parseFloat(bill.get_PREV_READ().trim())) *Float.parseFloat(bill.get_MULTI_FACT().trim());
            if (!bill.get_KEPT_AMOUNT().equalsIgnoreCase("NA")) {
                textData.append("KEPT AMOUNT : " + "Rs." + bill.get_KEPT_AMOUNT());
            }

            hprtPrinterHelper.WriteData((textData + "").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());

            textData.append("        बक़ाया विवरण\n");
            textData.append("     ------------------\n");
            textData.append("ऊर्जा  बक़ाया   :" + addBlankToString1(bill.get_EC_ARREAR().trim()) + "\n");
            textData.append("विद्युत कर बकाया  :" + addBlankToString1(bill.get_EDAREAR().trim()) + "\n");
            textData.append("विळम्ब अधिभार बकाया : " + bill.get_DPS_ARREAR().trim() + "\n");
            Float i = (Float.parseFloat(convertBlankToZero(bill.get_OTH_ARREAR().trim()))) + (Float.parseFloat(convertBlankToZero(bill.getOTH_CGST_AMT().trim()))) + (Float.parseFloat(convertBlankToZero(bill.getOTH_SGCT_AMT().trim())));
            textData.append("अन्य बकाया     :" + addBlankToString1(i.toString()) + "\n");
            // textData.append("अन्य बकाया          :" + addBlankToString(bill.get_OTH_ARREAR().trim()) + "\n");
            //float totalA = Float.parseFloat(convertBlankToZero(bill.get_EC_ARREAR())) + Float.parseFloat(convertBlankToZero(bill.get_DPS_ARREAR())) + Float.parseFloat(convertBlankToZero(bill.get_OTH_ARREAR()));
            textData.append(" कुल बकाया (A)  :" + addBlankToString1(bill.get_SubTotalA().trim()) + "\n");
            textData.append("     ******************");
            //  method = "addText";
            hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());
            textData.append("    वर्तमान विपत्र विवरण\n");
            textData.append("       ------------------\n");
            textData.append("ऊर्जा शुल्क       :" + addBlankToString1(bill.get_EC_CURR().trim()) + "\n");
            textData.append("नियत / मांग शुल्क :" + addBlankToString1(bill.get_FIX_CURR().trim()) + "\n");
            textData.append("आधिक्य  मांग शुल्क:" + addBlankToString1(bill.get_EXC_DEM_CURR().trim()) + "\n");
            textData.append("विद्युत कर       :" + addBlankToString1(bill.get_ED_CURRENT().trim()) + "\n");
            //textData.append("मीटर किराया     :" + addBlankToString1(bill.get_MET_RENT().trim()) + "\n");
            //textData.append("सीजीएसटी  @ 9% :" + addBlankToString1(bill.get_CGST_AMT().trim()) + "\n");
            //textData.append("एसजीएसटी  @ 9% :" + addBlankToString1(bill.get_SGCT_AMT().trim()) + "\n");
            textData.append("कैपेसिटर   शुल्क   :" + addBlankToString1(bill.get_SHUN_CURR().trim()) + "\n");
            textData.append("अन्य  शुल्क       :" + addBlankToString1(bill.get_OTH_CURR().trim()) + "\n");
            textData.append("----- ----------------\n");
            textData.append("राज्य सरकार अनुदान:" + bill.get_GOV_SUBSIDY().trim());
            textData.append("---------------------");
            hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(100);
            textData.delete(0, textData.length());
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x45, 0x01}); // Bold ON
            HPRTPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x11}); // Double height and width
            textData.append("वर्तमान विपत्र\n");
            textData.append("राशि: " + String.format("%.2f",billAmt));
            hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(100);
            textData.delete(0, textData.length());
            //----------------------------------------------------------------
            textData.append("***********************\n");
            if(!(bill.get_CATEGORY().startsWith("DS")||bill.get_CATEGORY().startsWith("KJ"))) {
                textData.append("विळम्ब अधिभार   :" + addBlankToString1(bill.get_DPS_CURR().trim()) + "\n");
                textData.append("ब्याज  SD(C) पर   :" + addBlankToString1(bill.get_INTR_SEC_DEP().trim()) + "\n");
                textData.append("रीमिशन          :" + addBlankToString1(bill.get_Remission_charge().trim()) + "\n");
                textData.append("छूट MMC पर       :" + addBlankToString1(bill.get_REBATE_ON_MMC().trim()) + "\n");
            }
            textData.append("अतिरिक्त तिमाही छूट :" + addBlankToString1("-" + bill.getQUARTERLY_REBATE_POSTED().trim()) + "\n");
            textData.append("TOTAL (A+B+C)   :" + addBlankToString1(bill.get_SubTotalC().trim()) + "\n");
            textData.append("***********************\n");
            textData.append("छूट           :" + addBlankToString1(bill.get_REBATE().trim()));
            //reset Size
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x45, 0x00}); // Bold OFF
            HPRTPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x00}); // Font size reset
            hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(100);
            textData.delete(0, textData.length());
//-----------------------------------------------------
            textData.append("       देय राशि\n");
            textData.append("-----------------------\n");
            textData.append("तक     " + bill.get_UPTO_DATE().trim() + " :" + addBlankToString1(bill.get_PROMPT_AMT().trim()) + "\n");
            textData.append("तक     " + bill.get_BY_DATE().trim() + " :" + addBlankToString1(bill.get_GROSS_AMT().trim()) + "\n");
            textData.append("बाद     " + bill.get_AFTER_DATE().trim() + " :" + addBlankToString1(bill.get_NET_AMT().trim()));

            hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());
         /*   if (!bill.get_KEPT_AMOUNT().equalsIgnoreCase("NA")) {


                textData.append("INSTALMENT AMOUNT ( C ):" + bill.get_KEPT_INST_FLAG() + " " + bill.get_KEPT_INST_AMT().trim() + "\n");
                textData.append("AMOUNT PAYABLE   ( B+C ) : " + bill.get_KEPT_PAY_AMT());

                textData.append("----------------------\n");
            }*/
            //  method = "addText";
            textData.append("     ******************\n");
            textData.append("     अन्य बकाया  विवरण \n");
            textData.append("     ******************\n");

            textData.append("अन्य बकाया          : " + bill.get_OTH_ARREAR().trim() + "\n");
//            textData.append("अन्य बकाया सीजीएसटी  : " + bill.getOTH_CGST_AMT().trim() + "\n");
//            textData.append("अन्य बकाया  एसजीएसटी : " + bill.getOTH_SGCT_AMT().trim());

            hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(100);
            textData.delete(0, textData.length());
            textData.append("     ******************\n");
            textData.append("   अंतिम भुगतान का विवरण \n");
            textData.append("     ******************\n");
            textData.append("अंतिम भुगतान की राशि:" + bill.get_LAST_PAID_AMT().trim() + "\n");
            textData.append("अंतिम भुगतान तिथि :" + bill.get_LAST_PAY_DATE().trim() + "\n");
            textData.append("रसीद संख्या:" + bill.get_LAST_RCPT_NO().trim() + "\n");
            textData.append("मीटर वाचक   :  " + CommonPref.getUserDetails(PrintHomeActivity.this).get_UserID().trim() + "\n");
            textData.append("टोलफ्री हेल्पलाइन नं  :  " + "1912" + "\n");
            //  method = "addText";
            hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            hprtPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x00});
            textData.delete(0, textData.length());
            Bitmap solar = BitmapFactory.decodeResource(getResources(), R.drawable.solar);
            if (CommonPref.getUserDetails(PrintHomeActivity.this).get_billmsg().equalsIgnoreCase("Y") && solar != null) {
                solar = Bitmap.createScaledBitmap(solar, 350, 220, true);
                printimage(solar);
            }
            hprtPrinterHelper.WriteData(("" + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            hprtPrinterHelper.WriteData(("" + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");

            Intent intent = new Intent(PrintHomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
           /* Bundle databundale=new Bundle();
            databundale.putString("actno",mru1.get_CON_ID());
            databundale.putString("name",mru1.get_CNAME());
            databundale.putString("subdivid",mru1.get_SECTION_ID().substring(0,4));
            intent.putExtra("bundle",databundale);*/
            startActivity(intent);
        } catch (Exception e) {
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
    }

    private void createReceiptDataOfflineFortvsHindi(HPRTPrinterHelper hprtPrinterHelper) {
        String method = "";
        String gstno = "";
        Bitmap azadi = BitmapFactory.decodeResource(getResources(), R.drawable.solar);
        StringBuilder textData = new StringBuilder();
        try {
            String website = "";
            String header = CommonPref.getUserDetails(mContext).get_DivId().substring(0, 1);
            if (header.equals("1")) {
                header = "             NBPDCL";
                website = "www.nbpdcl.co.in";
                gstno = "NBGSTNO12457896";
            } else if (header.equals("2")) {
                header = "             SBPDCL";
                website = "www.sbpdcl.co.in";
                gstno = "SBGSTNO12457896";
            } else {
                header = "";
            }
            textData.append(header + "\n");
            textData.append("**********************\n");
            textData.append("खाता नंबर     :   " + bill.get_ACT_NO().trim() + "\n");
            textData.append("प्रमंडल         :   " + CommonPref.getUserDetails(PrintHomeActivity.this).get_DivName().trim() + "\n");
            textData.append("अवर प्रमंडल     :   " + CommonPref.getUserDetails(PrintHomeActivity.this).get_SubdivName().trim() + "\n");
            textData.append("उपभोक्ता  संख्या :   " + bill.get_CON_ID().trim() + "\n");
            textData.append("खाता          :   " + CommonPref.getUserDetails(PrintHomeActivity.this).get_MRUNo() + "\n");
            textData.append("उपभोक्ता नाम   :   " + bill.get_CNAME().trim() + "\n");
            String meterStatus = "";
            String meterReading = "";
            if (bill.get_READ_STAT().toString().trim().equalsIgnoreCase("OK")) {
                meterStatus = "Actual";
                meterReading = bill.get_CUR_READ().toString().trim();
            } else {
                meterStatus = "Average";
                meterReading = "NA";
            }
            hprtPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x00});
            hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());
            textData.append("वर्तमान मीटर की स्थिति : " + meterStatus + " ( " + bill.get_READ_STAT().toString().trim() + " ) " + "\n");
            textData.append("वर्तमान पढ़ने की तारीख : " + bill.get_READ_DATE().trim() + "\n");
            textData.append("वर्तमान रीडिंग (Kwh)   :  " + meterReading + "\n");
            textData.append("पिछले  रीडिंग (Kwh)   :  " + bill.get_PREV_READ().trim() + "\n");
            String msg = "";
            if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("4X")) {
                msg = " अत्यधिक उच्च खपत रिकॉर्ड\n. ";
            } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("NE")) {
                msg = " नकारात्मक बिल\n ";
            } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("LK")) {
                /*  msg = " Reading After More Than 3 Months.  ";*/
                msg = "LK ";
            }
            textData.append("अद्यतन बिल के लिए " + website + "\n पर जाएँ " + "या संबंधित अवर प्रमंडल / प्रमंडल   कार्यालय से संपर्क करें\n");

            textData.append("एरर कोड    :        " + bill.get_AMBI_Flag().toString().trim() + "\n");
            textData.append("मीटर वाचक  :       " + CommonPref.getUserDetails(PrintHomeActivity.this).get_UserID().trim() + "\n");
            textData.append("हेल्पलाइन नं. :       " + "1912" + "\n");
    /*        if (CommonPref.getUserDetails(PrintHomeActivity.this).get_billmsg().equalsIgnoreCase("Y")) {
                textData.append("***********************************\n");
                textData.append(("निर्वाचक सूची  का विशेष संक्षिप्त \n पुनरीक्षण कार्यक्रम,2020 \n" +
                        "मतदाता सत्यापन  कार्यक्रम (इवीपी)  \n" +
                        "   01-09-2019 से 18-11-2019 \n" +
                        "मैं  करूंगा  अपनी प्रविष्टियां \n100 प्रतिशत सही\n\n\n\n\n").getBytes("UTF-8"));
                textData.append("****************************\n");
            }*/
            //    boolean t=conn.isConnected();
            //conn.printData(textData.toString());
            hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            Thread.sleep(50);
            textData.delete(0, textData.length());
            c1 = localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
            if (c1 == 1) {
                Toast.makeText(PrintHomeActivity.this, "Update database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PrintHomeActivity.this, "Error in database", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
    }


    public void LineFeed() {
        try {
            String msg = "     " + "\n" + "     ";
            msg = msg + "\n";
            mmOutputStream.write(msg.getBytes(), 0, msg.getBytes().length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void openfortvs(boolean flag,boolean hindi) throws InterruptedException {
        Thread.sleep(100);
        Thread t = null;
        String address = CommonPref.getPrinterMacAddress(mContext);
        if (!address.isEmpty()) {
            TVSHindi tvsHindi = new TVSHindi(address, flag,hindi);
            t = new Thread(tvsHindi);
            t.run();
        } else {
            Toast.makeText(mContext, "please setup printer first", Toast.LENGTH_SHORT).show();
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

                                    handler.post(new Runnable() {
                                        public void run() {
                                            //       myLabel.setText(data);
                                        }
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

    private boolean CheckBluetoothState() {
        Boolean b = false;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(mContext, "Bluetooth Printer Not available", Toast.LENGTH_SHORT).show();
        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return true;
                }
                if (bluetoothAdapter.isDiscovering()) {
                    //   txt_state.setText("Bluetooth is currently in device discovery process.");
                    Toast.makeText(mContext, "Bluetooth is currently in device discovery process.", Toast.LENGTH_SHORT).show();

                } else {
                    //   txt_state.setText("Bluetooth is Enabled");
                    Toast.makeText(mContext, "Bluetooth is currently in device discovery process.", Toast.LENGTH_SHORT).show();

                    //  btnscanDevice.setEnabled(true);
                    b = true;
                }

            } else {
                // txt_state.setText("Bluetooth is Not Enabled!");
                Intent enableblutooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableblutooth, REQUEST_ENABLE_BT);

            }
        }
        return b;
    }

    private boolean print(Bitmap bitmap) {
        if (mmSocket != null) {
            if (mmSocket.isConnected() && mmOutputStream != null && mmInputStream != null) {
                byte[] center = new byte[]{0x1b, 0x61, 0x01};
                bitmap = Utiilties.decodeSampledBitmapFromBitmap(bitmap, 384);
                try {
                    byte[] pixels = Utiilties.convert(bitmap);
                    byte xH = (byte) ((((bitmap.getWidth() - 1) / 8) + 1) / AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY);
                    byte yL = (byte) (bitmap.getHeight() % AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY);
                    byte yH = (byte) (bitmap.getHeight() / AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY);
                    byte xL = (byte) ((((bitmap.getWidth() - 1) / 8) + 1) % AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY);
                    byte[] command = new byte[]{(byte) 29, (byte) 118, (byte) 48, (byte) 0, xL, xH, yL, yH};
                    mmOutputStream.write(center);
                    mmOutputStream.write(command);
                    mmOutputStream.write(pixels);
                    //toast("Image Printed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //  toast("Printer Not Connect");
            }
        }
        return true;
    }

    private void gotoNext() {
        if (session.checkLogin()) {
            Conneting();
        } else {
            Intent intent = new Intent(getApplicationContext(), Activity_DeviceList.class);
            startActivity(intent);
            //     finish();
        }
    }

    public void Conneting() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (btAdapt.isDiscovering())
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }btAdapt.cancelDiscovery();
        BluetoothDevice btDev = btAdapt.getRemoteDevice(session.getKeyMac());
        try {
            btDev_str = btDev.toString();
            toothAddress = btDev_str;
            pd = ProgressDialog.show(PrintHomeActivity.this, "Please Wait", "Connecting");
            thread = new Thread(() -> {
                // TODO Auto-generated method stub
                try {
                    int portOpen = HPRTPrinterHelper.PortOpen("Bluetooth," + btDev_str);
                    message = new Message();
                    message.what = portOpen;
                    handler_bt.sendMessage(message);
//                            Log.e("", "msg:"+portOpen);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler_bt = new Handler() {
        public void handleMessage(Message msg) {
            pd.dismiss();
            thread = new Thread(() -> {
                // TODO Auto-generated method stub
                try {
                    int portOpen = HPRTPrinterHelper.PortOpen("Bluetooth," + btDev_str);
                    message = new Message();
                    message.what = portOpen;
                    handler_bt.sendMessage(message);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    };
    public void printimage(Bitmap bitmap) {
        bmp_print = bitmap;
        Log.e("bmp_print", String.valueOf(bmp_print));
        //Log.e("bmp_print", Utiilties.BitMapToString(bmp_print));
        df = new DecimalFormat("0.00");
        try {
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x61, 0x01});
            //  Log.e("printnu", String.valueOf(1));
            for (int i = 0; i < 1; i++) {
                //Log.e("bmp_print3", bmp_print.toString());
                HPRTPrinterHelper.PrintBitmap(bmp_print, (byte) 1, (byte) 0, 203);
                HPRTPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
                Thread.sleep(500);

            }
            //恢复居左对齐
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x61, 0x00});
        } catch (Exception e) {
            Log.e("exception", e.getMessage());
            e.printStackTrace();
        }
    }
    class TVSHindi extends Thread {
        private String address = null;
        BluetoothDevice device = null;
        HPRTPrinterHelper hprtPrinterHelper = null;
        Boolean online=null;
        boolean isHindi;
        int portopen=0;
        public TVSHindi(String address1,Boolean b,boolean hindi) {
            this.address = address1;
            Log.e("Bluetooth address is :",address);
            online=b;
            this.isHindi=hindi;
            try {
                 device = mBluetoothAdapter.getRemoteDevice(address);
                 hprtPrinterHelper = new HPRTPrinterHelper();
                 portopen = hprtPrinterHelper.PortOpen("Bluetooth," + address);
                 Log.e("port open :",""+portopen);
                // Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("bluetooth address",address.toString());
            }
        }
        public void run() {
            if(portopen>=0) {
                if (isHindi) {
                    if (online) {
                        createReceiptDatafortvsHindi(hprtPrinterHelper);
                        //createReceiptDatafortvsenglish();
                    } else {
                        createReceiptDataOfflineFortvsHindi(hprtPrinterHelper);
                    }
                }else{
                    if (online) {
                        createReceiptDatafortvsenglish();
                        //createReceiptDatafortvsenglish();
                    } else {
                        createReceiptDataOfflineFortvsenglish();
                    }
                }
            }else{
                Toast.makeText(mContext, "Please Turn on Printer", Toast.LENGTH_SHORT).show();

            }

        }
    }
    private void showPrintLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Print Language");
        String[] languages = {"हिंदी", "English"};

        builder.setItems(languages, (dialog, which) -> {
            if (which == 0) {
                // Hindi selected
                bluetooth(true);
            } else {
                // English selected
                bluetooth(false);
            }

        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


}


