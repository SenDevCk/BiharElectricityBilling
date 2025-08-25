package com.nic.app.biharelectricitybilling.ui;
import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.ConsumerAdapter;
import com.nic.app.biharelectricitybilling.entity.GPSTracker;
import com.nic.app.biharelectricitybilling.entity.MRUDetails;
import com.nic.app.biharelectricitybilling.entity.UserDetails;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.MarshmallowPermission;
import com.nic.app.biharelectricitybilling.util.Utiilties;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;
public class ConsumerActivity4A10 extends Activity {
    private static final int CAMERA_PIC = 100;
    private ActionBar actionBar;
    Button btnContinue;
    private static final int CAMERA_REQUEST = 1777;
    String fullImage = "image.jpg";
    final int PIC_CROP = 2;
    DataBaseHelper localDBHelper;
    ArrayList<MRUDetails> mruList;
    ScrollView scrollView;
    String fname;
    String reason = "";
    MarshmallowPermission MARSHMALLOW_PERMISSION = null;
    int count = 0;
    ConsumerAdapter adapter;
    String Unmeter = "";
    TextView tvAcNo, tvConNo, tvMeterNo, tvConName, tvAddress, tvCategory,
            tvLoad;
    EditText etMobileNo, etDTNo, etMeterNo;
    ListView dataList;
    LinearLayout lin_listview, lin_meterNo, IsmeterCorrect, isbillinglayout;
    String stringMobileNo = "0", stringDtNo;
    GPSTracker gps;
    // String Aplflag="N";
    String isadddressupdated = "N";
    RadioGroup radioGroup, radioGroupbilling;
    String category = "";
    RadioButton radioBtnYes, radioBtnNo, radioBtnYesbilling, radioBtnNobilling;
    Spinner spreason;
    Boolean isMeterNoCorrect, isbilling=true;
    File myDir=null;
    /*private static String IMAGE_FILE_PATH;*/
    private static String CROP_IMAGE_FILE_PATH;
    private static String CROP_IMAGE_FILE_PATH1;
    private static String IMAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        setContentView(R.layout.activity_consumer);
        // Database Opening
        localDBHelper = new DataBaseHelper(ConsumerActivity4A10.this);
        localDBHelper = new DataBaseHelper(this);
        try {
            localDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            localDBHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        // -------------------------------------------------------------------------------------
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
        // actionBar.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.back12, null));
        // For displaying title and subtitle and change text color
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Consumer Details </font>"));

        String pos = getIntent().getStringExtra("POS");
        String value = getIntent().getStringExtra("VALUE");
        initialization();
        isMeterNoCorrect = true;
        mruList = localDBHelper.getMRU2(pos, value);
        if (mruList.isEmpty() || mruList.size() <= 0) {
            scrollView.setVisibility(View.GONE);
            AlertDialogForNoResultFound();

        } else if (mruList.size() == 1) {
            valueInitialization();
        } else if (mruList.size() > 1) {

            loadMruList();
        }

        CROP_IMAGE_FILE_PATH = "/BEB/FullImage/"
                + CommonPref.getUserDetails(getApplicationContext())
						.get_MRUNo() + "/";
		CROP_IMAGE_FILE_PATH1 = "/BEB/CropImage/"
				+ CommonPref.getUserDetails(getApplicationContext())
						.get_MRUNo() + "/";
    }

