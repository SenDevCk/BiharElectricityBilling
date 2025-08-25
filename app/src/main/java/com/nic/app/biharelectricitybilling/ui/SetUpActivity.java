package com.nic.app.biharelectricitybilling.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.Tvsprinter.Activity_DeviceList;
import com.nic.app.biharelectricitybilling.Tvsprinter.SharedPrefClass;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.Establishment_details;
import com.nic.app.biharelectricitybilling.entity.MRUDetails;
import com.nic.app.biharelectricitybilling.entity.Report;
import com.nic.app.biharelectricitybilling.entity.UserDetails;
import com.nic.app.biharelectricitybilling.entity.Versioninfo;
import com.nic.app.biharelectricitybilling.entity.dconsumer_details;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.MarshmallowPermission;
import com.nic.app.biharelectricitybilling.util.Utiilties;
import com.nic.app.biharelectricitybilling.util.imageutils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import HPRTAndroidSDK.HPRTPrinterHelper;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class SetUpActivity extends Activity {
    private ActionBar actionBar;
    Button btnDwnldMRU, btnConfigurePrinter, btnRequestForClosing, btnsendPhoto, btnupateaddress, btndownloadtdc;
    DataBaseHelper localDBHelper;
    ArrayList<MRUDetails> mruList;
    MRUDetails mru1;
    MarshmallowPermission MARSHMALLOW_PERMISSION = null;
    ArrayList<String> PhotoList;
    int printid1 = 0;
    SharedPrefClass session;
    BluetoothAdapter btAdapt;
    String btDev_str;
    public static String toothAddress = null;
    private ProgressDialog pd;
    private Message message;
    private Thread thread;
    BluetoothDevice con_dev = null;
    BluetoothAdapter mBluetoothAdapter;
    File myDir = null;
    String Photopath = "";
    ProgressDialog progressDialog;
    private static final int REQUEST_ENABLE_BT = 99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        // Database Opening
        localDBHelper = new DataBaseHelper(SetUpActivity.this);
        localDBHelper = new DataBaseHelper(this);
        try {
            if (!localDBHelper.checkDataBase()) {
                localDBHelper.createDataBase();
            }
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {

            localDBHelper.openDataBase();

        } catch (SQLException sqle) {

            throw sqle;

        }
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
        session = new SharedPrefClass(SetUpActivity.this);
        btAdapt = BluetoothAdapter.getDefaultAdapter();
        // For displaying title and subtitle and change text color
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>SetUp</font>"));
        mru1 = new MRUDetails();
        btnDwnldMRU = (Button) findViewById(R.id.btn_dwnld_mru);
        btnConfigurePrinter = (Button) findViewById(R.id.btn_configure_printer);
        btnRequestForClosing = (Button) findViewById(R.id.btn_request_closing);
        btnsendPhoto = (Button) findViewById(R.id.btn_photoSend);
        btndownloadtdc = (Button) findViewById(R.id.btn_dwnld_mru_tdc);
        btnupateaddress = (Button) findViewById(R.id.btn_update_address);
        btnConfigurePrinter.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            AlertDialogForPrinter();

        });
     /*   btnsendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mruList = localDBHelper.getMRU("Is_Pending_Bill", "G");
                for (MRUDetails mru : mruList) {
                    File file = new File(Environment.getExternalStorageDirectory()
                            .getPath() + "/BEB/CropImage/" + CommonPref.getUserDetails(SetUpActivity.this).get_MRUNo() + "/" + mru.get_ACT_NO() + ".jpg");
                    if (file.exists()) {
                        byte[] photoByte = MeterReadingStatusActivity.convertFileToByteArray(file);
                        Photopath = (Base64.encodeToString(photoByte, Base64.NO_WRAP));
                        mru1.set_Meter_Photo_Byte(Photopath);
                        //	PhotoList.add(Photopath);
                    }

                    new SendPhoto().execute(mru1.get_Meter_Photo_Byte());

                }*/
                /*for(int i=0;i<=PhotoList.size();i++){
                    new SendPhoto().execute(PhotoList.get(i));
				}
            }
        });*/

        btnDwnldMRU.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            if (Utiilties.isOnline(SetUpActivity.this)) {
                new CheckUpdate().execute();
            }
        });
        btndownloadtdc.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            if (Utiilties.isOnline(SetUpActivity.this)) {

                new getdisconnecetd().execute(
                        CommonPref.getUserDetails(SetUpActivity.this)
                                .get_SubdivId(),
                        CommonPref.getUserDetails(SetUpActivity.this)
                                .get_MRUNo());
            }
        });
        btnupateaddress.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(SetUpActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(SetUpActivity.this);
            }
            builder.setTitle("Delete Previous Data")
                    .setMessage("Are you sure you want to delete this Previous Data?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // continue with delete
                        localDBHelper.deleteblock();
               /* if (!Utiilties.isOnline(User_FirstHomeActivity.this)) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(User_FirstHomeActivity.this);
                    ab.setMessage("Internet Connection is not avaliable.Please Turn ON Network Connection to download data.");
                    ab.setPositiveButton("Turn On Network Connection", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent I = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                            startActivity(I);
                        }
                    });
                    ab.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }

                    });
                    ab.create().getWindow().getAttributes().windowAnimations = R.style.alert_animation;
                    ab.show();
                } else {
                    progressDialog.show();
                    new GetBlockList(CommonPref.getUserDetails(User_FirstHomeActivity.this).get_distcode()).execute();
                }*/
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        // do nothing
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        btnRequestForClosing.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            senmobileno();
            //  sendreason();

        });

    }

    private class CheckUpdate extends AsyncTask<Void, Void, Versioninfo> {
        String imei = null;

        @SuppressWarnings("unused")
        CheckUpdate() {
        }

        private final ProgressDialog dialog = new ProgressDialog(
                SetUpActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Checking for update...");
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }

        @SuppressLint("HardwareIds")
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected Versioninfo doInBackground(Void... Params) {
            TelephonyManager tm = (TelephonyManager) getSystemService(SetUpActivity.this.TELEPHONY_SERVICE);
            if (ContextCompat.checkSelfPermission(SetUpActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            try {
                tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    imei = tm.getDeviceId();
                } else {

                  /*  Settings.Secure.getString(getApplicationContext().getContentResolver(),
                            Settings.Secure.ANDROID_ID);*/
                    //  imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                    //  imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                    imei = Utiilties.getIMEI_forAndroid10(SetUpActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utiilties.writeIntoLog(Log.getStackTraceString(e));
            }
            String version = null;
            try {
                version = getPackageManager().getPackageInfo(getPackageName(),
                        0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Versioninfo versioninfo = WebServiceHelper.CheckVersion(imei,version);
            return versioninfo;
        }

        @Override
        protected void onPostExecute(final Versioninfo versioninfo) {

            final AlertDialog.Builder ab = new AlertDialog.Builder(
                    SetUpActivity.this);
            ab.setCancelable(false);
            if (versioninfo != null && versioninfo.isValidDevice()) {

                CommonPref.setCheckUpdate(SetUpActivity.this,
                        System.currentTimeMillis());
                if (versioninfo != null && !versioninfo.isValidDevice()) {
                    Toast.makeText(SetUpActivity.this,
                            "Your device is not registered !",
                            Toast.LENGTH_LONG).show();
                    dialog.dismiss();

                } else if (versioninfo != null && !versioninfo.isVerUpdated()) {
                    ab.setTitle("Update available");
                    ab.setMessage(Html.fromHtml("Please Update the apk from given url"));
                    ab.setPositiveButton("OK",
                            (dialog1, whichButton) -> {
                                dialog.dismiss();
                                showDailog(ab, versioninfo);

                            });
                    ab.setNegativeButton("Cancel",
                            (dialog1, whichButton) -> {
                                dialog1.dismiss();
                                dialog.dismiss();

                                // showDailog(ab, versioninfo);

                            });
                    ab.show();
                } else {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    new getMRUList().execute(
                            CommonPref.getUserDetails(SetUpActivity.this)
                                    .get_UserID(),
                            CommonPref.getUserDetails(SetUpActivity.this)
                                    .get_password(),
                            CommonPref.getUserDetails(SetUpActivity.this)
                                    .get_IMEI());
                }

            }

        }
    }

    private void showDailog(AlertDialog.Builder ab,
                            final Versioninfo versioninfo) {

        if (!versioninfo.isVerUpdated()) {

            if (versioninfo.getPriority() == 0) {

                //this.dialog.dismiss();
                new getMRUList().execute(
                        CommonPref.getUserDetails(SetUpActivity.this)
                                .get_UserID(),
                        CommonPref.getUserDetails(SetUpActivity.this)
                                .get_password(),
                        CommonPref.getUserDetails(SetUpActivity.this)
                                .get_IMEI());
            } else if (versioninfo.getPriority() == 1) {

                ab.setTitle(versioninfo.getUpdateTile());
                ab.setMessage(versioninfo.getUpdateMsg());

                // ab.setMessage("New version of App is available. Please update the App before proceeding. Do you want to update now?");

                // ab.setMessage(Html
                // .fromHtml("<font color=#000000>New Version of Application is available. Please update the application before proceeding. Do you want to update now?</font>"));
                ab.setPositiveButton("Update",
                        (dialog, whichButton) -> {

                            Intent myWebLink = new Intent(
                                    Intent.ACTION_VIEW);
                            myWebLink.setData(Uri.parse(versioninfo
                                    .getAppUrl()));

                            startActivity(myWebLink);

                            dialog.dismiss();
                        });
                ab.setNegativeButton("Cancel",
                        (dialog, whichButton) -> {
                            // GlobalVariables.isOffline
                            // = true;

                            dialog.cancel();
                        });

                ab.show();

            } else if (versioninfo.getPriority() == 2) {

                ab.setTitle(versioninfo.getUpdateTile());
                ab.setMessage(versioninfo.getUpdateMsg());
                // ab.setMessage("Please update your App its required. Click on Update button");

                ab.setPositiveButton("Update",
                        (dialog, whichButton) -> {
                            Intent myWebLink = new Intent(
                                    Intent.ACTION_VIEW);
                            myWebLink.setData(Uri.parse(versioninfo
                                    .getAppUrl()));
                            startActivity(myWebLink);
                            dialog.dismiss();
                            // finish();
                        });
                ab.show();
            }
        } else {

    /*        if (localDBHelper.getSizeOfMRU() > 0 && CommonPref.getForceClosing(SetUpActivity.this).toString().trim().equals("LOCK")) {
                AlertDialogForMRUExist();
            } else {*/
            new getMRUList().execute(
                    CommonPref.getUserDetails(SetUpActivity.this)
                            .get_UserID(),
                    CommonPref.getUserDetails(SetUpActivity.this)
                            .get_password(),
                    CommonPref.getUserDetails(SetUpActivity.this)
                            .get_IMEI());
            // }
        }
    }

    private class getMRUList extends AsyncTask<String, Void, ArrayList<MRUDetails>> {
        public getMRUList() {
        }

        private final ProgressDialog dialog = new ProgressDialog(
                SetUpActivity.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SetUpActivity.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Please wait. Loading MRU...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected ArrayList<MRUDetails> doInBackground(String... param) {
            ArrayList<MRUDetails> res1 = WebServiceHelper.LoadMRU(param[0], param[1], param[2]);
            return res1;
        }

        @Override
        protected void onPostExecute(ArrayList<MRUDetails> result) {
            if (result != null) {
                if (result.size() == 1) {
                    for (MRUDetails mru : result) {
                        if (result.get(0).get_RESPONSE_MESSAGE().toString().trim().equals("Success")) {
                         /*   File dir = new File(Environment.getExternalStorageDirectory() + "/BEB/CropImage/" + CommonPref.getUserDetails(SetUpActivity.this)
                                    .get_MRUNo() + "/");*/
                      /*  String  CROP_IMAGE_FILE_PATH = "/BEB/CropImage/"
                                    + CommonPref.getUserDetails(SetUpActivity.this)
                                    .get_MRUNo() + "/";
                            String root=null;
                            if(android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.Q){
                                //  root = Environment.getRootDirectory().toString();
                                // root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                                root= getApplicationContext().getExternalFilesDir(CROP_IMAGE_FILE_PATH).toString();
                                myDir = new File(root);
                            }else {
                                root = Environment.getExternalStorageDirectory().toString();
                                myDir = new File(root+"/"+CROP_IMAGE_FILE_PATH);
                            }*/
                            ArrayList<Uri> urilist = localDBHelper.getAllMRUuri();
                            //   deleteRecursive(myDir);
                            if (urilist.size() > 0) {
                                imageutils.delete_Alluri(urilist, getApplicationContext());
                                clearApplicationData();
                            }
                            long c = localDBHelper.insertMru(result, CommonPref.getUserDetails(SetUpActivity.this).get_MRUNo().trim());
                            if (c > 0) {
                                Toast.makeText(SetUpActivity.this, result.size() + " Row inserted", Toast.LENGTH_LONG).show();
                                CommonPref.setForceClosing(SetUpActivity.this, "LOCK");
                                if (this.dialog.isShowing())
                                    this.dialog.dismiss();
                                //  if (new getMRUList()!= null && new getMRUList().getStatus() == AsyncTask.Status.FINISHED) {
                                //START YOUR NEW TASK HERE
                                new Getconsumerconfirmation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "" + result.size());
                                //  }
                            } else {
                                if (this.dialog.isShowing())
                                    this.dialog.dismiss();
                                Toast.makeText(SetUpActivity.this, "Error in local Database", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (this.dialog.isShowing())
                                this.dialog.dismiss();
                            AlertDialogForMruError(mru.get_RESPONSE_MESSAGE().toString().trim());
                        }
                    }
                } else {
                    // File dir = new File(Environment.getExternalStorageDirectory() + "/BEB/CropImage/" + CommonPref.getUserDetails(SetUpActivity.this).get_MRUNo() + "/");
                   /* String  CROP_IMAGE_FILE_PATH = "/BEB/CropImage/"
                            + CommonPref.getUserDetails(SetUpActivity.this)
                            .get_MRUNo() + "/";
                    String root=null;
                    if(android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.Q){
                        //  root = Environment.getRootDirectory().toString();
                        // root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                        root= getApplicationContext().getExternalFilesDir(CROP_IMAGE_FILE_PATH).toString();
                        myDir = new File(root);
                    }else {
                        root = Environment.getExternalStorageDirectory().toString();
                        myDir = new File(root+"/"+CROP_IMAGE_FILE_PATH);
                    }*/
                    ArrayList<Uri> urilist = localDBHelper.getAllMRUuri();
                    //   deleteRecursive(myDir);
                    if (urilist.size() > 0) {
                        imageutils.delete_Alluri(urilist, getApplicationContext());
                    }

                    //  deleteRecursive(myDir);
                    long c = localDBHelper.insertMru(result, CommonPref.getUserDetails(SetUpActivity.this)
                            .get_MRUNo().trim());
                    if (c > 0) {
                        Toast.makeText(SetUpActivity.this, result.size() + " Row inserted", Toast.LENGTH_LONG).show();
                        if (this.dialog.isShowing())
                            this.dialog.dismiss();
                        //  if (new getMRUList()!= null && new getMRUList().getStatus() == AsyncTask.Status.FINISHED) {
                        new Getconsumerconfirmation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "" + result.size());

                    } else {
                        Toast.makeText(SetUpActivity.this, "Error in local Database", Toast.LENGTH_LONG).show();
                        if (this.dialog.isShowing())
                            this.dialog.dismiss();
                    }
                }
            } else {
                Toast.makeText(SetUpActivity.this, "Error occurred in Network ", Toast.LENGTH_LONG).show();
            }
            //   alertDialog.cancel();
        }
    }

    private class getdisconnecetd extends AsyncTask<String, Void, ArrayList<dconsumer_details>> {
        public getdisconnecetd() {
        }

        private final ProgressDialog dialog = new ProgressDialog(
                SetUpActivity.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SetUpActivity.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Please wait. Loading Disconnected Consumer...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected ArrayList<dconsumer_details> doInBackground(String... param) {
            ArrayList<dconsumer_details> res1 = WebServiceHelper.Loaddconsumer(param[0], param[1]);
            return res1;
        }

        @Override
        protected void onPostExecute(ArrayList<dconsumer_details> result) {
            if (result != null) {
                if (!(result.get(0).getMsg_str().equalsIgnoreCase("success"))) {
                    if (this.dialog.isShowing())
                        this.dialog.dismiss();
                    Toast.makeText(SetUpActivity.this, "Error" + result.get(0).getMsg_str(), Toast.LENGTH_SHORT).show();
                } else {
                    long c = localDBHelper.insertdconsumerdetails(result, CommonPref.getUserDetails(SetUpActivity.this)
                            .get_MRUNo().trim());
                    if (c > 0) {
                        Toast.makeText(SetUpActivity.this, result.size() + " Row inserted", Toast.LENGTH_LONG).show();
                        if (this.dialog.isShowing())
                            this.dialog.dismiss();
                    } else {
                        Toast.makeText(SetUpActivity.this, "Error in local Database", Toast.LENGTH_LONG).show();
                        if (this.dialog.isShowing())
                            this.dialog.dismiss();
                    }
                }
            } else {
                Toast.makeText(SetUpActivity.this, "Error occurred in Network ", Toast.LENGTH_LONG).show();
                if (this.dialog.isShowing())
                    this.dialog.dismiss();
            }

        }

    }

    public void AlertDialogForMRUExist() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                SetUpActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("MRU already Exist!!!");
        // Setting Dialog Message
        alertDialog
                .setMessage("You have no permission to download MRU again until your work finishes. ");
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.bulb_1);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("OK",
                (dialog, which) -> {
                    //finish();
                });

        // Showing Alert Message
        alertDialog.show();
    }

    public void AlertDialogForClosing(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                SetUpActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Login Again!!!");
        // Setting Dialog Message
        alertDialog
                .setMessage("" + msg);
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.bulb_1);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("OK",
                (dialog, which) -> {
                    //finish();
                });

        // Showing Alert Message
        alertDialog.show();
    }

    public void AlertDialogForMruError(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                SetUpActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Alert!!!");
        // Setting Dialog Message
        alertDialog
                .setMessage("Mesage :" + msg);
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.bulb_1);
        // Setting Positive "Yes" Button
        alertDialog.setNegativeButton("OK",
                (dialog, which) -> dialog.cancel());

        // Showing Alert Message
        alertDialog.show();
    }


    private void bluetooth(int printid) {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
                return;
            }

            // Android 12+ requires BLUETOOTH_CONNECT permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }

            if (!bluetoothAdapter.isEnabled()) {
                Toast.makeText(this, "Bluetooth about to start.", Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, printid);
                return;
            }

            // Bluetooth is enabled, navigate based on printid
            switch (printid) {
                case 1:
                    startActivity(new Intent(this, ConfigurePrinter.class));
                    break;

                case 2:
                    startActivity(new Intent(this, AnalogicsPrinterSetup.class));
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                    break;

                case 3:
                    gotoNext();  // Custom method
                    break;

                default:
                    Toast.makeText(this, "Unknown printer type", Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Bluetooth error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private class setMRUClosing extends AsyncTask<String, Void, String> {
        public setMRUClosing() {
        }

        private final ProgressDialog dialog = new ProgressDialog(
                SetUpActivity.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SetUpActivity.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Please wait. Requesting For MRU Closing...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... param) {
            String res1 = WebServiceHelper.ForceCloseMRU(param[0], param[1], param[2], param[3]);
            return res1;
        }

        @Override
        protected void onPostExecute(String result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                alertDialog.setTitle("Request for MRU Closing..");
                if (result != null && result.toString().contains("ACCEPTED")) {
                    // localDBHelper.deletemklkdeatils();
                    alertDialog.setMessage("Requesting...");
                    alertDialog.show();
                    Toast.makeText(SetUpActivity.this, result.toString().trim(), Toast.LENGTH_LONG).show();
                    //  ActivityCompat.finishAffinity(SetUpActivity.this);
                    Intent intent = new Intent(SetUpActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else if (result == null) {
                    Toast.makeText(SetUpActivity.this, "Error occurred in Network ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SetUpActivity.this, "" + result.toString().trim(), Toast.LENGTH_LONG).show();
                }
                alertDialog.cancel();
            }

        }
    }

    public void AlertDialogForPrinter() {
        final Dialog dialog = new Dialog(SetUpActivity.this);
        /*	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before*/
        dialog.setContentView(R.layout.printerdialog);
        dialog.setTitle("Please Select Printer");
        // set the custom dialog components - text, image and button
        final RadioButton epson = (RadioButton) dialog.findViewById(R.id.Epson);
        final RadioButton Analogics = (RadioButton) dialog.findViewById(R.id.Analogics);
        final RadioButton tvs = (RadioButton) dialog.findViewById(R.id.tvs);
    /*    if (CommonPref.getUserDetails(SetUpActivity.this).get_UserID().contains("MRC")) {
            // Analogics.setVisibility(View.GONE);
        }*/
        Button Cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        // if button is clicked, close the custom dialog
        Cancel.setOnClickListener(v -> dialog.dismiss());
        Button Ok = (Button) dialog.findViewById(R.id.btn_OK);
        // if button is clicked, close the custom dialog
        Ok.setOnClickListener(v -> {
            if (epson.isChecked() && Analogics.isChecked()) {
                Toast.makeText(SetUpActivity.this, "Please Select One Printer", Toast.LENGTH_SHORT).show();
            } else if (epson.isChecked()) {
                //    Toast.makeText(SetUpActivity.this, "Epson", Toast.LENGTH_SHORT).show();
                // CommonPref.setPrinterType(SetUpActivity.this, "E");
                dialog.dismiss();
                bluetooth(1);
            } else if (Analogics.isChecked()) {
                //  Toast.makeText(SetUpActivity.this, "Analogics", Toast.LENGTH_SHORT).show();
        /*        Intent intent = new Intent(SetUpActivity.this,
                        AnalogicsPrinterSetup.class);
                startActivity(intent);*/
                dialog.dismiss();
                bluetooth(2);
            } else if (tvs.isChecked()) {
                //Toast.makeText(SetUpActivity.this, "Tvs ", Toast.LENGTH_SHORT).show();
             /*   Intent intent = new Intent(SetUpActivity.this,
                        Activity_DeviceList.class);
                startActivity(intent);
                CommonPref.setPrinterType(SetUpActivity.this,"T");*/
                dialog.dismiss();
                bluetooth(3);
            }/*else if (Bixolon.isChecked()) {
                dialog.dismiss();
                bluetooth(2);
            }*/

        });

        dialog.show();
    }

    private class SendPhoto extends AsyncTask<String, Void, String> {

        private final ProgressDialog dialog = new ProgressDialog(
                SetUpActivity.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SetUpActivity.this).create();

        @Override
        protected void onPreExecute() {

            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Photo Sending...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... param) {
            return WebServiceHelper.sendphoto(param[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            if (this.dialog.isShowing()) {
                this.dialog.dismiss();

                Toast.makeText(SetUpActivity.this, "" + result, Toast.LENGTH_SHORT).show();

            }

        }
    }

    public void showdialog() {
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                SetUpActivity.this);
// Setting Dialog Title
        alertDialog2.setTitle("Confirm Mru Closing...");

// Setting Dialog Message
        alertDialog2.setMessage("Are you sure you want Close this Mru?");

// Setting Icon to Dialog
        //	alertDialog2.setIcon(R.drawable.delete);

// Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                (dialog, which) -> {
                    // Write your code here to execute after dialog
                    /*Toast.makeText(SetUpActivity.this,
                            "You clicked on YES", Toast.LENGTH_SHORT)
                            .show();*/
                    //dialog.cancel();
                    showdialog1();
                });

// Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("NO",
                (dialog, which) -> {
                    // Write your code here to execute after dialog
                    /*Toast.makeText(SetUpActivity.this,
                            "You clicked on NO", Toast.LENGTH_SHORT)
                            .show();*/
                    dialog.cancel();
                });

// Showing Alert Dialog
        alertDialog2.show();
    }

    public void showdialogp() {
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                SetUpActivity.this);
// Setting Dialog Title
        alertDialog2.setTitle("Some bills are still in synchronization !!!");

// Setting Dialog Message
        alertDialog2.setMessage("Are you sure you want Close this Mru?");

// Setting Icon to Dialog
        //	alertDialog2.setIcon(R.drawable.delete);

// Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                (dialog, which) -> {
                    // Write your code here to execute after dialog
                    /*Toast.makeText(SetUpActivity.this,
                            "You clicked on YES", Toast.LENGTH_SHORT)
                            .show();*/
                    //dialog.cancel();
                    showdialog1();
                });

// Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("NO",
                (dialog, which) -> {
                    // Write your code here to execute after dialog
                    /*Toast.makeText(SetUpActivity.this,
                            "You clicked on NO", Toast.LENGTH_SHORT)
                            .show();*/
                    dialog.cancel();
                });

// Showing Alert Dialog
        alertDialog2.show();
    }

    public void showdialog1() {
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                SetUpActivity.this);
// Setting Dialog Title
        alertDialog2.setTitle("Confirm Mru Closing...");

