package com.nic.app.biharelectricitybilling.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;

import com.analogics.thermalAPI.Bluetooth_Printer_2inch_prof_ThermalAPI;
import com.analogics.thermalprinter.AnalogicsThermalPrinter;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import com.epson.eposprint.Builder;
import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.Tvsprinter.SharedPrefClass;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.BillDetails;
import com.nic.app.biharelectricitybilling.entity.MRUDetails;
import com.nic.app.biharelectricitybilling.entity.UserDetails;
import com.nic.app.biharelectricitybilling.entity.lk_md_details;
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
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import HPRTAndroidSDK.HPRTPrinterHelper;
import print.ShowMsg;

public class MeterReadingStatusActivity extends Activity implements ReceiveListener, AsyncResponse {
    LinearLayout linReadingStatus, linPowFact, linRecDemand;
    Spinner spReadingStatus, abnormality;
    Boolean flag = false;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    LinearLayout laymeterproperlyfixed, laymeterproperlyfixed1, maxdeemandkva, ll_kwh;
    String stringReadingStatus = "C", stringAcNo, stringPreRead, stringPowFact, stringRecDemd,
            stringIsRecycle = "N", stringabnormality = "0";
    Button btnSubmit;
    requestForBill requestbill;
    LinearLayout readingstatus;
    BluetoothDevice con_dev = null;
    EditText etMeterReading, etPowerFactor, etMaxdemandkw, etMaxdemandkva, et_meter_reading_kwh;
    DataBaseHelper localDBHelper;
    ArrayList<MRUDetails> mruList;
    TextView txtreadingstaus, tvReadingLable;
    String meterproperlyfixed = "";
    BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 99;
    private static final int REQUEST_ENABLE_BT1 = 100;
    String address = null;
    static final UUID MY_UUID = UUID.randomUUID();
    protected String btAddressDir = Environment.getExternalStorageDirectory() + "";
    BluetoothAdapter bluetoothAdapter;
    RadioGroup radioGroup;
    RadioButton radioBtnYes, radioBtnNo;

    private Activity mContext = null;
    //SR424107716
    MRUDetails mru1;
    String IsMeterFixedStatus = "";
    AnalogicsThermalPrinter conn = new AnalogicsThermalPrinter();
    private Printer mPrinter = null;
    String intentFlag = "";
    Spinner meterabnormility;
    TextView msxdeemand, lk_count;
    BillDetails bill;

    String p_reading_status = "";
    String lk_limit = "";
    lk_md_details lkdetails;
    ArrayList<String> spinnerarray = new ArrayList<>();
    Boolean flaglt1 = false;
    TextView error_text;
    String CROP_IMAGE_FILE_PATH = null;
    File myDir = null;
    SharedPrefClass session;
    BluetoothAdapter btAdapt;
    String btDev_str;
    public static String toothAddress = null;
    private ProgressDialog pd;
    private Message message;
    private Thread thread;
    DecimalFormat df = new DecimalFormat("0.00");
    LinearLayout prev;
    private String kvahRead, kwhRead;
    TextView text_kwh, text_kvah;
    long c1;

    //LinearLayout ll_kwh_kvah;
    @Override
    protected void onStart() {
        super.onStart();
        GlobalVariables.isFinishing_MRS = true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_status);
        stringAcNo = getIntent().getStringExtra("ACCOUNT_NO");
        intentFlag = getIntent().getStringExtra("FLAG");
        kvahRead = getIntent().getStringExtra("kvah");
        kwhRead = getIntent().getStringExtra("kwh");
        localDBHelper = new DataBaseHelper(MeterReadingStatusActivity.this);
        localDBHelper = new DataBaseHelper(this);
        try {
            localDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {

            localDBHelper.openDataBase();

        } catch (SQLException sqle) {
            Utiilties.writeIntoLog(Log.getStackTraceString(sqle));
            throw sqle;
        }
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
        actionBar.setTitle(Html
                .fromHtml("<font color='#FFFFFF'>Reading Status</font>"));
        actionBar.setSubtitle(Html.fromHtml("<b style=\"color:White;\">" + (stringAcNo) + "</b> "));
        mContext = MeterReadingStatusActivity.this;

/*        CROP_IMAGE_FILE_PATH = "/BEB/CropImage/"
                + CommonPref.getUserDetails(MeterReadingStatusActivity.this)
                .get_MRUNo() + "/";
        String root=null;
        if(android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.Q){
            root= getApplicationContext().getExternalFilesDir(CROP_IMAGE_FILE_PATH).toString();
            myDir = new File(root);
        }else {
            root = Environment.getExternalStorageDirectory().toString();
            myDir = new File(root+"/"+CROP_IMAGE_FILE_PATH);
        }*/
    /*    if (CommonPref.getPrinterType(MeterReadingStatusActivity.this).equalsIgnoreCase("T")) {
            session = new SharedPrefClass(MeterReadingStatusActivity.this);
            btAdapt = BluetoothAdapter.getDefaultAdapter();
            gotoNext();
        }*/
        requestbill = new requestForBill();
        linReadingStatus = (LinearLayout) findViewById(R.id.lin_reading_status);
        linPowFact = (LinearLayout) findViewById(R.id.linPowFact);
        maxdeemandkva = (LinearLayout) findViewById(R.id.linRecDmnd2);
        linRecDemand = (LinearLayout) findViewById(R.id.linRecDmnd1);
        prev = (LinearLayout) findViewById(R.id.prev);
        spReadingStatus = (Spinner) findViewById(R.id.sp_reading_status);
        abnormality = (Spinner) findViewById(R.id.sp_reading_abnormality);
        etMeterReading = (EditText) findViewById(R.id.et_meter_reading);
        etPowerFactor = (EditText) findViewById(R.id.et_power_factor);
        etMaxdemandkw = (EditText) findViewById(R.id.et_max_demand2);
        et_meter_reading_kwh = (EditText) findViewById(R.id.et_meter_reading_kwh);
        etMaxdemandkva = (EditText) findViewById(R.id.et_max_demand);
        txtreadingstaus = (TextView) findViewById(R.id.readingStatus);
        tvReadingLable = (TextView) findViewById(R.id.tv_reading_lable);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group_meter1);
        readingstatus = (LinearLayout) findViewById(R.id.readingstatus11);
        msxdeemand = (TextView) findViewById(R.id.txtmaxdemand1);
        error_text = (TextView) findViewById(R.id.error_text);
        error_text.setVisibility(View.GONE);
        text_kwh = findViewById(R.id.text_kwh);
        text_kvah = findViewById(R.id.text_kvah);
        //  BesbBharatQrBuilder.Circle circle = null;
        lk_count = (TextView) findViewById(R.id.lkcount);
        radioBtnYes = (RadioButton) findViewById(R.id.yes1);
        laymeterproperlyfixed = findViewById(R.id.meterproperplyfixed);
        ll_kwh = findViewById(R.id.ll_kwh);
        ll_kwh.setVisibility(View.GONE);
        laymeterproperlyfixed1 = (LinearLayout) findViewById(R.id.meterproperplyfixed1);
        radioBtnNo = (RadioButton) findViewById(R.id.no1);
        btnSubmit = (Button) findViewById(R.id.btn_OK);
        lkdetails = localDBHelper.getmdlkdetails(CommonPref.getUserDetails(MeterReadingStatusActivity.this));
        if (intentFlag.equals("1")) {
            spReadingStatus.setVisibility(View.GONE);
            txtreadingstaus.setVisibility(View.VISIBLE);
            linReadingStatus.setVisibility(View.GONE);
            laymeterproperlyfixed.setVisibility(View.GONE);
            laymeterproperlyfixed1.setVisibility(View.GONE);
            stringReadingStatus = "C";
        } else {
            spReadingStatus.setVisibility(View.VISIBLE);
            txtreadingstaus.setVisibility(View.GONE);
            linReadingStatus.setVisibility(View.GONE);
            stringReadingStatus = "C";
        }