    private void valueInitialization() {
        // TODO Auto-generated method stub
        for (MRUDetails mru : mruList) {
            scrollView.setVisibility(View.VISIBLE);
            lin_listview.setVisibility(View.GONE);
            tvAcNo.setText(mru.get_ACT_NO());
            tvConNo.setText(mru.get_CON_ID());
            mru.get_CATEGORY();
            tvConName.setText(mru.get_CNAME());
            tvAddress.setText(mru.get_Cfathername());
            tvCategory.setText(mru.get_CATEGORY());
      /*      if(mru.get_APL_CONSUMER().equalsIgnoreCase("Y") && mru.get_APL_BILLING_FLAG().equalsIgnoreCase("N")){
                Aplflag="Y";
            }*/
            if (mru.get_Is_address_updated().equalsIgnoreCase("Y")) {
                isadddressupdated = "Y";
            } else {
                isadddressupdated = "N";
            }
            tvLoad.setText(mru.get_LOAD());
            Unmeter = mru.get_METR_UNMETER();
            if (Unmeter.equals("UM")) {
                IsmeterCorrect.setVisibility(View.GONE);
                tvMeterNo.setText("UnMeter");
            } else {
                IsmeterCorrect.setVisibility(View.VISIBLE);
                tvMeterNo.setText(mru.get_METER_NO());

            }
            etMobileNo.setText(mru.get_CONTACT_NUM().trim());
            etDTNo.setText(mru.get_DT_NO().trim());
            stringMobileNo = mru.get_CONTACT_NUM().trim();
            category = mru.get_CATEGORY().trim();
            stringDtNo = mru.get_DT_NO().trim();
            IMAGE_NAME = mru.get_CON_ID() + ".jpg";

        }
        spreason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 == 0) {
                    reason = "0";
                } else {
                    reason = getResources().getStringArray(R.array.reason)[arg2].trim();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void initialization() {
        // TODO Auto-generated method stub
        tvAcNo = (TextView) findViewById(R.id.tv_ac_no);
        tvConNo = (TextView) findViewById(R.id.tv_con_no);
        tvMeterNo = (TextView) findViewById(R.id.tv_meter_no);
        tvConName = (TextView) findViewById(R.id.tv_con_name);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvCategory = (TextView) findViewById(R.id.tv_category);
        tvLoad = (TextView) findViewById(R.id.tv_load);
        etMobileNo = (EditText) findViewById(R.id.et_mobileNo);
        etDTNo = (EditText) findViewById(R.id.et_dtNo);
        etMeterNo = (EditText) findViewById(R.id.et_meterNo);
        scrollView = (ScrollView) findViewById(R.id.scrollview);
        btnContinue = (Button) findViewById(R.id.btn_continue);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group_meter);
        radioBtnYes = (RadioButton) findViewById(R.id.yes);
        radioBtnNo = (RadioButton) findViewById(R.id.no);
        IsmeterCorrect = (LinearLayout) findViewById(R.id.ismetercorrect);
        spreason = (Spinner) findViewById(R.id.et_meterNo_billing);
        radioGroupbilling = (RadioGroup) findViewById(R.id.radio_group_billing);
        radioBtnYesbilling = (RadioButton) findViewById(R.id.yes_billing);
        radioBtnNobilling = (RadioButton) findViewById(R.id.no_billing);
        isbillinglayout = (LinearLayout) findViewById(R.id.lin_new_billing);

        dataList = (ListView) findViewById(R.id.listConsumer);
        lin_listview = (LinearLayout) findViewById(R.id.lin_listview);
        lin_meterNo = (LinearLayout) findViewById(R.id.lin_new_meter);
        btnContinue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isbilling) {
                    if (category.equalsIgnoreCase("KJ")) {
                        if (Unmeter.equals("UM")) {
                            long a = localDBHelper.saveMeterNo("N", "", tvAcNo.getText().toString());
                            if (a > 0) {

                                if (!(etMobileNo.getText().toString().equals(stringMobileNo)) || !(etDTNo.getText().toString().equals(stringDtNo))) {
                                    // This line will call due to some changes in editbox
                                    if (!Utiilties.isOnline(ConsumerActivity4A10.this)) {
                                        long c = localDBHelper.updateMru(etMobileNo.getText().toString(), etDTNo.getText().toString(), tvAcNo.getText().toString(), "Y","","");
                                        if (c > 0) {
                                            localDBHelper.insertmobiledtnumber(etMobileNo.getText()
                                                            .toString(), etDTNo.getText().toString(),
                                                    tvAcNo.getText().toString());
                                            Toast.makeText(getApplicationContext(), "Update in Local DataBase", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                    "Error in Local Database",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        getgps();
                                    } else {
                                        // Online Code
                                        new updateDtNo().execute(etMobileNo.getText()
                                                        .toString(), etDTNo.getText().toString(),
                                                tvAcNo.getText().toString());
                                    }
                                } else {
                                    getgps();

                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Error in Inserting Meter Number",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else if (Unmeter.equals("MS")) {
                            if (isMeterNoCorrect) {

                                long a = localDBHelper.saveMeterNo("N", "", tvAcNo.getText().toString());

                                if (a > 0) {

                                    if (!(etMobileNo.getText().toString().equals(stringMobileNo))
                                            || !(etDTNo.getText().toString().equals(stringDtNo))) {
                                        // This line will call due to some changes in editbox
                                        if (!Utiilties.isOnline(ConsumerActivity4A10.this)) {
                                            // Offline code
                                            long c = localDBHelper.updateMru(etMobileNo.getText()
                                                            .toString(), etDTNo.getText().toString(),
                                                    tvAcNo.getText().toString(), "Y","","");
                                            if (c > 0) {
                                                localDBHelper.insertmobiledtnumber(etMobileNo.getText()
                                                                .toString(), etDTNo.getText().toString(),
                                                        tvAcNo.getText().toString());
                                                Toast.makeText(getApplicationContext(),
                                                        "Update in Local DataBase",
                                                        Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(),
                                                        "Error in Local Database",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                            callCameraIntent();
                                        } else {
                                            // Online Code
                                            new updateDtNo().execute(etMobileNo.getText()
                                                            .toString(), etDTNo.getText().toString(),
                                                    tvAcNo.getText().toString());
                                        }
                                    } else {

                                        callCameraIntent();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error in Inserting Meter Number",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                if (!etMeterNo.getText().toString().trim().isEmpty()) {

                                    long a = localDBHelper.saveMeterNo("Y", etMeterNo.getText().toString().trim(), tvAcNo.getText().toString());
                                    if (a > 0) {

                                        if (!(etMobileNo.getText().toString().equals(stringMobileNo))
                                                || !(etDTNo.getText().toString().equals(stringDtNo))) {
                                            // This line will call due to some changes in editbox
                                            if (!Utiilties.isOnline(ConsumerActivity4A10.this)) {
                                                // Offline code
                                                long c = localDBHelper.updateMru(etMobileNo.getText()
                                                                .toString(), etDTNo.getText().toString(),
                                                        tvAcNo.getText().toString(), "Y","","");
                                                if (c > 0) {
                                                    localDBHelper.insertmobiledtnumber(etMobileNo.getText()
                                                                    .toString(), etDTNo.getText().toString(),
                                                            tvAcNo.getText().toString());
                                                    Toast.makeText(getApplicationContext(),
                                                            "Update in Local DataBase",
                                                            Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Error in Local Database", Toast.LENGTH_LONG).show();
                                                }
                                                callCameraIntent();
                                            } else {
                                                // Online Code
                                                new updateDtNo().execute(etMobileNo.getText().toString(), etDTNo.getText().toString(), tvAcNo.getText().toString());
                                            }
                                        } else {

                                            callCameraIntent();
                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error in Inserting Meter Number",
                                                Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "Kindly Enter Valid Meter Number",
                                            Toast.LENGTH_LONG).show();
                                }

                            }

                        }

                    } else if (!isvalidmobileno(etMobileNo.getText().toString())) {
                        Toast.makeText(ConsumerActivity4A10.this, "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                    } else if ((localDBHelper.getmobileno(etMobileNo.getText().toString()) > 3)) {
                        Toast.makeText(ConsumerActivity4A10.this, "You Have Entered This Number More Than 3 Times", Toast.LENGTH_SHORT).show();
                    } else {
                        if (Unmeter.equals("UM")) {
                            long a = localDBHelper.saveMeterNo("N", "", tvAcNo.getText().toString());
                            if (a > 0) {

                                if (!(etMobileNo.getText().toString().equals(stringMobileNo)) || !(etDTNo.getText().toString().equals(stringDtNo))) {
                                    // This line will call due to some changes in editbox
                                    if (!Utiilties.isOnline(ConsumerActivity4A10.this)) {
                                        // Offline code
                                        long c = localDBHelper.updateMru(etMobileNo.getText()
                                                        .toString(), etDTNo.getText().toString(),
                                                tvAcNo.getText().toString(), "Y","","");
                                        if (c > 0) {
                                            localDBHelper.insertmobiledtnumber(etMobileNo.getText()
                                                            .toString(), etDTNo.getText().toString(),
                                                    tvAcNo.getText().toString());
                                            Toast.makeText(getApplicationContext(),
                                                    "Update in Local DataBase",
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                    "Error in Local Database",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        getgps();
                                    } else {
                                        // Online Code
                                        new updateDtNo().execute(etMobileNo.getText()
                                                        .toString(), etDTNo.getText().toString(),
                                                tvAcNo.getText().toString());
                                    }
                                } else {
                                    getgps();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Error in Inserting Meter Number",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else if (Unmeter.equals("MS")) {
                            if (isMeterNoCorrect) {
                                long a = localDBHelper.saveMeterNo("N", "", tvAcNo.getText().toString());
                                if (a > 0) {
                                    if (!(etMobileNo.getText().toString().equals(stringMobileNo))
                                            || !(etDTNo.getText().toString().equals(stringDtNo))) {
                                        // This line will call due to some changes in editbox
                                        if (!Utiilties.isOnline(ConsumerActivity4A10.this)) {
                                            // Offline code
                                            long c = localDBHelper.updateMru(etMobileNo.getText()
                                                            .toString(), etDTNo.getText().toString(),
                                                    tvAcNo.getText().toString(), "Y","","");
                                            if (c > 0) {
                                                localDBHelper.insertmobiledtnumber(etMobileNo.getText()
                                                                .toString(), etDTNo.getText().toString(),
                                                        tvAcNo.getText().toString());
                                                Toast.makeText(getApplicationContext(),
                                                        "Update in Local DataBase",
                                                        Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error in Local Database", Toast.LENGTH_LONG).show();
                                            }
                                            callCameraIntent();
                                        } else {
                                            // Online Code
                                            new updateDtNo().execute(etMobileNo.getText()
                                                            .toString(), etDTNo.getText().toString(),
                                                    tvAcNo.getText().toString());
                                        }
                                    } else {

                                        callCameraIntent();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error in Inserting Meter Number",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                if (!etMeterNo.getText().toString().trim().isEmpty()) {

                                    long a = localDBHelper.saveMeterNo("Y", etMeterNo.getText().toString().trim(), tvAcNo.getText().toString());
                                    if (a > 0) {

                                        if (!(etMobileNo.getText().toString().equals(stringMobileNo))
                                                || !(etDTNo.getText().toString().equals(stringDtNo))) {
                                            // This line will call due to some changes in editbox
                                            if (!Utiilties.isOnline(ConsumerActivity4A10.this)) {
                                                // Offline code
                                                long c = localDBHelper.updateMru(etMobileNo.getText()
                                                                .toString(), etDTNo.getText().toString(),
                                                        tvAcNo.getText().toString(), "Y","","");
                                                if (c > 0) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Update in Local DataBase",
                                                            Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Error in Local Database",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                                callCameraIntent();
                                            } else {
                                                // Online Code
                                                new updateDtNo().execute(etMobileNo.getText().toString(), etDTNo.getText().toString(), tvAcNo.getText().toString());
                                            }
                                        } else {

                                            callCameraIntent();
                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error in Inserting Meter Number",
                                                Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "Kindly Enter Valid Meter Number",
                                            Toast.LENGTH_LONG).show();
                                }

                            }

                        }
                    }

                } else {
                    if (reason.equalsIgnoreCase("") || reason.equalsIgnoreCase("0")) {
                        Toast.makeText(ConsumerActivity4A10.this, "Please select Reason", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!Utiilties.isOnline(ConsumerActivity4A10.this)) {
                          Long i = localDBHelper.saveBillreason(reason, tvAcNo.getText().toString());
                          if(i>=0){
                              finish();
                          }else{
                              Toast.makeText(ConsumerActivity4A10.this, "Error in local database", Toast.LENGTH_SHORT).show();
                          }
                        }else{
                            new updatebillingreason().execute(tvConNo.getText().toString(),reason);
                        }
                    }
                }
            }
        });

        // ListView Item Click Listener
        dataList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                // ListView Clicked item value
                String conId = ((TextView) view.findViewById(R.id.tv_con_id))
                        .getText().toString();
                for (MRUDetails mru : mruList) {
                    if (mru.get_CON_ID().equals(conId)) {
                        scrollView.setVisibility(View.VISIBLE);
                        lin_listview.setVisibility(View.GONE);
                        tvAcNo.setText(mru.get_ACT_NO());
                        tvConNo.setText(mru.get_CON_ID());
                        tvMeterNo.setText(mru.get_METER_NO());
                        tvConName.setText(mru.get_CNAME());
                        tvAddress.setText(mru.get_Cfathername());
                        tvCategory.setText(mru.get_CATEGORY());
                        tvLoad.setText(mru.get_LOAD());
                        etMobileNo.setText(mru.get_CONTACT_NUM().trim());
                        etDTNo.setText(mru.get_DT_NO().trim());
                        stringMobileNo = mru.get_CONTACT_NUM().trim();
                        stringDtNo = mru.get_DT_NO().trim();
                        IMAGE_NAME = mru.get_CON_ID() + ".jpg";
                    }
                }

            }

        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes) {
                    //	Toast.makeText(getApplicationContext(), "choice: Yes", Toast.LENGTH_SHORT).show();
                    lin_meterNo.setVisibility(View.GONE);
                    isMeterNoCorrect = true;
                } else if (checkedId == R.id.no) {
                    //	Toast.makeText(getApplicationContext(), "choice: No", Toast.LENGTH_SHORT).show();
                    lin_meterNo.setVisibility(View.VISIBLE);
                    isMeterNoCorrect = false;
                }
            }
        });
        radioGroupbilling.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes_billing) {
                    //	Toast.makeText(getApplicationContext(), "choice: Yes", Toast.LENGTH_SHORT).show();
                    isbillinglayout.setVisibility(View.GONE);
                    isbilling = true;
                } else if (checkedId == R.id.no_billing) {
                    //	Toast.makeText(getApplicationContext(), "choice: No", Toast.LENGTH_SHORT).show();
                    isbillinglayout.setVisibility(View.VISIBLE);
                    isbilling = false;
                }
            }
        });

    }

    /**
     * Retrives the result returned from selecting image, by invoking the method
     * <code>selectImageFromGallery()</code>
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if request code is same we pass as argument in startActivityForResult
        if (requestCode == 1) {
            if (resultCode == RESULT_CANCELED) {
            } else {
                File file=null;
       /*         if(android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.Q){
                    byte[] imgData = data.getByteArrayExtra("CapturedImage");
                    Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0,
                            imgData.length);
                   // Uri selectedImage = data.getData();
                    try {
                        *//*the user's device may not support cropping*//*
                        cropCapturedImage(getImageUri(getApplicationContext(),bmp));

                    } catch (ActivityNotFoundException aNFE) {
                        //display an error message if user device doesn't support
                        String errorMessage = "Sorry - your device doesn't support the crop action!";
                        Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }else{
                    file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");

                    try {
                        *//*the user's device may not support cropping*//*
                        cropCapturedImage(Uri.fromFile(file));

                    } catch (ActivityNotFoundException aNFE) {
                        //display an error message if user device doesn't support
                        String errorMessage = "Sorry - your device doesn't support the crop action!";
                        Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }*/

                String root=null;
                root= getApplicationContext().getExternalFilesDir(CROP_IMAGE_FILE_PATH).toString();
                myDir = new File(root+"/"+tvConNo.getText().toString().trim());
                try {
                    /*the user's device may not support cropping*/
                    cropCapturedImage(Uri.fromFile(myDir));
                } catch (ActivityNotFoundException aNFE) {
                    //display an error message if user device doesn't support
                    String errorMessage = "Sorry - your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
        if (requestCode == 5){ /*
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);

            Bitmap bitmap1 = null;
            try {
                bitmap1 = Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                // bitmap1= Utiilties.GenerateThumbnail(bitmap1, 500, 500);
                int i = bitmap1.getByteCount();
                Bitmap bitmap2;
                if (i > 250000) {
                  *//*  bitmap2 = ShrinkBitmap(bitmap1, 300, 200);
                    int i3=bitmap2.getByteCount();*//*
                    bitmap2 = getResizedBitmap(bitmap1, 250);
                } else {
                    bitmap2 = bitmap1;
                }
                SaveImage(bitmap2);
                //  scannedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_CANCELED) {
            }  if (resultCode == RESULT_OK) {
                //Create an instance of bundle and get the returned data
                Bitmap bitmap = null;
                try {
                    Bundle extras = data.getExtras();
                     bitmap = extras.getParcelable("data");
                  //  bitmap = Images.Media.getBitmap(this.getContentResolver(), data.getData());
                     if(bitmap==null){
                        bitmap = Images.Media.getBitmap(this.getContentResolver(), data.getData());
                       //  Uri imageUri = data.getData();
                       //  bitmap = Images.Media.getBitmap(this.getContentResolver(), imageUri);
                     }
                   /* int i = thePic.getByteCount();
                    Bitmap bitmap2;
                    if (i > 250000) {
                        // bitmap2 = ShrinkBitmap(thePic, 300, 200);
                        bitmap2 = getResizedBitmap(thePic, 250);
                    } else {
                        bitmap2 = thePic;
                    }*/
                    SaveImage(bitmap);
                } catch (Exception ex) {
                    try {
                       // Utiilties.writeIntoLog(Log.getStackTraceString(ex));
                        bitmap = Images.Media.getBitmap(this.getContentResolver(), data.getData());
                     /*   int i = bitmap.getByteCount();
                        Bitmap bitmap2;
                        if (i > 250000) {
                            // bitmap2 = ShrinkBitmap(bitmap, 300, 200);
                            bitmap2 = getResizedBitmap(bitmap, 250);
                        } else {
                            bitmap2 = bitmap;
                        }*/
                        SaveImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public void AlertDialogForNoResultFound() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ConsumerActivity4A10.this);
        // Setting Dialog Title
        alertDialog.setTitle("Result Not Found!!");
        // Setting Dialog Message
        alertDialog
                .setMessage("No Consumer Details had been found regarding your search.\n Please Try Again...");
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.bulb_1);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(getBaseContext(),
                                BillingHomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // do something on back.

            if (mruList.size() > 1 && lin_listview.getVisibility() == View.GONE) {
                loadMruList();

            } else if (mruList.size() > 1
                    && lin_listview.getVisibility() == View.VISIBLE) {
                finish();
            } else {
                finish();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void loadMruList() {
        scrollView.setVisibility(View.GONE);
        lin_listview.setVisibility(View.VISIBLE);
        try {
            adapter = new ConsumerAdapter(ConsumerActivity4A10.this,
                    R.layout.list_row_consumer, mruList);
            dataList.setAdapter(adapter);

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error in loading",
                    Toast.LENGTH_SHORT).show();
        //    Utiilties.writeIntoLog(Log.getStackTraceString(ex));
        }
    }

    public void callCameraIntent() {
        String isPendingBill = localDBHelper.getIsPendingBill(tvAcNo.getText().toString().trim());
        if (!(isPendingBill.trim().equalsIgnoreCase("P") || isPendingBill.trim().equalsIgnoreCase("G"))) {
            gps = new GPSTracker(ConsumerActivity4A10.this);
            // check if GPS enabled
            if (gps.canGetLocation()) {
       /*         if(android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.Q){
                    Intent iCamera = new Intent(getApplicationContext(), CameraActivity.class);
                    iCamera.putExtra("KEY_PIC", "6");
                    iCamera.putExtra("idname", "meterphoto");
                    startActivityForResult(iCamera, 1);
                   *//* Intent intent = new Intent(this, ScanActivity.class);
                    intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);
                    startActivityForResult(intent, 5);*//*
                }
            else   if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    Intent intent = new Intent(this, ScanActivity.class);
                    intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);
                    startActivityForResult(intent, 5);
                } else{
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(intent, 1);
                }*/
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                String root= getApplicationContext().getExternalFilesDir(CROP_IMAGE_FILE_PATH).toString();
                myDir = new File(root+"/"+tvConNo.getText().toString().trim());
               /* root = Environment.getExternalStorageDirectory().getPath()
                        + "/SBDocs/Photos_Crop" + "/" + utildb.getSdoCode() + "/"
                        + utildb.getActiveMRU();*/
              //  myDir = new File(root+"/"+tvConNo.getText().toString().trim()+".jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(myDir));
                startActivityForResult(intent, 1);

            } else {
                gps.showSettingsAlert();
            }
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Title
            alertDialog.setTitle("Alert!!!");
            String msg = "";
            if (isPendingBill.trim().equalsIgnoreCase("P")) {
                msg = "The Bill has been already captured, please sync the data for Bill Generation";
            } else if (isPendingBill.trim().equalsIgnoreCase("G")) {
                msg = "The Bill has been already generated for choosen Consumer.";
            }
            // Setting Dialog Message
            alertDialog.setMessage(msg);

            // On pressing Settings button
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            // Showing Alert Message
            alertDialog.show();
        }
     }

    private class updateDtNo extends AsyncTask<String, Void, String> {

        public updateDtNo() {

        }

        private final ProgressDialog dialog = new ProgressDialog(
                ConsumerActivity4A10.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                ConsumerActivity4A10.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog
                    .setMessage("Please wait. \n Updating DT and Mobile Number...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... param) {
            UserDetails user = CommonPref
                    .getUserDetails(getApplicationContext());
            String res1 = WebServiceHelper.UpdateMRU(param[0], param[1],
                    param[2], user);
            return res1;
        }

        @Override
        protected void onPostExecute(String result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                alertDialog.setTitle("Updating MRU..");
                if (result != null) {
                    alertDialog.setMessage("Synchronizing...");
                    alertDialog.show();
                    if (result.trim().equalsIgnoreCase("Update Successfull")) {
                        long c = localDBHelper.updateMru(etMobileNo.getText()
                                        .toString(), etDTNo.getText().toString(),
                                tvAcNo.getText().toString(), "N","","");
                        if (c > 0) {
                            Toast.makeText(getApplicationContext(), result,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Error in Local Database",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    long c = localDBHelper.updateMru(etMobileNo.getText()
                            .toString(), etDTNo.getText().toString(), tvAcNo
                            .getText().toString(), "Y","","");
                    Toast.makeText(getApplicationContext(), "Error in Network",
                            Toast.LENGTH_LONG).show();
                }

                alertDialog.cancel();
                if (Unmeter.equals("UM")) {
                    getgps();
                } else if (Unmeter.equals("MS")) {
                    callCameraIntent();
                }
            }

        }
    }
    private class updatebillingreason extends AsyncTask<String, Void, String> {

        public updatebillingreason() {

        }

        private final ProgressDialog dialog = new ProgressDialog(
                ConsumerActivity4A10.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                ConsumerActivity4A10.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog
                    .setMessage("Please wait. \n Updating Reason Of not billing...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }
        @Override
        protected String doInBackground(String... param) {
            UserDetails user = CommonPref
                    .getUserDetails(getApplicationContext());
            return WebServiceHelper.Updatereason(param[0], param[1], user);
        }

        @Override
        protected void onPostExecute(String result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                alertDialog.setTitle("Updating MRU..");
                if (result != null) {
                    alertDialog.setMessage("Synchronizing...");
                    alertDialog.show();
                    if (result.trim().contains("SUCCESS")) {
                        long c = localDBHelper.updatereasonstatus(tvAcNo.getText().toString(), "N");
                        if (c > 0) {
                            Toast.makeText(getApplicationContext(), result,
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Error in Local Database",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                  /*  long c = localDBHelper.updateMru(etMobileNo.getText()
                            .toString(), etDTNo.getText().toString(), tvAcNo
                            .getText().toString(), "Y");
                    Toast.makeText(getApplicationContext(), "Error in Network",
                            Toast.LENGTH_LONG).show();*/
                  finish();
                }

                alertDialog.cancel();
            }

        }
    }
  /*  private void cropCapturedImage(Uri picUri) {
        Toast.makeText(ConsumerActivity4A10.this, "pick image"+picUri, Toast.LENGTH_SHORT).show();

            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerIntent.setType("image/*");
            photoPickerIntent.putExtra("crop", "true");
            photoPickerIntent.putExtra("scale", true);
            photoPickerIntent.putExtra("outputX", 256);
            photoPickerIntent.putExtra("outputY", 256);
            photoPickerIntent.putExtra("aspectX", 1);
            photoPickerIntent.putExtra("aspectY", 1);
            photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
            startActivityForResult(photoPickerIntent, 2);

    }*/
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
        //cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, 2);
    }

    private void SaveImage(Bitmap finalBitmap) {
        String success = "";
        String root=null;
  /*      if(android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.Q){
          //  root = Environment.getRootDirectory().toString();
           // root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
             root= getApplicationContext().getExternalFilesDir(CROP_IMAGE_FILE_PATH).toString();
             myDir = new File(root);
        }else {
            root = Environment.getExternalStorageDirectory().toString();
             myDir = new File(root+"/"+CROP_IMAGE_FILE_PATH);
        }*/
        root= getApplicationContext().getExternalFilesDir(CROP_IMAGE_FILE_PATH1).toString();
        myDir = new File(root);
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        fname = IMAGE_NAME;
        File file = new File(myDir, fname);
        //String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), finalBitmap, "Title", null);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            success = "OK";
        } catch (Exception e) {
            e.printStackTrace();
//            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        if (success.matches("OK")) {
            // create class object
            gps = new GPSTracker(ConsumerActivity4A10.this);
            double latitude = 0.0;
            double longitude = 0.0;
            long dateTime = 0;
            // check if GPS enabled
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                dateTime = gps.getTime();
                String date = Utiilties.getDate(dateTime, "dd/MM/yyyy");
                if (latitude > 0.0 && longitude > 0.0) {
                    long c = localDBHelper.savePhoto(String.valueOf(latitude),
                            String.valueOf(longitude), myDir+"/"
                                    , tvConNo.getText().toString()
                                    .trim(), date,"","");
                    if (c > 0) {
                        if (isadddressupdated.equalsIgnoreCase("Y")) {
                            Intent intent = new Intent(getBaseContext(),
                                    MeterReadingStatusActivity.class);
                            intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
                                    .toString().trim());
                            intent.putExtra("FLAG", "0");
                            startActivity(intent);
                            finish();
                        } else if (Integer.parseInt(CommonPref.getUserDetails(ConsumerActivity4A10.this).get_distcode()) == 236) {
                            Intent intent = new Intent(getBaseContext(),
                                    MeterReadingStatusActivity.class);
                            intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
                                    .toString().trim());
                            intent.putExtra("FLAG", "0");
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(getBaseContext(),
                                    User_FirstHomeActivity.class);
                            intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
                                    .toString().trim());
                            intent.putExtra("CON_ID", tvConNo.getText()
                                    .toString().trim());
                            intent.putExtra("CON_NAME", tvConName.getText()
                                    .toString().trim());
                            intent.putExtra("FLAG", "0");
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Error in Local Database", Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    long c = localDBHelper.savePhoto("9999", "9999", myDir
                            + "/", tvConNo.getText().toString()
                            .trim(), date,"","");
                    if (c > 0) {
                        if (isadddressupdated.equalsIgnoreCase("Y")) {
                            Intent intent = new Intent(getBaseContext(),
                                    MeterReadingStatusActivity.class);
                            intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
                                    .toString().trim());
                            intent.putExtra("FLAG", "0");
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(getBaseContext(),
                                    User_FirstHomeActivity.class);
                            intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
                                    .toString().trim());
                            intent.putExtra("CON_ID", tvConNo.getText()
                                    .toString().trim());
                            intent.putExtra("CON_NAME", tvConName.getText()
                                    .toString().trim());
                            intent.putExtra("FLAG", "0");
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Error in Local Database", Toast.LENGTH_LONG)
                                .show();

                    }
					/*	latitude=9.9;
						longitude=9.9;

						Toast.makeText(getApplicationContext(),
								"Please Wait a while until GPS is stable", Toast.LENGTH_LONG)
								.show();*/
                }

            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
        } else {
            Toast.makeText(getApplicationContext(), success, Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void getgps() {

        // create class object
        gps = new GPSTracker(ConsumerActivity4A10.this);
        double latitude = 0.0;
        double longitude = 0.0;
        long dateTime = 0;

        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            dateTime = gps.getTime();
            String date = Utiilties.getDate(dateTime, "dd/MM/yyyy");
            if (latitude > 0.0 && longitude > 0.0) {

                long c = localDBHelper.savePhoto(String.valueOf(latitude),
                        String.valueOf(longitude), "", tvConNo.getText().toString()
                                .trim(), date,"","");
                if (c > 0) {
                    if (isadddressupdated.equalsIgnoreCase("Y")) {
                        Intent intent = new Intent(getBaseContext(),
                                MeterReadingStatusActivity.class);
                        intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
                                .toString().trim());
                        intent.putExtra("FLAG", "0");
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getBaseContext(),
                                User_FirstHomeActivity.class);
                        intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
                                .toString().trim());
                        intent.putExtra("CON_ID", tvConNo.getText()
                                .toString().trim());
                        intent.putExtra("CON_NAME", tvConName.getText()
                                .toString().trim());
                        intent.putExtra("FLAG", "0");
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error in Local Database", Toast.LENGTH_LONG)
                            .show();
                }
            } else {
                long c = localDBHelper.savePhoto(String.valueOf("9999"),
                        String.valueOf("9999"), "", tvConNo.getText().toString()
                                .trim(), date,"","");
                if (c > 0) {
                    if (isadddressupdated.equalsIgnoreCase("Y")) {
                        Intent intent = new Intent(getBaseContext(),
                                MeterReadingStatusActivity.class);
                        intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
                                .toString().trim());
                        intent.putExtra("FLAG", "0");
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getBaseContext(),
                                User_FirstHomeActivity.class);
                        intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
                                .toString().trim());
                        intent.putExtra("FLAG", "0");
                        intent.putExtra("CON_ID", tvConNo.getText()
                                .toString().trim());
                        intent.putExtra("CON_NAME", tvConName.getText()
                                .toString().trim());
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error in Local Database", Toast.LENGTH_LONG)
                            .show();
                }

            }
        } else {

            gps.showSettingsAlert();
        }
    }

    public Boolean isvalidmobileno(String mobno) {
        Pattern mobileno = Pattern.compile("^(((\\+?\\(91\\))|0|((00|\\+)?91))-?)?[6-9]\\d{9}$");
        Boolean isvalidmobileno = mobileno.matcher(mobno).matches();
        return isvalidmobileno;
    }

    @Override
    protected void onResume() {
        MARSHMALLOW_PERMISSION = new MarshmallowPermission(ConsumerActivity4A10.this, Manifest.permission.CAMERA);
        MARSHMALLOW_PERMISSION = new MarshmallowPermission(ConsumerActivity4A10.this, Manifest.permission.ACCESS_FINE_LOCATION);
        super.onResume();
    }

    Bitmap ShrinkBitmap(Bitmap file, int width, int height) {
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = file;
        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);
        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        // bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        //this gives the size of the compressed image in kb
        long lengthbmp = imageInByte.length / 1024;

        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream("/sdcard/mediaAppPhotos/compressed_new.jpg"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        SimpleDateFormat m_sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
      String  m_curentDateandTime = m_sdf.format(new Date());
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, m_curentDateandTime, null);
        return Uri.parse(path);
    }

}
