package com.nic.app.biharelectricitybilling.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.analogics.thermalAPI.Bluetooth_Printer_2inch_prof_ThermalAPI;
import com.analogics.thermalprinter.AnalogicsThermalPrinter;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.BillDetails;
import com.nic.app.biharelectricitybilling.entity.MRUDetails;
import com.nic.app.biharelectricitybilling.entity.UserDetails;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.Utiilties;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
import jpos.JposException;
import jpos.POSPrinterConst;
import print.ShowMsg;

public class MeterReadingStatusActivityForApl extends Activity implements ReceiveListener {
    private ActionBar actionBar;
    LinearLayout linReadingStatus, linPowFact, linRecDemand;
    Spinner spReadingStatus;
    Boolean flag = false;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    LinearLayout laymeterproperlyfixed, laymeterproperlyfixed1;
    String stringReadingStatus, stringAcNo, stringPreRead, stringPowFact, stringRecDemd,
            stringIsRecycle = "N";
    Button btnSubmit;
    private static String CROP_IMAGE_FILE_PATH;

    BluetoothDevice con_dev = null;
    String fname;
    EditText etMeterReading, etPowerFactor, etMaxdemand;
    DataBaseHelper localDBHelper;
    ArrayList<MRUDetails> mruList;
    TextView txtreadingstaus, tvReadingLable;
    String meterproperlyfixed = "";
    BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 99;
    private static final int REQUEST_ENABLE_BT1 = 100;
    String address = null;
    static final UUID MY_UUID = UUID.randomUUID();
    protected String btAddressDir = Environment.getExternalStorageDirectory()
            + "";
    BluetoothAdapter bluetoothAdapter;
    RadioGroup radioGroup;
    RadioButton radioBtnYes, radioBtnNo;
    private static String IMAGE_NAME;
    private Context mContext = null;
    //SR424107716
    MRUDetails mru1;
    String IsMeterFixedStatus = "";
    AnalogicsThermalPrinter conn = new AnalogicsThermalPrinter();
    private Printer mPrinter = null;
    String intentFlag = "";
    BillDetails bill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_status);
        stringAcNo = getIntent().getStringExtra("ACCOUNT_NO");
        intentFlag = getIntent().getStringExtra("FLAG");
        // Database Opening
        localDBHelper = new DataBaseHelper(MeterReadingStatusActivityForApl.this);
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

        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));

        // For displaying title and subtitle and change text color
        actionBar.setTitle(Html
                .fromHtml("<font color='#FFFFFF'>Reading Status</font>"));
        mContext = this;

        linReadingStatus = (LinearLayout) findViewById(R.id.lin_reading_status);
        linPowFact = (LinearLayout) findViewById(R.id.linPowFact);
        linRecDemand = (LinearLayout) findViewById(R.id.linRecDmnd1);
        spReadingStatus = (Spinner) findViewById(R.id.sp_reading_status);
        etMeterReading = (EditText) findViewById(R.id.et_meter_reading);
        etPowerFactor = (EditText) findViewById(R.id.et_power_factor);
        etMaxdemand = (EditText) findViewById(R.id.et_max_demand);
        txtreadingstaus = (TextView) findViewById(R.id.readingStatus);
        tvReadingLable = (TextView) findViewById(R.id.tv_reading_lable);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group_meter1);
        radioBtnYes = (RadioButton) findViewById(R.id.yes1);
        laymeterproperlyfixed = (LinearLayout) findViewById(R.id.meterproperplyfixed);
        laymeterproperlyfixed1 = (LinearLayout) findViewById(R.id.meterproperplyfixed1);
        radioBtnNo = (RadioButton) findViewById(R.id.no1);
        btnSubmit = (Button) findViewById(R.id.btn_OK);
        if (intentFlag.equals("1")) {
            spReadingStatus.setVisibility(View.GONE);
            txtreadingstaus.setVisibility(View.VISIBLE);
            linReadingStatus.setVisibility(View.GONE);
            laymeterproperlyfixed.setVisibility(View.GONE);
            laymeterproperlyfixed1.setVisibility(View.GONE);
            stringReadingStatus = "";
        } else {
            spReadingStatus.setVisibility(View.VISIBLE);
            txtreadingstaus.setVisibility(View.GONE);
            linReadingStatus.setVisibility(View.VISIBLE);
            laymeterproperlyfixed.setVisibility(View.VISIBLE);
            laymeterproperlyfixed1.setVisibility(View.VISIBLE);
        }
        mruList = localDBHelper.getMRU2("ACT_NO", stringAcNo);
        for (MRUDetails mru : mruList) {
            stringPreRead = mru.get_PREVIOUS_READ();
            stringPowFact = mru.get_POW_FACT();
            stringRecDemd = mru.get_REC_DEM();
            mru1 = mru;
        }
        IMAGE_NAME = mru1.get_CON_ID() + ".jpg";

        if (mru1.get_CATEGORY().trim().toString().equals("LTIS1D") || mru1.get_CATEGORY().trim().toString().equals("LTIS2D") || mru1.get_CATEGORY().trim().toString().equals("PWWD")) {
            tvReadingLable.setText("KVAH Reading");
        } else {
            tvReadingLable.setText("KWH Reading");
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes1) {
                    //   Toast.makeText(getApplicationContext(), "choice: Yes", Toast.LENGTH_SHORT).show();
                    IsMeterFixedStatus = "";

                    //   Toast.makeText(getApplicationContext(), "" + IsMeterFixedStatus, Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.no1) {
                    IsMeterFixedStatus = "Meter Not Properly Fixed";

                    // Toast.makeText(getApplicationContext(), "" + IsMeterFixedStatus, Toast.LENGTH_SHORT).show();

                }
            }
        });
        spReadingStatus
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        if (arg2 == 0) {
                            flag = false;
                            linReadingStatus.setVisibility(View.GONE);
                            stringReadingStatus = "";
                        } else if (arg2 == 1) {
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
                                linRecDemand.setVisibility(View.VISIBLE);
                            }
                            stringReadingStatus = "OK";
                        } else if (arg2 == 2) {
                            flag = false;
                            linReadingStatus.setVisibility(View.GONE);
                            stringReadingStatus = "LK";
                        } else if (arg2 == 3) {
                            flag = false;
                            linReadingStatus.setVisibility(View.GONE);
                            stringReadingStatus = "MD";
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });

        btnSubmit.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            if (intentFlag.equals("1")) {
                SaveDataORSync("", stringPreRead, "", "", stringIsRecycle, IsMeterFixedStatus);
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
            } else if (!stringReadingStatus.equals("")) {
                if (flag) {
                    if (!etMeterReading.getText().toString().trim().equals("")) {
                        if (Float.valueOf(etMeterReading.getText().toString().trim()) <= Float.valueOf(stringPreRead)) {
                            AlertDialogForRecycle();
                        } else {
                            if (stringPowFact.trim().equals("I") && stringRecDemd.trim().equals("I")) {
                                SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), "", "", stringIsRecycle, IsMeterFixedStatus);
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                            } else if (stringPowFact.trim().equals("I") && !stringRecDemd.trim().equals("I")) {
                                if (!etMaxdemand.getText().toString().trim().equals("")) {
                                    SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), "", etMaxdemand.getText().toString().trim(), stringIsRecycle, IsMeterFixedStatus);
                                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please Enter Max Demand", Toast.LENGTH_LONG).show();
                                }

                            } else if (!stringPowFact.trim().equals("I") && stringRecDemd.trim().equals("I")) {
                                if (!etPowerFactor.getText().toString().trim().equals("")) {
                                    SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), etPowerFactor.getText().toString().trim(), "", stringIsRecycle, IsMeterFixedStatus);
                                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please Enter Max Demand", Toast.LENGTH_LONG).show();
                                }
                            } else if (!stringPowFact.trim().equals("I") && !stringRecDemd.trim().equals("I")) {
                                if (!etPowerFactor.getText().toString().trim().equals("") && !etMaxdemand.getText().toString().trim().equals("")) {
                                    SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), etPowerFactor.getText().toString().trim(), etMaxdemand.getText().toString().trim(), stringIsRecycle, IsMeterFixedStatus);
                                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please Enter Power Factor Or Max Demand", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                String powerfactor = etPowerFactor.getText().toString().trim();
                                String maxdemand = etMaxdemand.getText().toString().trim();
                                if (powerfactor.equalsIgnoreCase("") || maxdemand.equalsIgnoreCase("")) {
                                    Toast.makeText(getApplicationContext(), "Please Enter Power Factor Or Max Demand", Toast.LENGTH_LONG).show();
                                } else {
                                    SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), etPowerFactor.getText().toString().trim(), etMaxdemand.getText().toString().trim(), stringIsRecycle, IsMeterFixedStatus);
                                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Enter the value of Meter Reading",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    SaveDataORSync(stringReadingStatus, stringPreRead, "", "", stringIsRecycle, IsMeterFixedStatus);
                    Toast.makeText(getApplicationContext(), "Success",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Please Select Reading Status", Toast.LENGTH_LONG)
                        .show();
            }
        });

    }

    public void AlertDialogForRecycle() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MeterReadingStatusActivityForApl.this);
        // Setting Dialog Title
        alertDialog.setTitle("Meter Recycle !!!");
        // Setting Dialog Message
        alertDialog
                .setMessage("Current Reading is Below or Equal to Previous Reading. \n Is Meter Recycle ?");
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.bulb_1);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        stringIsRecycle = "Y";
                        SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), etPowerFactor.getText().toString().trim(), etMaxdemand.getText().toString().trim(), stringIsRecycle, IsMeterFixedStatus);

                    }
                });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        stringIsRecycle = "N";
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    public void SaveDataORSync(String ReadingStatus, String MeterRead, String PowerFactor, String MaxDemand, String isRecycle, String UnmeterStatus) {

        localDBHelper.saveMeterStatus(ReadingStatus, MeterRead, PowerFactor, MaxDemand, isRecycle, stringAcNo, "P", UnmeterStatus,"","","");
        if (!Utiilties.isOnline(MeterReadingStatusActivityForApl.this)) {
            //Offline code
            /*Intent intent = new Intent(getBaseContext(),BillingHomeActivity.class);
            startActivity(intent);
			finish();*/
            updateButtonState(false);
            Intent intent = new Intent(getBaseContext(), BillingHomeActivity.class);
            startActivity(intent);
            finish();
           /* if (CommonPref.getPrinterType(getApplicationContext()).equals("E")) {
                if (!runPrintReceiptSequence(false)) {
                    updateButtonState(true);
                }
            } else if (CommonPref.getPrinterType(getApplicationContext()).equals("A")) {
                {
                    updateButtonState(false);
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

                        }

                    } else {

                        Intent intent = new Intent(MeterReadingStatusActivity.this,
                                AnalogicsPrinterSetup.class);
                        startActivity(intent);
                        // finish();

                    }

                }
            }*/
        } else {
            //Online Code
          /*  mruList = localDBHelper.getMRU2("ACT_NO", stringAcNo);
            for (MRUDetails mru : mruList) {
                File file = new File(Environment.getExternalStorageDirectory()
                        .getPath() + "/BEB/CropImage/" + CommonPref.getUserDetails(getApplicationContext()).get_MRUNo() + "/" + mru.get_CON_ID() + ".jpg");
                if (file.exists()) {
                    byte[] photoByte = convertFileToByteArray(file);
                    mru.set_Meter_Photo_Byte(Base64.encodeToString(photoByte, Base64.NO_WRAP));

                } else {
                    mru.set_Meter_Photo_Byte("");
                }*/
            // mru.set_IsMeterFIxedStatus(IsMeterFixedStatus);
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            /*create instance of File with name img.jpg*/
            File file1 = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
            /*put uri as extra in intent object*/
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file1));
            /*start activity for result pass intent as argument and request code */
            startActivityForResult(intent, 1);
            //  new requestForBill().execute(mru);
        }
    }

    //  }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if request code is same we pass as argument in startActivityForResult
        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBlueToothState(true);
        } else if (requestCode == REQUEST_ENABLE_BT1) {
            CheckBlueToothState(false);
        }
        if (requestCode == 1) {
            if (resultCode == RESULT_CANCELED) {
            } else {
                //create instance of File with same name we created before to get image from storage
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
                //Crop the captured image using an other intent
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    // downsizing image as it throws OutOfMemory Exception for larger
                    // images
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(),
                            options);
                    /*the user's device may not support cropping*/
                    /*   Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(file));*/
                    SaveImage(bitmap);
                    // cropCapturedImage(Uri.fromFile(file));
                } catch (NullPointerException aNFE) {
                    //display an error message if user device doesn't support
                    String errorMessage = "Sorry - your device doesn't support camera action!";
                    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_CANCELED) {

            } else if (resultCode == RESULT_OK) {
                //Create an instance of bundle and get the returned data
                Bitmap bitmap = null;
                try {
                    Bundle extras = data.getExtras();
                    //get the cropped bitmap from extras

                    Bitmap thePic = extras.getParcelable("data");
                    //set image bitmap to image view
//			imVCature_pic.setImageBitmap(thePic);

                    SaveImage(thePic);

                } catch (Exception ex) {
                    try {
                        Utiilties.writeIntoLog(Log.getStackTraceString(ex));
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        SaveImage(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void cropCapturedImage(Uri picUri) {
        //call the standard crop action intent
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri of image
        cropIntent.setDataAndType(picUri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1.5);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 150);
        cropIntent.putExtra("outputY", 100);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        //start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, 2);
    }

    private void SaveImage(Bitmap finalBitmap) {
        String success = "";
		/*String id=CommonPref.getUserDetails(getApplicationContext())
				.get_MRUNo();*/
        CROP_IMAGE_FILE_PATH = "/BEB/CropImage/PmaImages/" + CommonPref.getUserDetails(MeterReadingStatusActivityForApl.this).get_MRUNo() + "/";
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + CROP_IMAGE_FILE_PATH);
        myDir.mkdirs();
     /*   Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);*/
        fname = IMAGE_NAME;
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            success = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        if (success.matches("OK")) {
            // create class object
            // gps = new GPSTracker(MeterReadingStatusActivityForApl.this);
  /*          double latitude = 0.0;
            double longitude = 0.0;
            long dateTime = 0;

            // check if GPS enabled
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                dateTime = gps.getTime();
                String date = Utiilties.getDate(dateTime, "dd/MM/yyyy");
                if (latitude > 0.0 && longitude > 0.0) {*/

            long c = localDBHelper.savePhotoAPL("", "", CROP_IMAGE_FILE_PATH + fname, mru1.get_CON_ID(), "");
            if (c > 0) {
                mruList = localDBHelper.getMRU2("ACT_NO", stringAcNo);
                for (MRUDetails mru : mruList) {
                    File file2 = new File(Environment.getExternalStorageDirectory()
                            .getPath() + "/BEB/CropImage/" + CommonPref.getUserDetails(getApplicationContext()).get_MRUNo() + "/" + mru.get_CON_ID() + ".jpg");
                    File file3 = new File(Environment.getExternalStorageDirectory()
                            .getPath() + "/BEB/CropImage/PmaImages/" + CommonPref.getUserDetails(getApplicationContext()).get_MRUNo() + "/" + mru.get_CON_ID() + ".jpg");
                    if (file2.exists()) {
                        byte[] photoByte = convertFileToByteArray(file2);
                        byte[] photoByte1 = convertFileToByteArray(file3);
                        mru.set_Meter_Photo_Byte(Base64.encodeToString(photoByte, Base64.NO_WRAP));
                        mru.set_Pma_Photo_Byte(Base64.encodeToString(photoByte1, Base64.NO_WRAP));
                    } else {
                        mru.set_Meter_Photo_Byte("");
                        mru.set_Pma_Photo_Byte("");
                    }
                    mru.set_IsMeterFIxedStatus(IsMeterFixedStatus);
                    new requestForBill().execute(mru);
                }
                      /*  Intent intent = new Intent(getBaseContext(),
                                MeterReadingStatusActivityForApl.class);
                        intent.putExtra("ACCOUNT_NO", mru1.get_CON_ID());
                        intent.putExtra("FLAG", "0");
                        startActivity(intent);
                        finish();*/
            } else {
                Toast.makeText(getApplicationContext(),
                        "Error in Local Database", Toast.LENGTH_LONG)
                        .show();

            }
               /* } else {
                    long c = localDBHelper.savePhoto("9999", "9999", CROP_IMAGE_FILE_PATH
                            + fname, tvAcNo.getText().toString()
                            .trim(), date);
                    if (c > 0) {
                        Intent intent = new Intent(getBaseContext(),
                                MeterReadingStatusActivityForApl.class);
                        intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
                                .toString().trim());
                        intent.putExtra("FLAG", "0");
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Error in Local Database", Toast.LENGTH_LONG)
                                .show();

                    }*/
					/*	latitude=9.9;
						longitude=9.9;

						Toast.makeText(getApplicationContext(),
								"Please Wait a while until GPS is stable", Toast.LENGTH_LONG)
								.show();*/
        } else {
            Toast.makeText(getApplicationContext(), success, Toast.LENGTH_LONG)
                    .show();
        }
    }


    private class requestForBill extends AsyncTask<MRUDetails, Void, BillDetails> {

        public requestForBill() {

        }

        private final ProgressDialog dialog = new ProgressDialog(
                MeterReadingStatusActivityForApl.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(
                MeterReadingStatusActivityForApl.this).create();

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
            UserDetails user = CommonPref.getUserDetails(getApplicationContext());
            BillDetails res1 = WebServiceHelper.BillRequestAPl(param[0], user);

            return res1;
        }

        @Override
        protected void onPostExecute(BillDetails result) {

            if (this.dialog.isShowing()) {
                this.dialog.dismiss();

                if (result == null) {
                    alertDialog.setTitle("Failed!!");
                    alertDialog.setMessage("Error" + result);
                    alertDialog.show();
                    updateButtonState(false);
                    if (!runPrintReceiptSequence(false)) {
                        updateButtonState(true);
                    }
                } else {
                    if ((result.get_RESPONSE_MESSAGE().toString().trim().equals("Success")) || (result.get_RESPONSE_MESSAGE().toString().trim().equals("Duplicate Bill"))) {
                        try {
                            SQLiteDatabase db = localDBHelper.getReadableDatabase();
                            long c = localDBHelper.insertBillDetails(result);
                            if (c > 0) {
                                localDBHelper.saveBillStatus("G", stringAcNo);
                                bill = result;
                                if (result.get_AMBI_Flag().toString().trim().equals("R")) {
                                    Toast.makeText(MeterReadingStatusActivityForApl.this, "Bill Generate and Save Locally",
                                            Toast.LENGTH_SHORT).show();
                                    if (CommonPref.getPrinterType(getApplicationContext()).equals("E")) {
                                        if (!runPrintReceiptSequence(true)) {
                                            updateButtonState(false);
                                        }
                                    }  else if (CommonPref.getPrinterType(getApplicationContext()).equals("A")) {
                                        {
                                            updateButtonState(false);
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

                                                Intent intent = new Intent(MeterReadingStatusActivityForApl.this, AnalogicsPrinterSetup.class);
                                                startActivity(intent);
                                                // finish();

                                            }

                                        }
                                    }
                                } else {
                                    Toast.makeText(MeterReadingStatusActivityForApl.this, "Bill OutSort", Toast.LENGTH_SHORT).show();
                                    updateButtonState(false);
                                    if (CommonPref.getPrinterType(getApplicationContext()).equals("E")) {

                                        if (!runPrintReceiptSequence(false)) {
                                            updateButtonState(true);
                                        }
                                    } else if (CommonPref.getPrinterType(getApplicationContext()).equals("A")) {
                                        {
                                            updateButtonState(true);
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

                                                Intent intent = new Intent(MeterReadingStatusActivityForApl.this,
                                                        AnalogicsPrinterSetup.class);
                                                startActivity(intent);
                                                // finish();

                                            }

                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(MeterReadingStatusActivityForApl.this, "Sqlite Error during inserting Data",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception ex) {
                            Toast.makeText(MeterReadingStatusActivityForApl.this, "Sqlite Error" + ex.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Utiilties.writeIntoLog(Log.getStackTraceString(ex));
                        }

                    } else {
                        alertDialog.setTitle("Failed!!");
                        alertDialog.setMessage(result.get_RESPONSE_MESSAGE().toString().trim());
                        alertDialog.show();

                    }
                }
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







    private boolean runPrintReceiptSequence(Boolean isOnline) {
        if (!initializeObject()) {
            return false;
        }
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
        if (!printData()) {
            finalizeObject();
            return false;
        }
        return true;
    }

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(Printer.TM_P20, Printer.MODEL_SOUTHASIA,
                    mContext);
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
                ShowMsg.showResult(code, makeErrorMessage(status), mContext);
                if (makeErrorMessage(status).isEmpty() && code == 0 && bill != null) {
                    long c = localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
                    if (c > 0) {
                        Toast.makeText(getApplicationContext(),"Update in Local DataBase",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    } else {
                        Toast.makeText(getApplicationContext(),"Error in Local Database",Toast.LENGTH_LONG).show();
                    }
                } else if (makeErrorMessage(status).isEmpty() && code == 0 && bill == null) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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


    private void updateButtonState(boolean state) {

        btnSubmit.setEnabled(state);
    }

    private boolean createReceiptData() {
        String method = "";
        // Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
        // Get our saved file into a bitmap object:
        File file = new File(Environment.getExternalStorageDirectory().getPath()
                + "/BEB/CropImage/" + CommonPref.getUserDetails(getApplicationContext()).get_MRUNo() + "/" + bill.get_CON_ID() + ".jpg");
        Bitmap logoData = null;
        if (file.exists()) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            logoData = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            int x = logoData.getWidth();
            if (x > 220) {
                logoData = Bitmap.createScaledBitmap(logoData, 350, 220, true);
            } else {
                logoData = Bitmap.createScaledBitmap(logoData, 350, 220, true);
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
            String header;
            if (bill.get_IsAlredyPrint() != null) {
                if (bill.get_IsAlredyPrint().toString().trim().equals("Y")) {
                    header = "Duplicate Bill";
                } else {
                    header = "ENERGY BILL";
                }
            } else {
                header = "ENERGY BILL";
            }
            mPrinter.addText(header + "\n");
            mPrinter.addText(bill.get_COMPANY().trim() + "\n");

            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextSize";
            mPrinter.addTextSize(2, 1);
            method = "addText";
            //mPrinter.addText("OFFER" + "\n");
            //mPrinter.addText("#*#*#*#" + "\n");
            String strRebateOnline = bill.get_ONLINE_REBATE();
            mPrinter.addText("Get Extra Rs" + strRebateOnline + "\n" + " rebate if Rs" + bill.get_PROMPT_AMT().trim() + "\n" + " paid online" + "\n" + "upto " + bill.get_UPTO_DATE().trim() + "\n");


            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            //textData.append("Get Extra Rs." +strRebateOnline+ " rebate if Rs."+ bill.get_PROMPT_AMT().trim() +" paid"+"\n"+"online upto " + bill.get_UPTO_DATE().trim()  + "\n" );
            textData.append("***********************************\n");
            textData.append("ELECTRICITY BILL  : " + monthYear(bill.get_BMONTH().trim(), bill.get_BYEAR().trim()) + "\n");
            textData.append("******************************\n");
            textData.append("DATE: " + bill.get_BILL_DT().trim() + "     TIME: " + bill.get_BILL_TIME().trim() + "\n");
            textData.append("CONSUMER DETAILS\n");
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("BILL NO             :    " + bill.get_BILL_NO().trim() + "\n");
            textData.append("DIVISION            :    " + bill.get_DIV_NAME().trim() + "\n");
            textData.append("SUBDIVISION         :    " + bill.get_SUB_DIV_NAME().trim() + "\n");
            textData.append("CON ID              :    " + bill.get_CON_ID().trim() + "\n");
            textData.append("A/C NUMBER          :    " + bill.get_ACT_NO().trim() + "\n");
            textData.append("MRU                 :    " + CommonPref.getUserDetails(getApplicationContext()).get_MRUNo() + "\n");
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
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText("READING DETAILS\n");
            mPrinter.addText("---------------------------\n");


            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("              PREVIOUS    CURRENT  \n");
            textData.append("READING :         " + bill.get_PREV_READ().trim() + "         " + bill.get_CUR_READ().trim() + "\n");
            textData.append("DATE    :    " + bill.get_PRE_READ_DATE().trim() + "  " + bill.get_BILL_DT().trim() + "\n");
            textData.append("STATUS  :       " + bill.get_PRE_READ_STAT().trim() + "           " + bill.get_READ_STAT().trim() + "   \n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            if (file.exists()) {
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
            textData.append("MF :  " + bill.get_MULTI_FACT().trim() + "             CONSUMPTION : " + bill.get_UNITS_CONS() + "\n");
            textData.append("RECORDED DEMAND : " + bill.get_REC_DEMAND().trim() + "      PF  : " + bill.get_POW_FACT().trim() + "\n");
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
            mPrinter.addText("CURRENT BILL DETAILS\n");
            mPrinter.addText("---------------------------\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("ENERGY CHARGES          :" + addBlankToString(bill.get_EC_CURR().trim()) + "\n");
            textData.append("DPS                     :" + addBlankToString(bill.get_DPS_CURR().trim()) + "\n");
            textData.append("FIXED/DEMD CHG          :" + addBlankToString(bill.get_FIX_CURR().trim()) + "\n");
            textData.append("EXCESS DEMD CHG         :" + addBlankToString(bill.get_EXC_DEM_CURR().trim()) + "\n");
            textData.append("ELECTRICITY DUTY        :" + addBlankToString(bill.get_ED_CURRENT().trim()) + "\n");
            textData.append("METER RENT              :" + addBlankToString(bill.get_MET_RENT().trim()) + "\n");
            textData.append("SHUNT CAP. CHG          :" + addBlankToString(bill.get_SHUN_CURR().trim()) + "\n");
            textData.append("OTHERS CHG              :" + addBlankToString(bill.get_OTH_CURR().trim()) + "\n");
            textData.append("---------------------------\n");
            textData.append("STATE GOV SUBSIDY       :" + addBlankToString(bill.get_GOV_SUBSIDY().trim()) + "\n");
            //float totalB = Float.parseFloat(convertBlankToZero(bill.get_EC_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_DPS_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_FIX_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_EXC_DEM_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_ED_CURRENT())) + Float.parseFloat(convertBlankToZero(bill.get_MET_RENT())) + Float.parseFloat(convertBlankToZero(bill.get_SHUN_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_OTH_CURR()));
            textData.append("---------------------------\n");
            textData.append("SUB TOTAL (B)           :" + addBlankToString(bill.get_SubTotalB().trim()) + "\n");
            textData.append("***********************************\n");
            textData.append("INTEREST ON SD(C)       :" + addBlankToString(bill.get_INTR_SEC_DEP().trim()) + "\n");
            textData.append("INCENTIVE               :" + addBlankToString(bill.get_INCENTIVE().trim()) + "\n");
            textData.append("REBATES ON MMC          :" + addBlankToString(bill.get_REBATE_ON_MMC().trim()) + "\n");
            //float total = Float.parseFloat(convertBlankToZero(bill.get_INTR_SEC_DEP())) + Float.parseFloat(convertBlankToZero(bill.get_INCENTIVE())) + Float.parseFloat(convertBlankToZero(bill.get_REBATE_ON_MMC())) + totalA + totalB ;
            textData.append("TOTAL (A+B+C)           :" + addBlankToString(bill.get_SubTotalC().trim()) + "\n");
            textData.append("***********************************\n");
            textData.append("REBATES                 :" + addBlankToString(bill.get_REBATE().trim()) + "\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText("AMOUNT PAYABLE\n");
            mPrinter.addText("---------------------------\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("UPTO        " + bill.get_UPTO_DATE().trim() + "  :" + addBlankToString(bill.get_PROMPT_AMT().trim()) + "\n");
            textData.append("BY          " + bill.get_BY_DATE().trim() + "  :" + addBlankToString(bill.get_GROSS_AMT().trim()) + "\n");
            textData.append("AFTER       " + bill.get_AFTER_DATE().trim() + "  :" + addBlankToString(bill.get_NET_AMT().trim()) + "\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText("---------------------------\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            if (!bill.get_KEPT_AMOUNT().equalsIgnoreCase("NA")) {
                textData.append("Installment amount( C ):" + bill.get_KEPT_INST_FLAG() + " " + bill.get_KEPT_INST_AMT().trim() + "\n");
                textData.append("Amount Payable ( B+C ) : " + bill.get_KEPT_PAY_AMT() + "\n");
            }
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText("---------------------------\n");

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText("***********************************\n");
            mPrinter.addText("DETAILS OF LAST PAYMENT \n");
            mPrinter.addText("***********************************\n");

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("LAST PAID AMOUNT        :    " + bill.get_LAST_PAID_AMT().trim() + "\n");
            textData.append("LAST PAID DATE          :    " + bill.get_LAST_PAY_DATE().trim() + "\n");
            textData.append("RECEIPT NUMBER          :    " + bill.get_LAST_RCPT_NO().trim() + "\n");
            textData.append("METER READER            :   " + CommonPref.getUserDetails(getApplicationContext()).get_UserID().trim() + "\n");
            textData.append("TOLLFREE HELPLINE NO.   :    " + "1912" + "\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);


        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return false;
        }

        textData = null;

        return true;
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
                Utiilties.writeIntoLog(Log.getStackTraceString(ex));
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, "sendData", mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                Utiilties.writeIntoLog(Log.getStackTraceString(e));
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
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return false;
        }
        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        } catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }

        if (!isBeginTransaction ) {
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
                website = "www.nbpdcl.in";
            } else if (header.equals("2")) {
                header = "SBPDCL";
                website = "www.sbpdcl.in";
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
            textData.append("DIVISION      :       " + CommonPref.getUserDetails(getApplicationContext()).get_DivName().trim() + "\n");
            textData.append("SUBDIVISION   :       " + CommonPref.getUserDetails(getApplicationContext()).get_SubdivName().trim() + "\n");
            textData.append("CON ID        :       " + mru1.get_CON_ID().trim() + "\n");
            textData.append("MRU           :       " + CommonPref.getUserDetails(getApplicationContext()).get_MRUNo() + "\n");
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

            if (bill != null && (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("4X") || bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("NE") || bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("LK"))) {
                String msg = "";

                if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("4X")) {
                    msg = "The Bill could not be Printed due to Very High Consumption Recorded. ";
                } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("NE")) {
                    msg = "The Bill could not be Printed due to Negative Bill. ";
                } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("LK")) {
                    //msg = " Reading After More Than 3 Months.  ";
                }

                //  textData.append(/*//*"The Bill could not be Printed due to " +*//**//* msg + "\nKindly visit our website at " + website + " \nOr Concerned Sub-division / division office. \n");
                textData.append("ERROR CODE    :        " + bill.get_AMBI_Flag().toString().trim() + "\n");

            } else {
                textData.append("The Bill could not be generated now, \nPlease collect your Bill from " + header + " \nWebsite after 2 Days. \n");
                //textData.append("CODE          :        7 \n");
                textData.append("Message      :        NETWORK ISSUE \n");
            }


            textData.append("METER READER  :       " + CommonPref.getUserDetails(getApplicationContext()).get_UserID().trim() + "\n");
            textData.append("TOLLLFREE HELPLINE NO.:" + "1912" + "\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);

        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return false;
        }

        textData = null;

        return true;
    }
   /* private boolean createReceiptDataOffline() {
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
                website = "www.nbpdcl.in";
            } else if (header.equals("2")) {
                header = "SBPDCL";
                website = "www.sbpdcl.in";
            } else {
                header = "";
            }
            mPrinter.addText(header + "\n");
            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            mPrinter.addText("******************************\n");


            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("खाता नंबर    :       " + mru1.get_ACT_NO().trim() + "\n");
            textData.append("प्रभाग        :       " + CommonPref.getUserDetails(getApplicationContext()).get_DivName().trim() + "\n");
            textData.append("प्रतिभाग      :       " + CommonPref.getUserDetails(getApplicationContext()).get_SubdivName().trim() + "\n");
            textData.append("उपभोक्ता पहचान :     " + mru1.get_CON_ID().trim() + "\n");
            textData.append("किताब        :      " + CommonPref.getUserDetails(getApplicationContext()).get_MRUNo() + "\n");
            textData.append("उपभोक्ता नाम   :      " + mru1.get_CNAME().trim() + "\n");
            String meterStatus = "";
            String meterReading = "";
            if (stringReadingStatus.equalsIgnoreCase("OK")) {
                meterStatus = "Actual";
                meterReading = etMeterReading.getText().toString().trim();
            } else {
                meterStatus = "Average";
                meterReading = "NA";
            }
            textData.append("वर्तमान मीटर रीडिंग का दर्जा  :       " + meterStatus + " ( " + stringReadingStatus + " ) " + "\n");
            if (mru1.get_Read_Date() == null) {
                textData.append("वर्तमान पढ़ने की तारीख :       " + "NA" + "\n");
            } else {
                textData.append("वर्तमान पढ़ने की तारीख :       " + mru1.get_Read_Date().toString().trim() + "\n");
            }

            textData.append("वर्तमान पढ़ने (kWh)      :       " + meterReading + "\n");
            textData.append("पिछला पढ़ने (kWh)       :      " + mru1.get_PREVIOUS_READ().trim() + "\n");

            if (bill != null && (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("4X") || bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("NE") || bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("LK"))) {
                String msg = "";

                if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("4X")) {
                    msg = "बिल बहुत अधिक खपत दर्ज की वजह से मुद्रित नहीं किया जा सकता है। ";
                } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("NE")) {
                    msg = "विधेयक नकारात्मक विधेयक के कारण मुद्रित नहीं किया जा सकता है।  ";
                } else if (bill.get_AMBI_Flag().toString().trim().equalsIgnoreCase("LK")) {
                    //msg = " Reading After More Than 3 Months.  ";
                }

                //  textData.append(*//**//*"The Bill could not be Printed due to " +*//**//* msg + "\nKindly visit our website at " + website + " \nOr Concerned Sub-division / division office. \n");
                textData.append("त्रुटि संकेत    :        " + bill.get_AMBI_Flag().toString().trim() + "\n");

            } else {
                textData.append("विधेयक अब उत्पन्न नहीं किया जा सकता है,\n" + "कृपया " + header + " से अपने बिल इकट्ठा\n" + website + "2 दिनों के बाद ।");
                //textData.append("CODE          :        7 \n");
                textData.append("संदेश      :       नेटवर्क समस्या \n");
            }


            textData.append("मीटर वाचक    :       " + CommonPref.getUserDetails(getApplicationContext()).get_UserID().trim() + "\n");
            textData.append("हेल्पलाइन नंबर  :       " + "1912" + "\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);


        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            return false;
        }
        textData = null;
        return true;
    }*/

    private boolean createReceiptDatahindi() {
        String method = "";
        // Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
        // Get our saved file into a bitmap object:
        File file = new File(Environment.getExternalStorageDirectory().getPath()
                + "/BEB/CropImage/" + CommonPref.getUserDetails(getApplicationContext()).get_MRUNo() + "/" + bill.get_CON_ID() + ".jpg");


        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap logoData = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        int x = logoData.getWidth();
        if (x > 220) {
            logoData = Bitmap.createScaledBitmap(logoData, 350, 220, true);
        } else {
            logoData = Bitmap.createScaledBitmap(logoData, 350, 220, true);
        }
        StringBuilder textData = new StringBuilder();
        final int barcodeWidth = 2;
        final int barcodeHeight = 100;

        if (mPrinter == null) {
            return false;
        }

        try {
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            method = "addTextSize";
            mPrinter.addTextSize(2, 2);
            method = "addText";
            String header;
            if (bill.get_RESPONSE_MESSAGE().toString().trim().equals("Duplicate Bill")) {
                header = "प्रतिलिपि ऊर्जा विपत्र";
            } else {
                header = "ऊर्जा विपत्र";
            }
            mPrinter.addText(header + "\n");
            mPrinter.addText(bill.get_COMPANY().trim() + "\n");

            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            textData.append("***********************************\n");

            textData.append("ऊर्जा विपत्र  : " + monthYear(bill.get_BMONTH().trim(), bill.get_BYEAR().trim()) + "\n");
            textData.append("******************************\n");
            textData.append("तारीख: " + bill.get_BILL_DT().trim() + "     समय: " + bill.get_BILL_TIME().trim() + "\n");
            textData.append("उपभोक्ता विवरण\n");
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);

            textData.append("विपत्र संख्या     :    " + bill.get_BILL_NO().trim() + "\n");
            textData.append("प्रमंडल         :    " + bill.get_DIV_NAME().trim() + "\n");
            textData.append("अवर प्रमंडल         :    " + bill.get_SUB_DIV_NAME().trim() + "\n");
            textData.append("उपभोक्ता  संख्या :    " + bill.get_CON_ID().trim() + "\n");
            textData.append("खाता नंबर       :    " + bill.get_ACT_NO().trim() + "\n");
            textData.append("खाता          :    " + CommonPref.getUserDetails(getApplicationContext()).get_MRUNo() + "\n");
            textData.append("उपभोक्ता नाम         :    " + bill.get_CNAME().trim() + "\n");
            textData.append("पता            :    ");
            textData.append(bill.get_BILL_ADDRESS().trim() + "\n");
            textData.append("क्षेत्र       :  " + bill.get_AREA_TYPE().trim() + "\n");
            textData.append("पोल सं          :  " + bill.get_POLE_NO().trim() + "\n");
            textData.append("मीटर संख्या      :  " + bill.get_METER_NO().trim() + "       PH : " + bill.get_PHASE().trim() + "\n");
            textData.append("मीटर स्वामित्व     :  " + bill.get_MET_OWNER().trim() + "\n");
            textData.append("उपभोक्ता श्रेणी       :  " + bill.get_CATEGORY().trim() + "\n");
            if (bill.get_CATEGORY().contains("D")) {
                textData.append("संविदा मांग :  " + bill.get_CON_DEM().trim() + "\n");

            } else {
                textData.append("स्वीकृत भार : " + bill.get_SANC_LOAD().trim() + "\n");

            }
            textData.append("जमानत राशि            : " + bill.get_SEC_DEP().trim() + "\n");
            textData.append("विपत्र माह       :  " + bill.get_BILLED_MONTH().trim() + "\n");
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText("मानपठन \n");
            mPrinter.addText("---------------------------\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("              पिछला    \n" + "वर्तमान  \n");
            textData.append("पठन   :     " + bill.get_PREV_READ().trim() + "         " + bill.get_CUR_READ().trim() + "\n");
            textData.append("तारीख :      " + bill.get_PRE_READ_DATE().trim() + "  " + bill.get_BILL_DT().trim() + "\n");
            textData.append("स्थिति :      " + bill.get_PRE_READ_STAT().trim() + "           " + bill.get_READ_STAT().trim() + "   \n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addImage";
            mPrinter.addImage(logoData, 0, 0, logoData.getWidth(), logoData.getHeight(),
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_AUTO);
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("\n");
            //float consumption = (Float.parseFloat(bill.get_CUR_READ().trim()) - Float.parseFloat(bill.get_PREV_READ().trim())) *Float.parseFloat(bill.get_MULTI_FACT().trim());
            textData.append("गुणांक :  " + bill.get_MULTI_FACT().trim() + "             खपत : " + bill.get_UNITS_CONS() + "\n");
            textData.append("\n" + "दर्ज मांग : " + bill.get_REC_DEMAND().trim() + "      पावर फैक्टर    : " + bill.get_POW_FACT().trim() + "\n");
            textData.append("एमएमसी      : " + bill.get_MMC_UNIT().trim() + "         औसत  : " + bill.get_AVG_UNIT().trim() + "\n");
            textData.append("घोषित यूनिट        : " + bill.get_BILLED_UNIT().trim() + "विपत्र प्रकार: " + bill.get_BILL_TYPE().trim() + "\n");
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText("बक़ाया विवरण\n");
            mPrinter.addText("---------------------------\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("ऊर्जा  बक़ाया            :" + addBlankToString(bill.get_EC_ARREAR().trim()) + "\n");
            textData.append("विळम्ब अधिभार बकाया            :" + addBlankToString(bill.get_DPS_ARREAR().trim()) + "\n");
            textData.append("अन्य बकाया                  :" + addBlankToString(bill.get_OTH_ARREAR().trim()) + "\n");
            //float totalA = Float.parseFloat(convertBlankToZero(bill.get_EC_ARREAR())) + Float.parseFloat(convertBlankToZero(bill.get_DPS_ARREAR())) + Float.parseFloat(convertBlankToZero(bill.get_OTH_ARREAR()));
            textData.append(" कुल बकाया (अ)           :" + addBlankToString(bill.get_SubTotalA().trim()) + "\n");
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText("वर्तमान विपत्र विवरण\n");
            mPrinter.addText("---------------------------\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("ऊर्जा शुल्क          :" + addBlankToString(bill.get_EC_CURR().trim()) + "\n");
            textData.append("विळम्ब अधिभार            :" + addBlankToString(bill.get_DPS_CURR().trim()) + "\n");
            textData.append("नियत / मांग शुल्क   :" + addBlankToString(bill.get_FIX_CURR().trim()) + "\n");
            textData.append("आधिक्य  मांग शुल्क:" + addBlankToString(bill.get_EXC_DEM_CURR().trim()) + "\n");
            textData.append("विद्युत कर         :" + addBlankToString(bill.get_ED_CURRENT().trim()) + "\n");
            textData.append("मीटर किराया       :" + addBlankToString(bill.get_MET_RENT().trim()) + "\n");
            textData.append("कैपेसिटर   शुल्क       :" + addBlankToString(bill.get_SHUN_CURR().trim()) + "\n");
            textData.append("अन्य  शुल्क         :" + addBlankToString(bill.get_OTH_CURR().trim()) + "\n");
            //float totalB = Float.parseFloat(convertBlankToZero(bill.get_EC_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_DPS_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_FIX_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_EXC_DEM_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_ED_CURRENT())) + Float.parseFloat(convertBlankToZero(bill.get_MET_RENT())) + Float.parseFloat(convertBlankToZero(bill.get_SHUN_CURR())) + Float.parseFloat(convertBlankToZero(bill.get_OTH_CURR()));
            textData.append("उप कुल (B)    :" + addBlankToString(bill.get_SubTotalB().trim()) + "\n");
            textData.append("***********************************\n");
            textData.append("ब्याज  SD(C) पर   :" + addBlankToString(bill.get_INTR_SEC_DEP().trim()) + "\n");
            textData.append("प्रोत्साहन          :" + addBlankToString(bill.get_INCENTIVE().trim()) + "\n");
            textData.append("छूट MMC पर        :" + addBlankToString(bill.get_REBATE_ON_MMC().trim()) + "\n");
            //float total = Float.parseFloat(convertBlankToZero(bill.get_INTR_SEC_DEP())) + Float.parseFloat(convertBlankToZero(bill.get_INCENTIVE())) + Float.parseFloat(convertBlankToZero(bill.get_REBATE_ON_MMC())) + totalA + totalB ;
            textData.append("TOTAL (A+B+C)     :" + addBlankToString(bill.get_SubTotalC().trim()) + "\n");
            textData.append("***********************************\n");
            textData.append("छूट           :" + addBlankToString(bill.get_REBATE().trim()) + "\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText("देय राशि\n");
            mPrinter.addText("---------------------------\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("तक        " + bill.get_UPTO_DATE().trim() + "  :" + addBlankToString(bill.get_PROMPT_AMT().trim()) + "\n");
            textData.append("द्वारा        " + bill.get_BY_DATE().trim() + "    :" + addBlankToString(bill.get_GROSS_AMT().trim()) + "\n");
            textData.append("बाद        " + bill.get_AFTER_DATE().trim() + "  :" + addBlankToString(bill.get_NET_AMT().trim()) + "\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText("***********************************\n");
            mPrinter.addText("अंतिम भुगतान का विवरण \n");
            mPrinter.addText("***********************************\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("अंतिम भुगतान की गई राशि    :  " + bill.get_LAST_PAID_AMT().trim() + "\n");
            textData.append("अंतिम भुगतान तिथि          :  " + bill.get_LAST_PAY_DATE().trim() + "\n");
            textData.append("रसीद संख्या               :  " + bill.get_LAST_RCPT_NO().trim() + "\n");
            textData.append("मीटर वाचक               :  " + CommonPref.getUserDetails(getApplicationContext()).get_UserID().trim() + "\n");
            textData.append("टोलफ्री हेल्पलाइन नं।         :  " + "1912" + "\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);


        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return false;
        }

        textData = null;

        return true;
    }

    private boolean createReceiptDataForanalogics() {
        String method = "";
        // Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
        // Get our saved file into a bitmap object:
        File file = new File(Environment.getExternalStorageDirectory().getPath()
                + "/BEB/CropImage/" + CommonPref.getUserDetails(getApplicationContext()).get_MRUNo() + "/" + bill.get_CON_ID() + ".jpg");
        Bitmap logoData = null;
        if (file.exists()) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            logoData = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            int x = logoData.getWidth();
            if (x > 220) {
                logoData = Bitmap.createScaledBitmap(logoData, 350, 220, true);
            } else {
                logoData = Bitmap.createScaledBitmap(logoData, 350, 220, true);
            }
        }
        StringBuilder textData = new StringBuilder();
        Bluetooth_Printer_2inch_prof_ThermalAPI printer = new Bluetooth_Printer_2inch_prof_ThermalAPI();
        if (printer == null) {
            return false;
        }
        try {
            String header;
            if (bill.get_IsAlredyPrint() != null) {
                if (bill.get_IsAlredyPrint().toString().trim().equals("Y")) {
                    header = "          Duplicate Bill";
                } else {
                    header = "          ENERGY BILL";
                }
            } else {
                header = "          ENERGY BILL";
            }
            textData.append(header + "\n");
            textData.append("             " + bill.get_COMPANY().trim() + "\n");

            textData.append("********************************\n");
            String strRebateOnline = bill.get_ONLINE_REBATE();
            textData.append("Get Extra Rs " + strRebateOnline + " rebate if" + "\n" + "Rs " + bill.get_PROMPT_AMT().trim() + " paid online upto " + "\n" + bill.get_UPTO_DATE().trim() + "\n");

            textData.append("********************************\n");

            textData.append("ELECTRICITY BILL  : " + monthYear(bill.get_BMONTH().trim(), bill.get_BYEAR().trim()) + "\n");
            textData.append("*******************************\n");
            textData.append("DATE: " + bill.get_BILL_DT().trim() + " TIME: " + bill.get_BILL_TIME().trim() + "\n");
            textData.append("        CONSUMER DETAILS\n");
            textData.append("********************************\n");

            textData.append("BILL NO       : " + bill.get_BILL_NO().trim() + "\n");
            textData.append("DIVISION      : " + bill.get_DIV_NAME().trim() + "\n");
            textData.append("SUBDIVISION   : " + bill.get_SUB_DIV_NAME().trim() + "\n");
            textData.append("CON ID        : " + bill.get_CON_ID().trim() + "\n");
            textData.append("A/C NUMBER    : " + bill.get_ACT_NO().trim() + "\n");
            textData.append("MRU           : " + CommonPref.getUserDetails(getApplicationContext()).get_MRUNo() + "\n");
            textData.append("NAME          : " + bill.get_CNAME().trim() + "\n");
            textData.append("ADDRESS       : ");
            textData.append(bill.get_BILL_ADDRESS().trim() + "\n");
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
            if (file.exists()) {
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

                /*byte c[] = convertBitmapToByteArray(getApplicationContext(),logoData);
               //c=convertBitmapToByteArray(getApplicationContext(),logoData);
                String hexstring=toHexString(c);
                byte[] imageData = printer.prepare2InchImageData_VIP(address,
                        hexstring, 0);
                conn.printData(imageData);*/
            }


            textData.append("\n");
            textData.append("OLD CONSUMPTION  :  " + bill.get_OLD_CONSUMPTION().trim() + "\n");

            textData.append("MF : " + bill.get_MULTI_FACT().trim() + "     CONSUMPTION : " + bill.get_UNITS_CONS() + "\n");
            textData.append("RECORDED DEMAND  : " + bill.get_REC_DEMAND().trim() + " PF:" + bill.get_POW_FACT().trim() + "\n");
            textData.append("MMC UNITS        : " + bill.get_MMC_UNIT().trim() + "  AVG: " + bill.get_AVG_UNIT().trim() + "\n");
            textData.append("BLD UNITS        : " + bill.get_BILLED_UNIT().trim() + "  TYPE: " + bill.get_BILL_TYPE().trim() + "\n");
            textData.append("********************************\n");
            if (!bill.get_KEPT_AMOUNT().equalsIgnoreCase("NA")) {
                textData.append("KEPT AMOUNT   : " + "Rs." + bill.get_KEPT_AMOUNT().trim() + "\n");
            }
            textData.append("********************************\n");
            textData.append("        ARREAR DETAILS\n");
            textData.append("-------------------------------\n");
            textData.append("ENERGY DUES      :" + addBlankToString1(bill.get_EC_ARREAR().trim()) + "\n");
            textData.append("ARREAR DPS       :" + addBlankToString1(bill.get_DPS_ARREAR().trim()) + "\n");
            textData.append("ARREAR ED        :" + addBlankToString1(bill.get_EDAREAR().trim()) + "\n");
            textData.append("OTHERS           :" + addBlankToString1(bill.get_OTH_ARREAR().trim()) + "\n");
            textData.append("SUB TOTAL (A)    :" + addBlankToString1(bill.get_SubTotalA().trim()) + "\n");
            textData.append("********************************\n");
            textData.append("      CURRENT BILL DETAILS\n");
            textData.append("------------------------------\n");
            textData.append("ENERGY CHARGES   :" + addBlankToString1(bill.get_EC_CURR().trim()) + "\n");
            textData.append("DPS              :" + addBlankToString1(bill.get_DPS_CURR().trim()) + "\n");
            textData.append("FIXED/DEMD CHG   :" + addBlankToString1(bill.get_FIX_CURR().trim()) + "\n");
            textData.append("EXCESS DEMD CHG  :" + addBlankToString1(bill.get_EXC_DEM_CURR().trim()) + "\n");
            textData.append("ELECTRICITY DUTY :" + addBlankToString1(bill.get_ED_CURRENT().trim()) + "\n");
            textData.append("METER RENT       :" + addBlankToString1(bill.get_MET_RENT().trim()) + "\n");
            textData.append("SHUNT CAP. CHG   :" + addBlankToString1(bill.get_SHUN_CURR().trim()) + "\n");
            textData.append("OTHERS CHG       :" + addBlankToString1(bill.get_OTH_CURR().trim()) + "\n");
            textData.append("------------------------------\n");
            textData.append("STATE GOV SUBSIDY:" + addBlankToString1(bill.get_GOV_SUBSIDY().trim()) + "\n");
            textData.append("------------------------------\n");
            textData.append("SUB TOTAL (B)    :" + addBlankToString1(bill.get_SubTotalB().trim()) + "\n");
            textData.append("********************************\n");
            textData.append("INTEREST ON SD(C):" + addBlankToString1(bill.get_INTR_SEC_DEP().trim()) + "\n");
            textData.append("INCENTIVE        :" + addBlankToString1(bill.get_INCENTIVE().trim()) + "\n");
            textData.append("REBATES ON MMC   :" + addBlankToString1(bill.get_REBATE_ON_MMC().trim()) + "\n");
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
            textData.append("     DETAILS OF LAST PAYMENT \n");
            textData.append("********************************\n");
            textData.append("LAST PAID AMOUNT   : " + bill.get_LAST_PAID_AMT().trim() + "\n");
            textData.append("LAST PAID DATE     : " + bill.get_LAST_PAY_DATE().trim() + "\n");
            textData.append("RECEIPT NUMBER     : " + bill.get_LAST_RCPT_NO().trim() + "\n");
            textData.append("METER READER       : " + CommonPref.getUserDetails(getApplicationContext()).get_UserID().trim() + "\n");
            textData.append("TOLLLFREE HELPLINE NO.: " + "1912" + "\n\n\n");
            conn.printData(textData.toString());
            textData.delete(0, textData.length());
            long c1 = localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
            if (c1 == 1) {
                Toast.makeText(getApplicationContext(), "Update database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error in database", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return false;
        }
        textData = null;

        return true;
    }

    private String addBlankToString1(String value) {

        int x = value.trim().length();
        for (int i = 0; i < 10 - x; i++) {
            value = " " + value;
        }

        return value;
    }

    private boolean createReceiptDataOfflineforanalogics() {
        String method = "";
        StringBuilder textData = new StringBuilder();
        Bluetooth_Printer_2inch_prof_ThermalAPI printer = new Bluetooth_Printer_2inch_prof_ThermalAPI();

        if (printer == null) {
            return false;
        }

        try {


            String website = "";
            String header = CommonPref.getUserDetails(mContext).get_DivId().substring(0, 1);
            if (header.equals("1")) {
                header = "             NBPDCL";
                website = "www.nbpdcl.in";
            } else if (header.equals("2")) {
                header = "             SBPDCL";
                website = "www.sbpdcl.in";
            } else {
                header = "";
            }
            textData.append(header + "\n");
            textData.append("******************************\n");
            textData.append("A/C NUMBER    :  " + mru1.get_ACT_NO().trim() + "\n");
            textData.append("DIVISION      :  " + CommonPref.getUserDetails(getApplicationContext()).get_DivName().trim() + "\n");
            textData.append("SUBDIVISION   :  " + CommonPref.getUserDetails(getApplicationContext()).get_SubdivName().trim() + "\n");
            textData.append("CON ID        :  " + mru1.get_CON_ID().trim() + "\n");
            textData.append("MRU           :  " + CommonPref.getUserDetails(getApplicationContext()).get_MRUNo() + "\n");
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


            textData.append("METER READER  : " + CommonPref.getUserDetails(getApplicationContext()).get_UserID().trim() + "\n");
            textData.append("TOLLLFREE HELPLINE NO. : " + "1912" + "\n\n\n\n\n");
            conn.printData(textData.toString());
            textData.delete(0, textData.length());
            long c1 = localDBHelper.updateBillDetails(bill.get_ACT_NO(), "Y");
            if (c1 == 1) {
                Toast.makeText(getApplicationContext(), "Update database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error in database", Toast.LENGTH_LONG).show();
            }


        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return false;
        }

        textData = null;

        return true;
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

    private void bluetooth() {
        // TODO Auto-generated method stub


        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth

                Toast.makeText(mContext, "Device does not support Bluetooth",
                        Toast.LENGTH_SHORT).show();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(mContext, "Bluetooth about to start.",
                        Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            if (bill.get_AMBI_Flag().toString().trim().equals("R")) {
                if (CommonPref.getPrinterType(getApplicationContext()).equals("E")) {
                    runPrintReceiptSequence(true);
                } else if (CommonPref.getPrinterType(getApplicationContext()).equals("A")) {
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

                        Intent intent = new Intent(MeterReadingStatusActivityForApl.this,
                                AnalogicsPrinterSetup.class);
                        startActivity(intent);
                        // finish();

                    }

                }

            } else {
                if (CommonPref.getPrinterType(getApplicationContext()).equals("E")) {
                    runPrintReceiptSequence(false);
                } else if (CommonPref.getPrinterType(getApplicationContext()).equals("A")) {
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

                    }
                }
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
    }

    private void CheckBlueToothState(Boolean flag) {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {

            Toast.makeText(getBaseContext(), "Bluetooth is NOT Enabled",
                    Toast.LENGTH_LONG).show();

        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (bluetoothAdapter.isDiscovering()) {
                    Toast.makeText(
                            getBaseContext(),
                            "Bluetooth is currently in device discovery process.",
                            Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getBaseContext(), "Bluetooth is Enabled.",
                            Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(getBaseContext(), "Bluetooth is NOT Enabled.",
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

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBlueToothState(true);
        } else if (requestCode == REQUEST_ENABLE_BT1) {
            CheckBlueToothState(false);
        }
    }*/


}