        mruList = localDBHelper.getMRU2("ACT_NO", stringAcNo);
        for (MRUDetails mru : mruList) {
            stringPreRead = mru.get_PREVIOUS_READ();
            stringPowFact = mru.get_POW_FACT();
            stringRecDemd = mru.get_REC_DEM();
            p_reading_status = mru.get_Previous_read_stat();
            lk_limit = mru.get_Lk_Limit().trim();
            if (lk_limit.equalsIgnoreCase("N")) {
                //  lk_count.setVisibility(View.VISIBLE);
            }
            mru1 = mru;
        }
   /*     if (lkdetails!=null) {
            if (lkdetails.getLk_count_current() == null || lkdetails.getLk_count() == null) {
                actionBar.setSubtitle("error : on line 226");
                error_text.setVisibility(View.VISIBLE);
                error_text.setText("may lkdetails contains book_no blank on line 228 !");
                Log.e("MetReadStatusActivity", "lkdetails.getSub_div_id() or lkdetails.getCfname() null on line 207");
            } else {
                error_text.setVisibility(View.GONE);
                if (Integer.parseInt(lkdetails.getLk_count_current()) < Integer.parseInt(lkdetails.getLk_count())) {
                    if (lk_limit.equalsIgnoreCase("Y")) {
                        spinnerarray.clear();
                        spinnerarray.add("- Select Status -");
                        spinnerarray.add("OK");
                        spinnerarray.add("Door Lock");
                    } else {
                        spinnerarray.clear();
                        spinnerarray.add("- Select Status -");
                        spinnerarray.add("OK");
                    }
                } else {
                    spinnerarray.clear();
                    spinnerarray.add("- Select Status -");
                    spinnerarray.add("OK");
                }
                if (Integer.parseInt(lkdetails.getMd_count_current()) < (Integer.parseInt(lkdetails.getMd_count()))) {
                    spinnerarray.add("Defective/Burnt");
                }
            }
        }else{
            actionBar.setSubtitle("error on line 253");
            Log.e("error","error on line 207 on MeterReadingStatusActivity");
            error_text.setVisibility(View.VISIBLE);
          //  error_text.setText("may lkdetails contains book_no blank on line 266 !");
            error_text.setText("may lkdetails contains book_no blank on line 266 !");

        }*/