// Setting Dialog Message
        alertDialog2.setMessage("If you close Mru you are not able for billing of this Mru" + "\n" + "Are you sure you want Close this Mru?");

// Setting Icon to Dialog
        //	alertDialog2.setIcon(R.drawable.delete);

// Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                (dialog, which) -> {
                    // Write your code here to execute after dialog
                    /*Toast.makeText(SetUpActivity.this,
                            "You clicked on YES", Toast.LENGTH_SHORT)
                            .show();*/
                    if (localDBHelper.getSizeOfMRU() > 0) {
                        String mruname = CommonPref.getUserDetails(SetUpActivity.this).get_MRUNo().toString();
                        if (mruname.equalsIgnoreCase(null) || mruname.equalsIgnoreCase("null") || mruname.length() == 0 || mruname.equalsIgnoreCase("")) {
                            AlertDialogForClosing("Login In Online Mode and then Send To Force Close");
                            // Toast.makeText(SetUpActivity.this, "Login In Online Mode and then Send To Force Close", Toast.LENGTH_LONG).show();
                        } else {
                            new setMRUClosing().execute(
                                    CommonPref.getUserDetails(SetUpActivity.this)
                                            .get_UserID(),
                                    CommonPref.getUserDetails(SetUpActivity.this)
                                            .get_password(),
                                    CommonPref.getUserDetails(SetUpActivity.this)
                                            .get_IMEI(),
                                    CommonPref.getUserDetails(SetUpActivity.this)
                                            .get_MRUNo());
                        }
                    } else {
                        Toast.makeText(SetUpActivity.this, "No MRU Available", Toast.LENGTH_LONG).show();
                    }

                });
// Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("NO",
                (dialog, which) -> {
                    // Write your code here to execute after dialog
                    /*Toast.makeText(SetUpActivity.this,
                            "You clicked on NO", Toast.LENGTH_SHORT)
                            .show();*/
                    dialog.cancel();
                });

// Showing Alert Dialog
        alertDialog2.show();
    }


    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            finish();  // Close activity on cancel
            return;
        }

        switch (requestCode) {
            case 1:
                Toast.makeText(this, "Bluetooth opened successfully", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, ConfigurePrinter.class));
                break;

            case 2:
                startActivity(new Intent(this, AnalogicsPrinterSetup.class));
                break;

            case 3:
                Intent intent = new Intent(this, Activity_DeviceList.class);
                startActivity(intent);
                CommonPref.setPrinterType(this, "T");
                break;

            default:
                Toast.makeText(this, "Unknown request code", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    @SuppressLint("Range")
    public void senmobileno() {
        DataBaseHelper placeData = new DataBaseHelper(SetUpActivity.this);
        SQLiteDatabase db = placeData.getReadableDatabase();
        String tablName = "mobile_dt_table";
        Cursor cursor = db
                .rawQuery(
                        "SELECT  Act_no ,Mobile_no ,Dt_no FROM " + tablName,
                        null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // ((Button) view).setEnabled(false);
                // showToast("Uploading...");
                String[] param = new String[3];
                param[0] = cursor.getString(cursor.getColumnIndex("Act_no"));
                param[1] = cursor.getString(cursor.getColumnIndex("Mobile_no"));
                param[2] = cursor.getString(cursor.getColumnIndex("Dt_no"));
                new updateDtNo().execute(param[1], param[2], param[0]);
            }
        } else {
     /*       Toast.makeText(SetUpActivity.this,
                    "आपके पास कोई फ़ोन नंबर रिकॉर्ड रलंबित नहीं है !", Toast.LENGTH_SHORT).show();*/
            sendreason();
            // showdialog();
        }
        cursor.close();
        db.close();
        //  new updateDtNo().execute(param[1], param[2],param[0]);
    }

    @SuppressLint("Range")
    public void sendreason() {
        DataBaseHelper placeData = new DataBaseHelper(SetUpActivity.this);
        SQLiteDatabase db = placeData.getReadableDatabase();
        String tablName = "MRUDetails";
        Cursor cursor = db
                .rawQuery(
                        "SELECT  CON_ID ,reason ,ACT_NO FROM  MRUDetails where Is_Pending_Bill ='R'",
                        null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // ((Button) view).setEnabled(false);
                // showToast("Uploading...");
                String[] param = new String[3];
                param[0] = cursor.getString(cursor.getColumnIndex("CON_ID"));
                param[1] = cursor.getString(cursor.getColumnIndex("reason"));
                param[2] = cursor.getString(cursor.getColumnIndex("ACT_NO"));
                new updatereason().execute(param[0], param[1], param[2]);
            }
        } else {
     /*       Toast.makeText(SetUpActivity.this,
                    "आपके पास कोई कारण रिकॉर्ड रलंबित नहीं है !", Toast.LENGTH_SHORT).show();*/
            ArrayList<Report> list = placeData.getUntracedReport();
            if (list == null) {
                Toast.makeText(this, "TotalCon or Billed or traced not found !", Toast.LENGTH_SHORT).show();
                Log.e("error on SetupActivity ", "getUntracedReport is returning null");
                actionBar.setSubtitle("getUntracedReport is returning null");
            } else if (list.size() <= 0) {
                Toast.makeText(this, "TotalCon or Billed or traced not found !", Toast.LENGTH_SHORT).show();
                Log.e("error on SetupActivity ", "arraylist size 0 line 994");
                actionBar.setSubtitle("arraylist size 0 line 994");
            } else if (list.get(0).get_TotalCon() == null || list.get(0).get_Billed() == null || list.get(0).get_traced() == null || list.get(0).get_Pending() == null) {
                Toast.makeText(this, "TotalCon or Billed or traced not found !", Toast.LENGTH_SHORT).show();
                Log.e("error on SetupActivity ", "TotalCon or Billed or traced not found on line 944");
                actionBar.setSubtitle("found error on line 944");
            } else {
                if (Integer.parseInt(list.get(0).get_TotalCon()) == ((Integer.parseInt(list.get(0).get_Billed()) + Integer.parseInt(list.get(0).get_traced())))) {
                    showdialog();
                } else if (Integer.parseInt(list.get(0).get_TotalCon()) == ((Integer.parseInt(list.get(0).get_Billed()) + Integer.parseInt(list.get(0).get_traced()) + Integer.parseInt(list.get(0).get_Pending())))) {
                    Toast.makeText(SetUpActivity.this,
                            "Please Synchronize the pending bill", Toast.LENGTH_SHORT).show();
                    showdialogp();
                } else {
                    Toast.makeText(SetUpActivity.this,
                            "Please either bill all consumer or give some reason to unbilled", Toast.LENGTH_SHORT).show();
                }
            }


        }
        cursor.close();
        db.close();
        //  new updateDtNo().execute(param[1], param[2],param[0]);
    }

    private class updateDtNo extends AsyncTask<String, Void, String> {
        String Acc_no = "";

        public updateDtNo() {

        }

        private final ProgressDialog dialog = new ProgressDialog(
                SetUpActivity.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SetUpActivity.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Please wait. \n Updating DT and Mobile Number...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... param) {
            UserDetails user = CommonPref.getUserDetails(SetUpActivity.this);
            Acc_no = param[2];
            String res1 = WebServiceHelper.UpdateMRU(param[0], param[1], param[2], user);
            return res1;
        }

        @Override
        protected void onPostExecute(String result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                if (result != null) {
                    if (result.trim().equalsIgnoreCase("Update Successfull")) {
                        long c = localDBHelper.deletemoiledtno(Acc_no);
                        Toast.makeText(SetUpActivity.this, result, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SetUpActivity.this, result, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SetUpActivity.this, "Error in Network",
                            Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    private class updatereason extends AsyncTask<String, Void, String> {
        String Acc_no = "";

        public updatereason() {

        }
        private final ProgressDialog dialog = new ProgressDialog(
                SetUpActivity.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SetUpActivity.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Please wait. \n Updating Reason ...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }
        @Override
        protected String doInBackground(String... param) {
            UserDetails user = CommonPref.getUserDetails(SetUpActivity.this);
            Acc_no = param[2];
            String res1 = WebServiceHelper.Updatereason(param[0], param[1], user);
            return res1;
        }

        @Override
        protected void onPostExecute(String result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                if (result != null) {
                    if (result.trim().contains("SUCCESS")) {
                        long c = localDBHelper.updatereasonstatus(Acc_no, "");
                        Toast.makeText(SetUpActivity.this, result, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SetUpActivity.this, result, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SetUpActivity.this, "Error in Network",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private class Getconsumerconfirmation extends AsyncTask<String, Void, String> {

        private Getconsumerconfirmation() {
        }

        private final ProgressDialog dialog = new ProgressDialog(SetUpActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Getting Download Confirmation.\r\nPlease wait...");
            dialog.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            UserDetails user = CommonPref.getUserDetails(SetUpActivity.this);
            return WebServiceHelper.GetMrudownloadconfirmation(user, params[0]);
        }
        @Override
        protected void onPostExecute(String result1) {
            super.onPostExecute(result1);
            if (result1 != null && result1.contains("SUCCESS")) {
                localDBHelper.updateMrustatus(CommonPref.getUserDetails(SetUpActivity.this).get_MRUNo());
                String[] value_split = result1.split(Pattern.quote("|"));
                CommonPref.settotalconsumer(SetUpActivity.this, value_split[1], value_split[5]);
                localDBHelper.insertlkmdstatus(value_split, CommonPref.getUserDetails(SetUpActivity.this));
                if (dialog.isShowing()) dialog.dismiss();
             /*   Toast.makeText(SetUpActivity.this, "" + result1, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SetUpActivity.this, LoginActivity.class);
                i.setFlags(FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
                finish();*/
                new GetallEstablishment_type().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "" );

            } else {
                if (dialog.isShowing())
                    dialog.dismiss();
                Toast.makeText(SetUpActivity.this, "" + result1, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class GetallEstablishment_type extends AsyncTask<String, Void, ArrayList<Establishment_details>> {

        private GetallEstablishment_type() {
        }

        private final ProgressDialog dialog = new ProgressDialog(SetUpActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Getting Download Data.\r\nPlease wait...");
            dialog.show();
            super.onPreExecute();
        }
        @Override
        protected ArrayList<Establishment_details> doInBackground(String... params) {

            return WebServiceHelper.GetAllestablishment_type();
        }
        @Override
        protected void onPostExecute(ArrayList<Establishment_details> result1) {
            super.onPostExecute(result1);
            if (result1 != null && result1.size()>1) {
                localDBHelper.insertestablishment(result1,"");
                if (dialog.isShowing()) dialog.dismiss();
                Toast.makeText(SetUpActivity.this, "" + result1.size(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SetUpActivity.this, LoginActivity.class);
                i.setFlags(FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
                finish();
            } else {
                if (dialog.isShowing())
                    dialog.dismiss();
                Toast.makeText(SetUpActivity.this, "" + result1, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // do something on back.
            //Display alert message when back button has been pressed
            Intent i = new Intent(SetUpActivity.this, MainActivity.class);
            i.setFlags(FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void gotoNext() {
        Intent intent=null;
        Log.e("version","SDK_INT : "+android.os.Build.VERSION.SDK_INT+" VERSION_CODES: "+Build.VERSION_CODES.TIRAMISU +" : "+(android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU));
        intent = new Intent(SetUpActivity.this, Activity_DeviceList.class);
        startActivity(intent);
    }

    public void Conneting() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        if (btAdapt.isDiscovering())
            btAdapt.cancelDiscovery();
              BluetoothDevice btDev = btAdapt.getRemoteDevice(session.getKeyMac());
        try {
            btDev_str = btDev.toString();
            toothAddress = btDev_str;
            pd = ProgressDialog.show(SetUpActivity.this, "Please Wait", "Connecting");
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
            // Log.e("", "1msg:" + msg.what);
            if (msg.what == 0) {
                try {
//					HPRTPrinterHelper.PrintText("打印测试1234567890\n");
                    Intent mIntent = new Intent(SetUpActivity.this, MainActivity.class);
                    pd.dismiss();// 关闭ProgressDialog
                    startActivity(mIntent);
                    SetUpActivity.this.finish();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                /*Toast.makeText(getApplicationContext(),
                        "Failed", Toast.LENGTH_SHORT).show();*/
                pd.dismiss();// 关闭ProgressDialog
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
            }
//        	 Intent intent = new Intent();
//            intent.putExtra("is_connected", ((msg.what==0)?"OK":"NO"));
//            intent.putExtra("BTAddress", toothAddress);
//            setResult(HPRTPrinterHelper.ACTIVITY_CONNECT_BT, intent);
        }

        ;


    };
    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("EEEEEERRRRRROOOOOOORRRR", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            int i = 0;
            while (i < children.length) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
                i++;
            }
        }

        assert dir != null;
        return dir.delete();
    }
}