      /*  if (CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_UserID().trim().equalsIgnoreCase("1401MRC132") || CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_UserID().trim().equalsIgnoreCase("1401MRC134") || CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_UserID().trim().equalsIgnoreCase("2141RRF1")) {
            prev.setVisibility(View.VISIBLE);
            EditText edtprev = (EditText) findViewById(R.id.et_prev);
            String displaytext = stringPreRead + "," + p_reading_status;
            edtprev.setText(displaytext);
        }*/
        if (p_reading_status.equalsIgnoreCase("MD")) {
            spinnerarray.clear();
            spinnerarray.add("- Select Status -");
            spinnerarray.add("OK");
            spinnerarray.add("Defective/Burnt");
        } else if (p_reading_status.equalsIgnoreCase("LK")) {
          /*  spinnerarray.clear();
            spinnerarray.add("- Select Status -");
            spinnerarray.add("OK");
            spinnerarray.add("Door Lock");*/
            if (lk_limit.equalsIgnoreCase("Y")) {
                spinnerarray.clear();
                spinnerarray.add("- Select Status -");
                spinnerarray.add("OK");
                spinnerarray.add("Door Lock");
                spinnerarray.add("Defective/Burnt");
            } else {
                spinnerarray.clear();
                spinnerarray.add("- Select Status -");
                spinnerarray.add("OK");
                spinnerarray.add("Defective/Burnt");
            }
        } else {
            spinnerarray.clear();
            spinnerarray.add("- Select Status -");
            spinnerarray.add("OK");
            spinnerarray.add("Door Lock");
            spinnerarray.add("Defective/Burnt");
        }
        /*    else if(lk_limit.equalsIgnoreCase("Y")){
            spinnerarray.clear();
            spinnerarray.add("- Select Status -");
            spinnerarray.add("OK");
            spinnerarray.add("Door Lock");
            spinnerarray.add("Defective/Burnt");
        }else{
            spinnerarray.clear();
            spinnerarray.add("- Select Status -");
            spinnerarray.add("OK");
        }*/
        ArrayAdapter<String> spinnerCountShoesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerarray);
        spReadingStatus.setAdapter(spinnerCountShoesArrayAdapter);
        if (mru1.get_METR_UNMETER().equalsIgnoreCase("UM")) {
            readingstatus.setVisibility(View.GONE);
            stringReadingStatus = "UM";
        } else {
            readingstatus.setVisibility(View.VISIBLE);
        }
        if (mru1.get_CATEGORY().trim().toString().equals("NDS1D") || mru1.get_CATEGORY().trim().toString().equals("NDS2D") || mru1.get_CATEGORY().trim().toString().equals("LTIS1D") || mru1.get_CATEGORY().trim().toString().equals("LTIS2D") || mru1.get_CATEGORY().trim().toString().equals("PWWD") || mru1.get_CATEGORY().trim().toString().equals("LTEV")) {
            tvReadingLable.setText("KVAH Reading");
            maxdeemandkva.setVisibility(View.VISIBLE);
            ll_kwh.setVisibility(View.VISIBLE);
            flaglt1 = true;
        } else {

            tvReadingLable.setText("KWH Reading");
            maxdeemandkva.setVisibility(View.GONE);
            flaglt1 = false;
        }
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // find which radio button is selected
            if (checkedId == R.id.yes1) {
                //    Toast.makeText(MeterReadingStatusActivity.this, "choice: Yes", Toast.LENGTH_SHORT).show();
                IsMeterFixedStatus = "";

                //     Toast.makeText(MeterReadingStatusActivity.this, "" + IsMeterFixedStatus, Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.no1) {
                IsMeterFixedStatus = "Meter Not Properly Fixed";

                //  Toast.makeText(MeterReadingStatusActivity.this, "" + IsMeterFixedStatus, Toast.LENGTH_SHORT).show();

            }
        });
        spReadingStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg0.getItemAtPosition(arg2).toString().equalsIgnoreCase("- Select Status -")) {
                    flag = false;
                    linReadingStatus.setVisibility(View.GONE);
                    stringReadingStatus = "C";
                } else if (arg0.getItemAtPosition(arg2).toString().equalsIgnoreCase("OK")) {
                    flag = true;
                    linReadingStatus.setVisibility(View.VISIBLE);
                    if (stringPowFact.trim().equals("I")) {
                        linPowFact.setVisibility(View.GONE);
                    } else {
                        linPowFact.setVisibility(View.VISIBLE);
                    }
                    if (stringRecDemd.trim().equals("I")) {
                        linRecDemand.setVisibility(View.GONE);
                    } else {
                        if (flaglt1){
                            linRecDemand.setVisibility(View.GONE);
                        }else {
                            linRecDemand.setVisibility(View.VISIBLE);
                        }
                    }

                    stringReadingStatus = "OK";
                } else if (arg0.getItemAtPosition(arg2).toString().equalsIgnoreCase("Door Lock")) {
                    flag = false;
                    linReadingStatus.setVisibility(View.GONE);
                    stringReadingStatus = "LK";
                } else if (arg0.getItemAtPosition(arg2).toString().equalsIgnoreCase("Defective/Burnt")) {
                    flag = false;
                    linReadingStatus.setVisibility(View.GONE);
                    stringReadingStatus = "MD";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                stringReadingStatus = "C";
            }
        });
        abnormality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 == 0) {
                    stringabnormality = "0";
                } else {
                    stringabnormality = getResources().getStringArray(R.array.meter_array)[arg2].trim();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        btnSubmit.setOnClickListener(v -> {
            String pfvalue = null;
            pfvalue = etPowerFactor.getText().toString().trim();
            // TODO Auto-generated method stub
            if (stringabnormality.equalsIgnoreCase("0")) {
                Toast.makeText(MeterReadingStatusActivity.this, "Please select abnormality", Toast.LENGTH_SHORT).show();
            } else {
                if (mru1.get_METR_UNMETER().equalsIgnoreCase("UM")) {
                    SaveDataORSync("", stringPreRead, "", "", stringIsRecycle, stringabnormality);
                    Toast.makeText(MeterReadingStatusActivity.this, "Success", Toast.LENGTH_LONG).show();
                }
                if (flag && stringReadingStatus.equalsIgnoreCase("OK")) {
                    if (!(etMeterReading.getText().toString().trim().equals(""))) {
                        if (Integer.parseInt(etMeterReading.getText().toString().trim()) < Integer.parseInt(stringPreRead)) {
                            AlertDialogForRecycle();
                        } else if (Integer.parseInt(etMeterReading.getText().toString().trim()) == Integer.parseInt(stringPreRead)) {
                            //  Toast.makeText(MeterReadingStatusActivity.this, "Enter correct value of Meter Reading", Toast.LENGTH_LONG).show();
                            stringIsRecycle = "N";
                            if (!(pfvalue.equalsIgnoreCase("null")) && (Double.parseDouble(pfvalue) > 1.0 || Double.parseDouble(pfvalue) < 0)) {
                                Toast.makeText(MeterReadingStatusActivity.this, "Please Check Power Factor Value", Toast.LENGTH_LONG).show();

                            } else {
                                SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), etPowerFactor.getText().toString().trim(), etMaxdemandkw.getText().toString().trim(), stringIsRecycle, stringabnormality);
                            }
                        } else {
                            if (stringPowFact.trim().equals("I") && stringRecDemd.trim().equals("I")) {
                                SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), "", "", stringIsRecycle, stringabnormality);
                                Toast.makeText(MeterReadingStatusActivity.this, "Success", Toast.LENGTH_LONG).show();
                            } else if (stringPowFact.trim().equals("I") && !stringRecDemd.trim().equals("I")) {
                                if (flaglt1 && (et_meter_reading_kwh.getText().toString().trim().isEmpty() || et_meter_reading_kwh.getText().toString().trim().isBlank())) {
                                    Toast.makeText(MeterReadingStatusActivity.this, "Please enter KWH reading ", Toast.LENGTH_LONG).show();
                                }
                               /* else if (flaglt1 && (etMaxdemandkw.getText().toString().trim().isEmpty() || etMaxdemandkw.getText().toString().trim().isBlank())){
                                    Toast.makeText(MeterReadingStatusActivity.this, "Please enter max demand KW", Toast.LENGTH_LONG).show();
                                }*/
                                else if (flaglt1 && (etMaxdemandkva.getText().toString().trim().isEmpty() || etMaxdemandkva.getText().toString().trim().isBlank())) {
                                    Toast.makeText(MeterReadingStatusActivity.this, "Please enter max demand KVA", Toast.LENGTH_LONG).show();
                                } else if (flaglt1 && Float.parseFloat(etMaxdemandkva.getText().toString().trim()) <= 0.0) {
                                    Toast.makeText(MeterReadingStatusActivity.this, "Please enter max demand KVA greater than 0", Toast.LENGTH_LONG).show();
                                } else if (!flaglt1 && !etMaxdemandkw.getText().toString().trim().equals("")) {
                                    SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), "", etMaxdemandkw.getText().toString().trim(), stringIsRecycle, stringabnormality);
                                    Toast.makeText(MeterReadingStatusActivity.this, "Success", Toast.LENGTH_LONG).show();
                                } else if (flaglt1 && Float.parseFloat(etMeterReading.getText().toString().trim()) > Float.parseFloat(et_meter_reading_kwh.getText().toString().trim())) {
                                    SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), "", etMaxdemandkva.getText().toString().trim(), stringIsRecycle, stringabnormality);
                                    Toast.makeText(MeterReadingStatusActivity.this, "Success", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MeterReadingStatusActivity.this, "Please Check KWH < KVAH values", Toast.LENGTH_LONG).show();
                                }

                            } else if (!stringPowFact.trim().equals("I") && stringRecDemd.trim().equals("I")) {
                                if (!etPowerFactor.getText().toString().trim().equals("")) {
                                    if (!(pfvalue.equalsIgnoreCase("null")) && (Double.parseDouble(pfvalue) > 1.0 || Double.parseDouble(pfvalue) < 0.0)) {
                                        Toast.makeText(MeterReadingStatusActivity.this, "Please Check Power Factor Value", Toast.LENGTH_LONG).show();
                                    } else if (!(Double.parseDouble(etPowerFactor.getText().toString().trim()) >= 0.3 || Double.parseDouble(etPowerFactor.getText().toString().trim()) < 1)) {
                                        Toast.makeText(MeterReadingStatusActivity.this, "Please Check Power Factor Value (0.3 to 0.99)", Toast.LENGTH_LONG).show();
                                    } else {
                                        SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), etPowerFactor.getText().toString().trim(), "", stringIsRecycle, stringabnormality);
                                        Toast.makeText(MeterReadingStatusActivity.this, "Success", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(MeterReadingStatusActivity.this, "Please Enter Power Factor", Toast.LENGTH_LONG).show();
                                }
                            } else if (!stringPowFact.trim().equals("I") && !stringRecDemd.trim().equals("I")) {
                                if (!etPowerFactor.getText().toString().trim().equals("")) {
                                    if (!(pfvalue.equalsIgnoreCase("null")) && (Double.parseDouble(pfvalue) > 1.0 || Double.parseDouble(pfvalue) < 0.0)) {
                                        Toast.makeText(MeterReadingStatusActivity.this, "Please Check Power Factor Value", Toast.LENGTH_LONG).show();
                                    } else if (!(Double.parseDouble(etPowerFactor.getText().toString().trim()) >= 0.3 || Double.parseDouble(etPowerFactor.getText().toString().trim()) < 1)) {
                                        Toast.makeText(MeterReadingStatusActivity.this, "Please Check Power Factor Value (0.3 to 0.99)", Toast.LENGTH_LONG).show();
                                    } else {
                                        if (flaglt1 && (et_meter_reading_kwh.getText().toString().trim().isEmpty() || et_meter_reading_kwh.getText().toString().trim().isBlank())) {
                                            Toast.makeText(MeterReadingStatusActivity.this, "Please enter KWH reading ", Toast.LENGTH_LONG).show();
                                        }
                                       /* else if (flaglt1 && (etMaxdemandkw.getText().toString().trim().isEmpty() || etMaxdemandkw.getText().toString().trim().isBlank())){
                                            Toast.makeText(MeterReadingStatusActivity.this, "Please enter max demand KW", Toast.LENGTH_LONG).show();
                                        }*/
                                        else if (flaglt1 && (etMaxdemandkva.getText().toString().trim().isEmpty() || etMaxdemandkva.getText().toString().trim().isBlank())) {
                                            Toast.makeText(MeterReadingStatusActivity.this, "Please enter max demand KVA", Toast.LENGTH_LONG).show();
                                        } else if (flaglt1 && Float.parseFloat(etMaxdemandkva.getText().toString().trim()) <= 0.0) {
                                            Toast.makeText(MeterReadingStatusActivity.this, "Please enter max demand KVA greater than 0", Toast.LENGTH_LONG).show();
                                        } else if (!flaglt1 && !etMaxdemandkw.getText().toString().trim().equals("")) {
                                            SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), "", etMaxdemandkw.getText().toString().trim(), stringIsRecycle, stringabnormality);
                                            Toast.makeText(MeterReadingStatusActivity.this, "Success", Toast.LENGTH_LONG).show();
                                        } else if (flaglt1 && Float.parseFloat(etMeterReading.getText().toString().trim()) > Float.parseFloat(et_meter_reading_kwh.getText().toString().trim())) {
                                            SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), etPowerFactor.getText().toString().trim(), etMaxdemandkva.getText().toString().trim(), stringIsRecycle, stringabnormality);
                                            Toast.makeText(MeterReadingStatusActivity.this, "Success", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(MeterReadingStatusActivity.this, "Please Check  KWH < KVAH value", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(MeterReadingStatusActivity.this, "Please Enter Power Factor", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                String powerfactor = etPowerFactor.getText().toString().trim();
                                String maxdemand = etMaxdemandkw.getText().toString().trim();
                                if (powerfactor.equalsIgnoreCase("") || maxdemand.equalsIgnoreCase("")) {
                                    Toast.makeText(MeterReadingStatusActivity.this, "Please Enter Power Factor Or Max Demand", Toast.LENGTH_LONG).show();
                                } else {
                                    SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), etPowerFactor.getText().toString().trim(), etMaxdemandkw.getText().toString().trim(), stringIsRecycle, stringabnormality);
                                    Toast.makeText(MeterReadingStatusActivity.this, "Success", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(MeterReadingStatusActivity.this, "Enter  value of Meter Reading " + ((flaglt1) ? "KVA" : "KW"), Toast.LENGTH_LONG).show();
                    }
                } else if (stringReadingStatus.equalsIgnoreCase("LK") || stringReadingStatus.equalsIgnoreCase("MD")) {
                    SaveDataORSync(stringReadingStatus, stringPreRead, "", "", stringIsRecycle, stringabnormality);
                    Toast.makeText(MeterReadingStatusActivity.this, "Success", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MeterReadingStatusActivity.this, "Please Select Reading Status", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });

    }

    public void AlertDialogForRecycle() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MeterReadingStatusActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Meter Recycle !!!");
        // Setting Dialog Message
        alertDialog
                .setMessage("Current Reading is Below or Equal to Previous Reading. \n Is Meter Recycle ?");
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.bulb_1);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES",
                (dialog, which) -> {
                    stringIsRecycle = "Y";
                    if (!(stringReadingStatus.equalsIgnoreCase("C")) || stringReadingStatus != null) {
                        if (etPowerFactor.getText().toString().trim().equals("")) {
                            SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), etPowerFactor.getText().toString().trim(), etMaxdemandkw.getText().toString().trim(), stringIsRecycle, stringabnormality);
                        } else {
                            if (Float.parseFloat(etPowerFactor.getText().toString().trim()) >= 0.3 || Float.parseFloat(etPowerFactor.getText().toString().trim()) < 1) {
                                SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), etPowerFactor.getText().toString().trim(), etMaxdemandkw.getText().toString().trim(), stringIsRecycle, stringabnormality);
                            } else {
                                Toast.makeText(mContext, "Power factor must be 0.3 to 0.99 !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(MeterReadingStatusActivity.this, "Select meter reading status", Toast.LENGTH_SHORT).show();
                    }

                });
        alertDialog.setNegativeButton("NO",
                (dialog, which) -> stringIsRecycle = "N");

        // Showing Alert Message
        alertDialog.show();
    }

    public void SaveDataORSync(String ReadingStatus, String MeterRead, String PowerFactor, String MaxDemand, String isRecycle, String UnmeterStatus) {
        //tarrif changing
        if (mru1.get_CATEGORY().trim().toString().equals("NDS1D") || (mru1.get_CATEGORY().trim().toString().equals("NDS2D") && Double.parseDouble(mru1.get_LOAD())>0.5) || mru1.get_CATEGORY().trim().toString().equals("LTIS1D") || mru1.get_CATEGORY().trim().toString().equals("LTIS2D") || mru1.get_CATEGORY().trim().toString().equals("PWWD") || mru1.get_CATEGORY().trim().toString().equals("LTEV")) {
            if (flag) {
                kvahRead = MeterRead;
                kwhRead = et_meter_reading_kwh.getText().toString().trim();
                    if (Integer.parseInt(kwhRead)>Integer.parseInt(kvahRead)){
                        Toast.makeText(mContext, "KVAH Current Read must be greater than KWH Current Read ! Invalid Reading Found !", Toast.LENGTH_SHORT).show();
                        return;
                    }
            }else{
                kvahRead = MeterRead;
                kwhRead="";
            }
        } else {
            kwhRead = MeterRead;
        }
        localDBHelper.saveMeterStatus(ReadingStatus, MeterRead, PowerFactor, MaxDemand, isRecycle, stringAcNo, "P", UnmeterStatus, "", kvahRead, kwhRead);
        if (!Utiilties.isOnline(MeterReadingStatusActivity.this)) {
            updateButtonState(false);
       /*     if (!p_reading_status.equalsIgnoreCase("MD")) {
                if (stringReadingStatus.equalsIgnoreCase("LK")) {
                    int lkcount = Integer.parseInt(lkdetails.getLk_count_current()) + 1;
                    localDBHelper.insertlkmdstatus1("current_lk", lkcount, CommonPref.getUserDetails(MeterReadingStatusActivity.this));
                } else if (stringReadingStatus.equalsIgnoreCase("MD")) {
                    int lkcount = Integer.parseInt(lkdetails.getMd_count_current()) + 1;
                    localDBHelper.insertlkmdstatus1("current_md", lkcount, CommonPref.getUserDetails(MeterReadingStatusActivity.this));
                }
            }*/
            Intent intent = new Intent(MeterReadingStatusActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            //Online Code
            mruList = localDBHelper.getMRU2("ACT_NO", stringAcNo);
            Bitmap thumbnail = null;
            for (MRUDetails mru : mruList) {
                Uri file = Uri.parse(mru.get_Meter_Photo());
                if (!Uri.EMPTY.equals(file)) {
             /*       try {
                        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                            thumbnail = mContext.getContentResolver().loadThumbnail(file, new Size(200, 200), null);
                        } else {
                            thumbnail = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), file);
                        }
                        Log.e("Image", thumbnail.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("exception :", thumbnail.toString());
                    }
                    byte[] photoByte = getbytearray(thumbnail);*/

                    byte[] inputData = null;
                    try {
                        InputStream iStream = getContentResolver().openInputStream(file);
                        inputData = getBytes1(iStream);
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                                thumbnail = mContext.getContentResolver().loadThumbnail(file, new Size(200, 200), null);
                            } else {
                                thumbnail = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), file);
                            }
                            inputData = getbytearray(thumbnail);
                            Log.e("Image", thumbnail.toString());
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            Log.e("exception :", thumbnail.toString());
                        }
                    }
                    mru.set_Meter_Photo_Byte(Base64.encodeToString(inputData, Base64.NO_WRAP));
                } else {
                    mru.set_Meter_Photo_Byte("");
                }
                mru.set_IsMeterFIxedStatus(stringabnormality);
                mru.set_Previous_read_stat(p_reading_status);
                mru.setKvahReading(kvahRead);
                mru.setKwhReading(kwhRead);
                requestbill.delegate = this;
                requestbill.execute(mru);
            }
        }
    }


    private class requestForBill extends AsyncTask<MRUDetails, Void, BillDetails> {
        public AsyncResponse delegate = null;

        public requestForBill() {
        }

        private final ProgressDialog dialog = new ProgressDialog(
                MeterReadingStatusActivity.this);


        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog
                    .setMessage("Please wait. \n  Request for Generate bills...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected BillDetails doInBackground(MRUDetails... param) {
            UserDetails user = CommonPref.getUserDetails(MeterReadingStatusActivity.this);
            BillDetails res1 = WebServiceHelper.BillRequest(param[0], user);
            return res1;
        }

        @Override
        protected void onPostExecute(BillDetails result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                //  updateButtonState(false);
                delegate.processFinish(result);
            }
        }
    }


    @SuppressWarnings("resource")
    public static byte[] convertFileToByteArray(File f) {
        byte[] byteArray = null;
        try {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 8];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
            byteArray = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return byteArray;
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
                    MeterReadingStatusActivity.this);
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

                if (GlobalVariables.isFinishing_MRS)
                    ShowMsg.showResult(code, makeErrorMessage(status), mContext);

                long c;
                if (makeErrorMessage(status).isEmpty() && code == 0 && bill != null) {
                    if (bill.get_ACT_NO() != null) {
                        c = localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");

                    } else {
                        Toast.makeText(MeterReadingStatusActivity.this,
                                "Bill is not generated from server", Toast.LENGTH_LONG).show();
                        c = 0;
                    }
                    if (c > 0) {
                        Toast.makeText(MeterReadingStatusActivity.this,
                                "Update in Local DataBase",
                                Toast.LENGTH_LONG).show();
                        Intent i = new Intent(MeterReadingStatusActivity.this,
                                MainActivity.class);
                        Bundle databundale = new Bundle();
                        databundale.putString("actno", mru1.get_CON_ID());
                        databundale.putString("name", mru1.get_CNAME());
                        databundale.putString("subdivid", mru1.get_SECTION_ID().substring(0, 4));
                        i.putExtra("bundle", databundale);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i);
                    } else {
                        Toast.makeText(MeterReadingStatusActivity.this,
                                "Error in Local Database",
                                Toast.LENGTH_LONG).show();
                    }
                } else if (makeErrorMessage(status).isEmpty() && code == 0 && bill == null) {
                    Intent i = new Intent(MeterReadingStatusActivity.this,
                            MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle databundale = new Bundle();
                    databundale.putString("actno", mru1.get_CON_ID());
                    databundale.putString("name", mru1.get_CNAME());
                    databundale.putString("subdivid", mru1.get_SECTION_ID().substring(0, 4));
                    i.putExtra("bundle", databundale);
                    startActivity(i);
                }
                dispPrinterWarnings(status);
                updateButtonState(true);
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
        EditText edtWarnings = (EditText) findViewById(R.id.edtWarnings);
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

        edtWarnings.setText(warningsMsg);
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
                    if (GlobalVariables.isFinishing_MRS)
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
                    if (GlobalVariables.isFinishing_MRS)
                        ShowMsg.showException(e, "disconnect", mContext);
                    Utiilties.writeIntoLog(Log.getStackTraceString(e));
                }
            });
        }

        finalizeObject();
    }


    private void updateButtonState(boolean state) {

        btnSubmit.setEnabled(state);
    }

    private boolean createReceiptData() {
        String method = "";
        String gstno = "";
        // Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
        // Get our saved file into a bitmap object:
        Bitmap logoData = null;
        Uri file = Uri.parse(mru1.get_Meter_Photo());
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
            //if((bill.get_CATEGORY().startsWith("DS")||bill.get_CATEGORY().startsWith("KJ")) && billAmt<=0) {
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
            textData.append("MRU                 :    " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_MRUNo() + "\n");
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
            textData.append("METER READER            :    " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_UserID().trim() + "\n");
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
        Uri file = Uri.parse(mru1.get_Meter_Photo());
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
            //if((bill.get_CATEGORY().startsWith("DS")||bill.get_CATEGORY().startsWith("KJ")) && billAmt<=0) {
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
            if (CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_billmsg().equalsIgnoreCase("Y") && azadi != null) {
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
            textData.append("खाता          :    " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_MRUNo() + "\n");
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
            textData.append(String.format("%.2f",billAmt) + "\n");
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
            textData.append("मीटर वाचक              :  " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_UserID().trim() + "\n");
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
            } else if (header.equals("2")) {
                header = "SBPDCL";
                website = "www.sbpdcl.co.in";
            } else {
                header = "";
            }
            mPrinter.addText(header + "\n");
            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            mPrinter.addText("******************************\n");


            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("A/C NUMBER    :       " + mru1.get_ACT_NO().trim() + "\n");
            textData.append("DIVISION      :       " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_DivName().trim() + "\n");
            textData.append("SUBDIVISION   :       " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_SubdivName().trim() + "\n");
            textData.append("CON ID        :       " + mru1.get_CON_ID().trim() + "\n");
            textData.append("MRU           :       " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_MRUNo() + "\n");
            textData.append("CONSUMER NAME :   	   " + mru1.get_CNAME().trim() + "\n");
            String meterStatus = "";
            String meterReading = "";
            if (stringReadingStatus.equalsIgnoreCase("OK")) {
                meterStatus = "Actual";
                meterReading = etMeterReading.getText().toString().trim();
            } else {
                meterStatus = "Average";
                meterReading = "NA";
            }
            textData.append("CURR MTR Sts  :       " + meterStatus + " ( " + stringReadingStatus + " ) " + "\n");
            if (mru1.get_Read_Date() == null) {
                textData.append("CURR Rdg Dt   :       " + "NA" + "\n");
            } else {
                textData.append("CURR Rdg Dt   :       " + Utiilties.getDateString("MM/dd/yyyy") + "\n");
            }
            textData.append("CURR Rdg (Kwh):       " + meterReading + "\n");
            textData.append("PREV Rdg (Kwh):       " + mru1.get_PREVIOUS_READ().trim() + "\n");

            if (bill != null && (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("4X") || bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("NE") || bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("LK") || bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("X"))) {
                String msg = "";

                if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("4X")) {
                    msg = "The Bill could not be Printed due to Very High Consumption Recorded. ";
                } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("NE")) {
                    msg = "The Bill could not be Printed due to Negative Bill. ";
                } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("LK")) {
                    msg = " Door  Locked More Than six month  ";
                } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("X")) {
                    msg = "The Bill could not be Printed due to  High Consumption Recorded.  ";
                }

                //  textData.append(/*//*"The Bill could not be Printed due to " +*//**//* msg + "\nKindly visit our website at " + website + " \nOr Concerned Sub-division / division office. \n");
                textData.append("ERROR CODE    :        " + bill.get_AMBI_Flag().toString().trim() + "\n");

            } else {
                textData.append("The Bill could not be generated now, \nPlease collect your Bill from " + header + " \nWebsite after 2 Days. \n");
                //textData.append("CODE          :        7 \n");
                textData.append("Message      :        NETWORK ISSUE \n");
            }


            textData.append("METER READER  :       " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_UserID().trim() + "\n");
            textData.append("TOLLLFREE HELPLINE NO.:" + "1912" + "\n");
     /*       if (CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_billmsg().equalsIgnoreCase("Y")) {
                method = "addTextAlign";
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                textData.append("***********************************\n");
                textData.append("निर्वाचक सूची  का विशेष संक्षिप्त \n पुनरीक्षण कार्यक्रम,2020 \n" +
                        "मतदाता सत्यापन  कार्यक्रम (इवीपी)  \n" +
                        "   01-09-2019 से 18-11-2019 \n" +
                        "मैं  करूंगा  अपनी प्रविष्टियां \n100 प्रतिशत सही\n");
            }*/
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);

        } catch (Exception e) {
            if (GlobalVariables.isFinishing_MRS)
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
            textData.append("प्रमंडल         :   " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_DivName().trim() + "\n");
            textData.append("अवर प्रमंडल     :   " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_SubdivName().trim() + "\n");
            textData.append("उपभोक्ता  संख्या :   " + bill.get_CON_ID().trim() + "\n");
            textData.append("खाता          :   " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_MRUNo() + "\n");
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
            textData.append("मीटर वाचक  :       " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_UserID().trim() + "\n");
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
                Toast.makeText(MeterReadingStatusActivity.this, "Update database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MeterReadingStatusActivity.this, "Error in database", Toast.LENGTH_LONG).show();
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
        File file = null;
        String gstno = "";

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
            textData.append("*****************************\n");
            if (Float.parseFloat(bill.get_PROMPT_AMT().trim()) > 0) {
                String strRebateOnline = bill.get_ONLINE_REBATE();
                textData.append("Get Extra Rs " + strRebateOnline.trim() + " rebate if" + "\n" + "Rs " + bill.get_PROMPT_AMT().trim() + " paid online upto " + "\n" + bill.get_UPTO_DATE().trim() + "\n");
                textData.append("*****************************\n");
            }
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
            textData.append("MRU           : " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_MRUNo() + "\n");
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
           /* if (file.exists()) {
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

                *//*byte c[] = convertBitmapToByteArray(MeterReadingStatusActivity.this,logoData);
               //c=convertBitmapToByteArray(MeterReadingStatusActivity.this,logoData);
                String hexstring=toHexString(c);
                byte[] imageData = printer.prepare2InchImageData_VIP(address,
                        hexstring, 0);
                conn.printData(imageData);*//*
            }*/
            textData.append("\n");
            textData.append("OLD CONSUMPTION  :  " + bill.get_OLD_CONSUMPTION().trim() + "\n");
            textData.append("MF : " + bill.get_MULTI_FACT().trim() + "     CONSUMPTION : " + bill.get_UNITS_CONS() + "\n");
            textData.append("RECORDED DEMAND  : " + bill.get_REC_DEMAND().trim() + " PF:" + bill.get_POW_FACT().trim() + "\n");
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
            textData.append("EXTRA QUARTERLY REBATE :" + addBlankToString1("-" + bill.getQUARTERLY_REBATE_POSTED().trim()) + "\n");

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
            textData.append("METER READER       : " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_UserID().trim() + "\n");
            textData.append("TOLLLFREE HELPLINE NO.: " + "1912" + "\n");
           /* if (CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_billmsg().equalsIgnoreCase("Y")) {
                textData.append("***********************************\n");
                textData.append("निर्वाचक सूची  का विशेष संक्षिप्त \n पुनरीक्षण कार्यक्रम,2020 \n" +
                        "मतदाता सत्यापन  कार्यक्रम (इवीपी)  \n" +
                        "   01-09-2019 से 18-11-2019 \n" +
                        "मैं  करूंगा  अपनी प्रविष्टियां \n100 प्रतिशत सही\n\n\n\n\n");

            }*/
            conn.printData(textData.toString());
            textData.delete(0, textData.length());
            // conn.printData(getbytearray(getbitmap(bill.get_CON_ID(),bill.get_PROMPT_AMT())));
            long c1;
            if (bill.get_ACT_NO() != null) {
                c1 = localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
                Intent intent = new Intent(MeterReadingStatusActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        /*        Bundle databundale=new Bundle();
                databundale.putString("actno",mru1.get_CON_ID());
                databundale.putString("name",mru1.get_CNAME());
                databundale.putString("subdivid",mru1.get_SECTION_ID().substring(0,4));
                intent.putExtra("bundle",databundale);*/
                startActivity(intent);

            } else {
                c1 = 0;
                Toast.makeText(MeterReadingStatusActivity.this, "Bill is not generated from server", Toast.LENGTH_LONG).show();
            }


            if (c1 == 1) {
                Toast.makeText(MeterReadingStatusActivity.this, "Update database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MeterReadingStatusActivity.this, "Error in database", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            if (GlobalVariables.isFinishing_MRS)
                ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return;
        }
        textData = null;
    }

    private String addBlankToString1(String value) {

        int x = value.trim().length();
        for (int i = 0; i < 10 - x; i++) {
            value = " " + value;
        }

        return value;
    }

    private void createReceiptDataOfflineforanalogics() {
        String method = "";
        StringBuilder textData = new StringBuilder();
        Bluetooth_Printer_2inch_prof_ThermalAPI printer = new Bluetooth_Printer_2inch_prof_ThermalAPI();

        if (printer == null) {
            return;
        }

        try {
            String website = "";
            String header = CommonPref.getUserDetails(mContext).get_DivId().substring(0, 1);
            if (header.equals("1")) {
                header = "             NBPDCL";
                website = "www.nbpdcl.co.in";
            } else if (header.equals("2")) {
                header = "             SBPDCL";
                website = "www.sbpdcl.co.in";
            } else {
                header = "";
            }
            textData.append(header + "\n");
            textData.append("******************************\n");
            textData.append("A/C NUMBER    :  " + mru1.get_ACT_NO().trim() + "\n");
            textData.append("DIVISION      :  " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_DivName().trim() + "\n");
            textData.append("SUBDIVISION   :  " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_SubdivName().trim() + "\n");
            textData.append("CON ID        :  " + mru1.get_CON_ID().trim() + "\n");
            textData.append("MRU           :  " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_MRUNo() + "\n");
            textData.append("CONSUMER NAME :  " + mru1.get_CNAME().trim() + "\n");
            String meterStatus = "";
            String meterReading = "";
            if (stringReadingStatus.equalsIgnoreCase("OK")) {
                meterStatus = "Actual";
                meterReading = etMeterReading.getText().toString().trim();
            } else {
                meterStatus = "Average";
                meterReading = "NA";
            }
            textData.append("CURR MTR Sts  :  " + meterStatus + " ( " + stringReadingStatus + " ) " + "\n");
            if (mru1.get_Read_Date() == null) {
                textData.append("CURR Rdg Dt   :  " + "NA" + "\n");
            } else {
                textData.append("CURR Rdg Dt   :  " + Utiilties.getDateString("MM/dd/yyyy") + "\n");
            }

            textData.append("CURR Rdg (Kwh):    " + meterReading + "\n");
            textData.append("PREV Rdg (Kwh):    " + mru1.get_PREVIOUS_READ().trim() + "\n");

            if (bill != null && (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("4X") || bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("NE") || bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("LK"))) {
                String msg = "";
                if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("4X")) {
                    msg = "The Bill could not be Printed due to Very High Consumption Recorded. ";
                } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("NE")) {
                    msg = "The Bill could not be Printed due to Negative Bill. ";
                } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("LK")) {
                    //msg = " Reading After More Than 3 Months.  ";
                }
                textData.append(/*"The Bill could not be Printed due to " +*/ msg + "\nKindly visit our website at " + website + " \nOr Concerned Sub-division / division office. \n");
                textData.append("ERROR CODE    :        " + bill.get_AMBI_Flag().toString().trim() + "\n");
            } else {
                textData.append("The Bill could not be generated now, \nPlease collect your Bill from " + header + " \nWebsite after 2 Days. \n");
                //textData.append("CODE          :        7 \n");
                textData.append("Message        : NETWORK ISSUE \n");
            }
            textData.append("METER READER  : " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_UserID().trim() + "\n");
            textData.append("TOLLLFREE HELPLINE NO. : " + "1912" + "");
      /*      if (CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_billmsg().equalsIgnoreCase("Y")) {

                textData.append("***********************************\n");
                textData.append("निर्वाचक सूची  का विशेष संक्षिप्त \n पुनरीक्षण कार्यक्रम,2020 \n" +
                        "मतदाता सत्यापन  कार्यक्रम (इवीपी)  \n" +
                        "   01-09-2019 से 18-11-2019 \n" +
                        "मैं  करूंगा  अपनी प्रविष्टियां \n100 प्रतिशत सही\n\n\n\n\n");

            }*/
            conn.printData(textData.toString());
            textData.delete(0, textData.length());
            long c1;
            if (bill.get_ACT_NO() != null) {
                c1 = localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
                Intent intent = new Intent(MeterReadingStatusActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
             /*   Bundle databundale=new Bundle();
                databundale.putString("actno",mru1.get_CON_ID());
                databundale.putString("name",mru1.get_CNAME());
                databundale.putString("subdivid",mru1.get_SECTION_ID().substring(0,4));
                intent.putExtra("bundle",databundale);*/
                startActivity(intent);

            } else {
                c1 = 0;
                Toast.makeText(MeterReadingStatusActivity.this, "Bill is not generated from server", Toast.LENGTH_LONG).show();
            }
            if (c1 == 1) {
                Toast.makeText(MeterReadingStatusActivity.this, "Update database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MeterReadingStatusActivity.this, "Error in database", Toast.LENGTH_LONG).show();
            }


        } catch (Exception e) {
            if (GlobalVariables.isFinishing_MRS)
                ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return;
        }

        textData = null;

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
            if (GlobalVariables.isFinishing_MRS)
                ShowMsg.showMsg(makeErrorMessage(status), mContext);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                Utiilties.writeIntoLog(Log.getStackTraceString(ex));
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            if (GlobalVariables.isFinishing_MRS)
                ShowMsg.showException(e, "sendData", mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
                Utiilties.writeIntoLog(Log.getStackTraceString(e));
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
            if (GlobalVariables.isFinishing_MRS)
                ShowMsg.showException(e, "connect", mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return false;
        }
        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        } catch (Exception e) {
            if (GlobalVariables.isFinishing_MRS)
                ShowMsg.showException(e, "beginTransaction", mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));

        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            } catch (Epos2Exception e) {
                Utiilties.writeIntoLog(Log.getStackTraceString(e));
                // Do nothing
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
    public void open(boolean flag) {
        try {

            conn.openBT(address);
            if (flag) {
                createReceiptDataForanalogics();
            } else {
                createReceiptDataOfflineforanalogics();
            }


        } catch (IOException ex) {
            ex.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(ex));
        }
    }

    private void CheckBlueToothState(Boolean flag) {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {

            Toast.makeText(MeterReadingStatusActivity.this, "Bluetooth is NOT Enabled",
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
                            MeterReadingStatusActivity.this,
                            "Bluetooth is currently in device discovery process.",
                            Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(MeterReadingStatusActivity.this, "Bluetooth is Enabled.",
                            Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(MeterReadingStatusActivity.this, "Bluetooth is NOT Enabled.",
                        Toast.LENGTH_LONG).show();
                if (flag) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBlueToothState(true);
        } else if (requestCode == REQUEST_ENABLE_BT1) {
            CheckBlueToothState(false);
        }
    }

    @Override
    protected void onDestroy() {
        GlobalVariables.isFinishing_MRS = false;
        super.onDestroy();
    }


    public void showdialoggogreen(int i, Boolean online) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MeterReadingStatusActivity.this);
        if (i == 1) {
            builder1.setMessage("Consumer Opted for e-bill So, Hard copy of bill is not printing here !!");
            builder1.setCancelable(true);
        } else {
            builder1.setMessage("Do you want to print this Bill ");
            builder1.setCancelable(false);
        }
        builder1.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    dialog.cancel();
                    if (i == 1) {
                        Intent intent = new Intent(MeterReadingStatusActivity.this,
                                MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    } else {
                       showPrintLanguageDialog(online);
                    }
                });

        builder1.setNegativeButton(
                "No",
                (dialog, id) -> {
                    dialog.cancel();
                    Intent intent = new Intent(MeterReadingStatusActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void showPrintLanguageDialog(boolean isOnline) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("प्रिंट भाषा चुनें / Choose Print Language");
        String[] languages = {"हिंदी", "English"};

        builder.setItems(languages, (dialog, which) -> {
            if (which == 0) {
                // Hindi selected
                printBill(isOnline,true);
            } else {
                // English selected
                printBill(isOnline,false);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void printimage(Bitmap bitmap) {
        Bitmap bmp_print = bitmap;
        Log.e("bmp_print", String.valueOf(bmp_print));

        df = new DecimalFormat("0.00");
        try {
            HPRTPrinterHelper.WriteData(new byte[]{0x1B, 0x61, 0x01});
            //  Log.e("printnu", String.valueOf(1));
            for (int i = 0; i < 1; i++) {
                Log.e("bmp_print3", bmp_print.toString());
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

    public void openfortvs(boolean flag,boolean isHindi) {
        //   Thread.sleep(100);
        Thread t = null;
        String address = CommonPref.getPrinterMacAddress(mContext);
        if (!address.isEmpty()) {
            TVSHindi tvsHindi = new TVSHindi(address, flag,isHindi);
            t = new Thread(tvsHindi);
            t.run();
        } else {
            Toast.makeText(mContext, "please setup printer first", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MeterReadingStatusActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    private boolean createReceiptDatafortvsenglish() {
        try {
            Thread.sleep(100);
            String method = "";
            String gstno = "";
            Bitmap logoData = null;
            Bitmap azadi = BitmapFactory.decodeResource(getResources(), R.drawable.cm);
            //Uri azadiuri = Uri.parse("android.resource://com.nic.app.biharelectricitybilling/drawable/azadi");
            Uri file = Uri.parse(mru1.get_Meter_Photo());
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
            //if((bill.get_CATEGORY().startsWith("DS")||bill.get_CATEGORY().startsWith("KJ")) && billAmt<=0) {
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
            if (CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_billmsg().equalsIgnoreCase("Y") && azadi != null) {
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
            textData.append("MRU    :    " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_MRUNo() + "\n");
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
            textData.append("METER READER      :  " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_UserID().trim() + "\n");
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
            Uri file = Uri.parse(mru1.get_Meter_Photo());
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
            //if((bill.get_CATEGORY().startsWith("DS")||bill.get_CATEGORY().startsWith("KJ")) && billAmt<=0) {
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
            if (CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_billmsg().equalsIgnoreCase("Y") && azadi != null) {
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
            textData.append("खाता      :  " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_MRUNo() + "\n");
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
            textData.append("राशि: " +String.format("%.2f",billAmt));
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
            textData.append("मीटर वाचक   :  " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_UserID().trim() + "\n");
            textData.append("टोलफ्री हेल्पलाइन नं  :  " + "1912" + "\n");
            //  method = "addText";
            hprtPrinterHelper.WriteData((textData + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            hprtPrinterHelper.WriteData(new byte[]{0x1D, 0x21, 0x00});
            textData.delete(0, textData.length());
            Bitmap solar = BitmapFactory.decodeResource(getResources(), R.drawable.solar);
            if (CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_billmsg().equalsIgnoreCase("Y") && solar != null) {
                solar = Bitmap.createScaledBitmap(solar, 350, 220, true);
                printimage(solar);
            }
            hprtPrinterHelper.WriteData(("" + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            hprtPrinterHelper.WriteData(("" + "\n").getBytes("UTF-8"));
            hprtPrinterHelper.WriteData(new byte[]{0x1d, 0x0c});
            localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");

            Intent intent = new Intent(MeterReadingStatusActivity.this, MainActivity.class);
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
            textData.append("DIVISION     :    " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_DivName().trim() + "\n");
            textData.append("SUBDIVISION  :    " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_SubdivName().trim() + "\n");
            textData.append("CON ID       :    " + bill.get_CON_ID().trim() + "\n");
            textData.append("MRU          :    " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_MRUNo() + "\n");
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
            textData.append("METER READER  :     " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_UserID().trim() + "\n");
            textData.append("TOLLLFREE HELPLINE NO.  : " + "1912" + "\n");
      /*      if (CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_billmsg().equalsIgnoreCase("Y")) {
                textData.append("***********************************\n");
                textData.append(("निर्वाचक सूची  का विशेष संक्षिप्त \n पुनरीक्षण कार्यक्रम,2020 \n" +
                        "मतदाता सत्यापन  कार्यक्रम (इवीपी)  \n" +
                        "   01-09-2019 से 18-11-2019 \n" +
                        "मैं  करूंगा  अपनी प्रविष्टियां \n100 प्रतिशत सही\n\n\n\n\n").getBytes("UTF-8"));
                textData.append("   **********************   \n");
            }*/
            //    boolean t=conn.isConnected();
            //conn.printData(textData.toString());
            HPRTPrinterHelper.WriteData((textData + "\n").getBytes("gb2312"));
            textData.delete(0, textData.length());
            long c1 = localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
            if (c1 == 1) {
                Toast.makeText(MeterReadingStatusActivity.this, "Update database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MeterReadingStatusActivity.this, "Error in database", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return false;
        }
        textData = null;
        return true;
    }

    private void createReceiptDataOfflineFortvsHindi(HPRTPrinterHelper hprtPrinterHelper) {
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
            textData.append("**********************\n");
            textData.append("खाता नंबर     :   " + bill.get_ACT_NO().trim() + "\n");
            textData.append("प्रमंडल         :   " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_DivName().trim() + "\n");
            textData.append("अवर प्रमंडल     :   " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_SubdivName().trim() + "\n");
            textData.append("उपभोक्ता  संख्या :   " + bill.get_CON_ID().trim() + "\n");
            textData.append("खाता          :   " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_MRUNo() + "\n");
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
            textData.append("मीटर वाचक  :       " + CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_UserID().trim() + "\n");
            textData.append("हेल्पलाइन नं. :       " + "1912" + "\n");
        /*    if (CommonPref.getUserDetails(MeterReadingStatusActivity.this).get_billmsg().equalsIgnoreCase("Y")) {
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
            textData.delete(0, textData.length());
            long c1 = localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
            Thread.sleep(100);
            Intent intent = new Intent(MeterReadingStatusActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
         /*   Bundle databundale=new Bundle();
            databundale.putString("actno",mru1.get_CON_ID());
            databundale.putString("name",mru1.get_CNAME());
            databundale.putString("subdivid",mru1.get_SECTION_ID().substring(0,4));
            intent.putExtra("bundle",databundale);*/
            startActivity(intent);

            if (c1 == 1) {
                Toast.makeText(MeterReadingStatusActivity.this, "Update database", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(MeterReadingStatusActivity.this, "Error in database", Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }

    }

    public byte[] getbytearray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }

    class TVSHindi extends Thread {
        private String address = null;
        BluetoothDevice device = null;
        HPRTPrinterHelper hprtPrinterHelper = null;
        Boolean online = null;
        private boolean isHindi;
        int portopen = 0;

        public TVSHindi(String address1, Boolean b,boolean hindi) {
            this.address = address1;
            Log.e("Bluetooth address is :", address);
            online = b;
            this.isHindi=hindi;
            try {
                device = mBluetoothAdapter.getRemoteDevice(address);
                hprtPrinterHelper = new HPRTPrinterHelper();
                portopen = hprtPrinterHelper.PortOpen("Bluetooth," + address);
                Log.e("port open :", "" + portopen);
                //  thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            if (portopen >= 0) {
                if (isHindi) {
                    if (online) {
                        createReceiptDatafortvsHindi(hprtPrinterHelper);
                    } else {
                        createReceiptDataOfflineFortvsHindi(hprtPrinterHelper);
                    }
                }else{
                    if (online) {
                        createReceiptDatafortvsenglish();
                    } else {
                        createReceiptDataOfflineFortvsenglish();
                    }
                }
            } else {
                Toast.makeText(mContext, "Please Turn on Printer", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void printBill(Boolean billtype,boolean isHindi) {
        String printer_type = CommonPref.getPrinterType(MeterReadingStatusActivity.this);
        //   Log.e("printer type",":"+printer_type);
        //   Toast.makeText(mContext, ""+printer_type, Toast.LENGTH_SHORT).show();
        if (!printer_type.equals("N") && printer_type.equals("E")) {
            if (!runPrintReceiptSequence(billtype,isHindi)) {
            } else {
                Intent intent = new Intent(MeterReadingStatusActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
             /*   Bundle databundale=new Bundle();
                databundale.putString("actno",mru1.get_CON_ID());
                databundale.putString("name",mru1.get_CNAME());
                databundale.putString("subdivid",mru1.get_SECTION_ID().substring(0,4));
                intent.putExtra("bundle",databundale);*/
                startActivity(intent);
            }
        } else if (!printer_type.equals("N") && printer_type.equals("T")) {
            openfortvs(billtype,isHindi);
        } else if (!printer_type.equals("N") && printer_type.equals("A")) {
            {
                updateButtonState(false);
                CheckBlueToothState(billtype);
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
                        open(billtype);
                    } catch (Exception e) {// Catch exception if any
                        System.err.println("Error: " + e.getMessage());
                        Utiilties.writeIntoLog(Log.getStackTraceString(e));
                    }
                } else {
                    Intent intent = new Intent(MeterReadingStatusActivity.this, AnalogicsPrinterSetup.class);
                    startActivity(intent);
                    // finish();
                }
            }
        } else {
            Toast.makeText(mContext, "Please select Printer ", Toast.LENGTH_SHORT).show();
            //  finish();
            Intent intent = new Intent(MeterReadingStatusActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
          /*  Bundle databundale=new Bundle();
            databundale.putString("actno",mru1.get_CON_ID());
            databundale.putString("name",mru1.get_CNAME());
            databundale.putString("subdivid",mru1.get_SECTION_ID().substring(0,4));
            intent.putExtra("bundle",databundale);*/
            startActivity(intent);
        }

    }


    @Override
    public void processFinish(BillDetails result) {
       /* FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCustomKey("Bill result", ""+result);*/
        final AlertDialog alertDialog = new AlertDialog.Builder(
                MeterReadingStatusActivity.this).create();
        updateButtonState(false);
        if (result == null) {
            alertDialog.setTitle("Failed!!");
            alertDialog.setMessage("Error" + result);
            alertDialog.show();
        } else {
            if ((result.get_RESPONSE_MESSAGE().trim().equals("Success")) || (result.get_RESPONSE_MESSAGE().trim().equals("Duplicate Bill"))) {
                try {
                    SQLiteDatabase db = localDBHelper.getReadableDatabase();
                    long c = localDBHelper.insertBillDetails(result);
                  /*  if (!p_reading_status.equalsIgnoreCase("MD")) {
                        if (stringReadingStatus.equalsIgnoreCase("LK")) {
                            int lkcount = Integer.parseInt(lkdetails.getLk_count_current()) + 1;
                            localDBHelper.insertlkmdstatus1("current_lk", lkcount, CommonPref.getUserDetails(MeterReadingStatusActivity.this));
                        } else if (stringReadingStatus.equalsIgnoreCase("MD")) {
                            int lkcount = Integer.parseInt(lkdetails.getMd_count_current()) + 1;
                            localDBHelper.insertlkmdstatus1("current_md", lkcount, CommonPref.getUserDetails(MeterReadingStatusActivity.this));
                        }
                    }*/
                    if (c > 0) {
                        localDBHelper.saveBillStatus("G", stringAcNo);
                        bill = result;
                        //  Log.e("billing agency",CommonPref.getUserDetails(mContext).get_bill_agency());
                        if (CommonPref.getUserDetails(mContext).get_bill_agency().contains("DATA INGENIOUS")) {
                            new Thread(() -> {
                                senddata(result);
                            }).start();
                        }
                        if (CommonPref.getUserDetails(mContext).get_ocr_agency().contains("CORAL")) {
                            new Thread(() -> {
                                senddata_coral(result);
                            }).start();
                        }
                        if (bill.get_Go_Green().equalsIgnoreCase("Y")) {
                            showdialoggogreen(1, true);
                        }
                        if (result.get_AMBI_Flag().toString().trim().equals("R")) {
                            showdialoggogreen(2, true);
                        } else {
                            showdialoggogreen(2, false);
                        }
                    } else {
                        Toast.makeText(MeterReadingStatusActivity.this, "Sqlite Error during inserting Data",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(MeterReadingStatusActivity.this, "Sqlite Error" + ex.getMessage(),
                            Toast.LENGTH_SHORT).show();
//                            Utiilties.writeIntoLog(Log.getStackTraceString(ex));
                }
            } else if (result.get_RESPONSE_MESSAGE().toString().trim().contains("AMBIGUITY")) {
                Long i = localDBHelper.saveBillreason("AMBIGUITY", stringAcNo);
                finish();
            } else if (result.get_RESPONSE_MESSAGE().toString().trim().equalsIgnoreCase("Bill Request Pending for Approval")) {
                Long i = localDBHelper.saveBillreason("PA", stringAcNo);
                alertDialog.setTitle("Info!!");
                alertDialog.setMessage(result.get_RESPONSE_MESSAGE().toString().trim());
                alertDialog.show();
            } else {
                alertDialog.setTitle("Info!!");
                alertDialog.setMessage(result.get_RESPONSE_MESSAGE().toString().trim());
                alertDialog.show();
            }
        }
    }

    public void senddata(BillDetails bill) {
        UserDetails user = CommonPref.getUserDetails(mContext);
        Uri uri = Uri.parse("content://com.datainfosys.bpdcl.provider/Log");
        String data = user.get_UserID() + "," + user.get_SubdivId() + "," + user.get_DivId() + ",Billing," + bill.get_CON_ID() + "," + user.get_MRUNo() + "," + mru1.get_Latitude() + "," + mru1.get_Longitude() + "," + bill.get_READ_DATE() + "," + bill.get_READ_STAT() + "," + bill.get_CUR_READ();
        ContentValues values = new ContentValues();
        try {
            values.put("id", 0);
            values.put("data", data);
            values.put("uploaded", 0);
            ContentResolver contentResolver = getContentResolver();
            Uri newuri = contentResolver.insert(uri, values);
            Log.e("data sent", "" + data);
            android.util.Log.d("NewActivity", "getDataFromIntent: " + newuri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void senddata_coral(BillDetails bill) {
        UserDetails user = CommonPref.getUserDetails(mContext);
        Uri uri = Uri.parse("content://in.coral.met.provider/bill");
        String data = user.get_UserID() + "," + user.get_SubdivId() + "," + user.get_DivId() + ",Billing," + bill.get_CON_ID() + "," + user.get_MRUNo() + "," + mru1.get_Latitude() + "," + mru1.get_Longitude() + "," + bill.get_READ_DATE() + "," + bill.get_READ_STAT() + "," + bill.get_CUR_READ();
        ContentValues values = new ContentValues();
        try {
            values.put("id", 0);
            values.put("data", data);
            values.put("uploaded", 0);
            ContentResolver contentResolver = getContentResolver();
            Uri newuri = contentResolver.insert(uri, values);
            Log.e("data sent", "" + data);
            //  android.util.Log.d("NewActivity", "getDataFromIntent: "+newuri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getBytes1(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}

