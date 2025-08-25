package com.nic.app.biharelectricitybilling.ui;
import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.ConsumerAdapter;
import com.nic.app.biharelectricitybilling.entity.Establishment_details;
import com.nic.app.biharelectricitybilling.entity.GPSTracker;
import com.nic.app.biharelectricitybilling.entity.MRUDetails;
import com.nic.app.biharelectricitybilling.entity.UserDetails;
import com.nic.app.biharelectricitybilling.util.AppUtils;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.MarshmallowPermission;
import com.nic.app.biharelectricitybilling.util.Utiilties;
import com.nic.app.biharelectricitybilling.util.imageutils;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
public class ConsumerActivity4A11new extends Activity {
    private static final int CAMERA_PIC = 100;
    private static final int CAMERA_REQUEST = 1777;
    /*private static String IMAGE_FILE_PATH;*/
    private static String CROP_IMAGE_FILE_PATH;
    private static String IMAGE_NAME;
    private static String CROP_IMAGE_FILE_PATH1;
    public final String APP_TAG1 = "MyBillingAppCrop";
    final int PIC_CROP = 2000;
    private boolean isEditable = false;
    public String APP_TAG = "MyBillingApp";
    public String mru_no = "Nomru";
    public String photoFileName = "";
    public String photoFileName1 = "";
    Button btnContinue;
    String fullImage = "image.jpg";
    DataBaseHelper localDBHelper;
    ArrayList<MRUDetails> mruList;
    ArrayList<Establishment_details> PurposeList;
    ScrollView scrollView;
    String fname;
    double latitude = 0.0;
    double longitude = 0.0;
    String reason = "";
    MarshmallowPermission MARSHMALLOW_PERMISSION = null;
    int count = 0;
    ConsumerAdapter adapter;
    String Unmeter = "";
    TextView tvAcNo, tvConNo, tvMeterNo, tvConName, tvAddress, tvCategory,
            tvLoad;
    EditText etMobileNo, etDTNo, etMeterNo;
    //    TextView etMobileNo;
    ListView dataList;
    LinearLayout lin_listview, lin_meterNo, IsmeterCorrect, isbillinglayout, layout_pupose;
    String stringMobileNo = "0", Previous_read, stringDtNo, read_stats, stringRecDemd, maxdemand = "", powerfactor = "", stringPowFact;
    GPSTracker gps;
    // String Aplflag="N";
    String isadddressupdated = "N";
    RadioGroup radioGroup, radioGroupbilling, rggender;
    String category = "", Load = "", Meter_no = "", Phase = "", AREA_CODE = "", Sec_div_code = "", DIV_CODE = "", Prv_read_Date = "", REC_DEEMAND = "", POWER_FACTOR = "";
    RadioButton radioBtnYes, radioBtnNo, radioBtnYesbilling, radioBtnNobilling, rbmale, rbfemale;
    Spinner spreason, sppurpose;
    Boolean isMeterNoCorrect, isbilling = true;
    File myDir = null;
    String[] paramarray;
    ArrayList<String> reqread = new ArrayList<String>();
    Bundle bundle = null;
    String Ocr = "N";
    String DATA_Engines = "N";
    File photoFile;
    Uri fileProvider = null;
    Uri fileProvider2 = null;
    UserDetails user = null;
    String ocr_agency = "null";
    String bill_agency = "null";
    String cgender = "N";
    String cpurpose = "N";
    String Category = "";
    ArrayList<String> PurposeListBycat = null;
    private ActionBar actionBar;

    String kvah="",kwh="";
    boolean isNDSdataFound=false,flag=false;


    public static Bitmap getBitmapFromPath(String path) {
        Bitmap scaleImage = null;
        try {
            FileInputStream fi = new FileInputStream(path);
            return BitmapFactory.decodeStream(fi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return scaleImage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        setContentView(R.layout.activity_consumer);
        // Database Opening
        localDBHelper = new DataBaseHelper(ConsumerActivity4A11new.this);
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
        // -------------------------------------------  ------------------------------------------
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
        // actionBar.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.back12, null));
        // For displaying title and subtitle and change text color
        //current reading
        etMobileNo = findViewById(R.id.et_mobileNo);
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Consumer Details (D1)</font>"));
        String pos = getIntent().getStringExtra("POS");
        String value = getIntent().getStringExtra("VALUE");
        user = CommonPref.getUserDetails(getApplicationContext());
        ocr_agency = user.get_ocr_agency();
        //ocr_agency = "SUJANIX";
        Log.d("log_ocr_agency",ocr_agency);
        bill_agency = user.get_bill_agency();
        sppurpose =  findViewById(R.id.sp_purpose);
        PurposeListBycat = new ArrayList<String>();
        PurposeList = new ArrayList<Establishment_details>();
        layout_pupose =  findViewById(R.id.lin_new_purpose);
        initialization();
        isMeterNoCorrect = true;
        mruList = localDBHelper.getMRU2(pos, value);
        if (mruList.isEmpty() || mruList.size() <= 0) {
            scrollView.setVisibility(View.GONE);
            AlertDialogForNoResultFound();
            Toast.makeText(this, "" + this, Toast.LENGTH_LONG).show();

        } else if (mruList.size() == 1) {
            valueInitialization();
        } else if (mruList.size() > 1) {
            loadMruList();
        }
        CROP_IMAGE_FILE_PATH = "/BEB/FullImage/"
                + user.get_MRUNo() + "/";
        CROP_IMAGE_FILE_PATH1 = "/BEB/CropImage/" + user.get_MRUNo() + "/";
        mru_no = user.get_MRUNo();
        photoFileName = tvConNo.getText().toString().trim() + ".jpg";
        // photoFileName1 = tvConNo.getText().toString().trim() + "1.jpg";
    }

    private void valueInitialization() {
        // TODO Auto-generated method stub
        for (MRUDetails mru : mruList) {
            scrollView.setVisibility(View.VISIBLE);
            lin_listview.setVisibility(View.GONE);
//            if(mru.get_CONTACT_NUM()==null || mru.get_CONTACT_NUM().equals("")){
//                etMobileNo.setEnabled(true);
//                isEditable=true;
//            }
//           else {
//                etMobileNo.setEnabled(false);
//            }
            tvAcNo.setText(mru.get_ACT_NO());
            tvConNo.setText(mru.get_CON_ID());
            Category = mru.get_CATEGORY();
            tvConName.setText(mru.get_CNAME());
            tvAddress.setText(mru.get_Cfathername());
            if (CommonPref.getUserDetails(ConsumerActivity4A11new.this).get_UserID().equalsIgnoreCase("1401MRC132")) {
                String sta = mru.get_CATEGORY() + "," + mru.get_Previous_read_stat() + "," + mru.get_PREVIOUS_READ();
                tvCategory.setText(sta);
            } else {
                tvCategory.setText(mru.get_CATEGORY());
            }
            if (!(mru.get_Gender().equalsIgnoreCase("NA") || mru.get_Gender().equalsIgnoreCase("null"))) {
                cgender = mru.get_Gender();
                if (cgender.equalsIgnoreCase("M")) {
                    rbmale.setChecked(true);
                } else if (cgender.equalsIgnoreCase("F")) {
                    rbfemale.setChecked(true);
                }
            } else {
                cgender = "N";
            }
            if ((mru.get_CATEGORY().equalsIgnoreCase("LTIS2D") || mru.get_CATEGORY().equalsIgnoreCase("LTIS1D")) && (mru.get_purpose().equalsIgnoreCase("NA") || mru.get_purpose().equalsIgnoreCase("N") || mru.get_purpose().equalsIgnoreCase("null") || mru.get_purpose().length() >= 5)) {
                PurposeList = localDBHelper.getestablishment_details("LTS");
                //    Toast.makeText(this, "LTS" + PurposeList.size(), Toast.LENGTH_SHORT).show();
                for (Establishment_details purpose : PurposeList) {
                    PurposeListBycat.add(purpose.get_Establishment().trim());
                }

                setspiner(PurposeListBycat, mru.get_purpose());
            } else if ((mru.get_CATEGORY().equalsIgnoreCase("NDS2D") || mru.get_CATEGORY().equalsIgnoreCase("NDS1D")) && (mru.get_purpose().equalsIgnoreCase("NA") || mru.get_purpose().equalsIgnoreCase("N") || mru.get_purpose().equalsIgnoreCase("null") || mru.get_purpose().length() >= 5)) {
                PurposeList = localDBHelper.getestablishment_details("NDS");
                //     Toast.makeText(this, "NDS" + PurposeList.size(), Toast.LENGTH_SHORT).show();
                for (Establishment_details purpose : PurposeList) {
                    PurposeListBycat.add(purpose.get_Establishment().trim());
                }
                setspiner(PurposeListBycat, mru.get_purpose());
           /*     PurposeListBycat.add(0, "Select Purpose");
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, PurposeListBycat);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sppurpose.setAdapter(adapter);*/

            } else {
                layout_pupose.setVisibility(View.GONE);

            }
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
            // mobile no 4 character encripted.
            if (mru.get_CONTACT_NUM() == null || mru.get_CONTACT_NUM().trim().equals("") || mru.get_CONTACT_NUM().trim().equalsIgnoreCase("null") || mru.get_CONTACT_NUM().equalsIgnoreCase("NA")) {
                etMobileNo.setEnabled(true);
                isEditable = true;
            } else {

                if (mru.get_CONTACT_NUM().trim().length() != 10) {
                    etMobileNo.setEnabled(true);
                    isEditable = true;
                }
               /* if (mru.get_CONTACT_NUM().trim().length() >= 10) {
                    //     String phoneno=mru.get_CONTACT_NUM().trim();
                    //     editText.setTransformationMethod(new ChangeTransformationMethod());
                    etMobileNo.setText(mru.get_CONTACT_NUM().trim());
                    etMobileNo.setTransformationMethod(new ChangeTransformationMethod());
                }*/
                else {
                    etMobileNo.setText(mru.get_CONTACT_NUM().trim());
                    etMobileNo.setEnabled(false);
                    isEditable = false;
                }
            }

            etDTNo.setText(mru.get_DT_NO().trim());
            stringMobileNo = mru.get_CONTACT_NUM().trim();
            category = mru.get_CATEGORY().trim();
            stringDtNo = mru.get_DT_NO().trim();
            IMAGE_NAME = mru.get_CON_ID() + ".jpg";
            stringPowFact = mru.get_POW_FACT();
            stringRecDemd = mru.get_REC_DEM();
            Previous_read = mru.get_PREVIOUS_READ().trim();
            Load = mru.get_LOAD();
            Meter_no = mru.get_METER_NO();
            Prv_read_Date = mru.get_LAST_BILL_DATE();
            Phase = mru.get_PHASE();
         /*   REC_DEEMAND=mru.get_REC_DEM();
            POWER_FACTOR=mru.get_Power_Factor();*/
            //   AREA_CODE=CommonPref.getUserDetails(ConsumerActivity.this).get_MRUNo();
            Sec_div_code = mru.get_SECTION_ID();
            DIV_CODE = Sec_div_code.substring(0, 3);
            if (bill_agency.contains("DATA INGENIOUS")) {
                if (AppUtils.isPackageInstalled(ConsumerActivity4A11new.this, "com.datainfosys.bpdcl")) {
                    DATA_Engines = "Y";
                } else {
                    DATA_Engines = "N";
                    //   Toast.makeText(this, "Please Install company applications", Toast.LENGTH_SHORT).show();
                }
            } else {
                DATA_Engines = "N";
            }
           /* if (DIV_CODE.equals("226") || DIV_CODE.equals("227") || DIV_CODE.equals("244")) {
                Ocr = "Y";
            } else *//*if ((mru.get_CATEGORY().trim().toString().equals("LTIS1D") || mru.get_CATEGORY().trim().toString().equals("LTIS2D")) && user.get_ocr_agency().equalsIgnoreCase("CORAL")) {
                Ocr = "Y";
            }*/
            if (user.get_ocr_agency().equalsIgnoreCase("CORAL") || user.get_ocr_agency().equalsIgnoreCase("CRYSTAL") || user.get_ocr_agency().equalsIgnoreCase("SUJANIX")|| user.get_ocr_agency().equalsIgnoreCase("MEGA")|| user.get_ocr_agency().equalsIgnoreCase("EMDEE")) {
                Ocr = "Y";
            } else {
                Ocr = "N";
            }
            if (mru.get_CATEGORY().trim().toString().equals("LTIS1D") || mru.get_CATEGORY().trim().toString().equals("LTIS2D")) {
                Ocr = "Y";
                ocr_agency = "CORAL";
            }

            if (mru.get_CATEGORY().trim().equals("NDS1D") ||(mru.get_CATEGORY().trim().equals("NDS2D") && Double.parseDouble(mru.get_LOAD())>0.5)||mru.get_CATEGORY().trim().equals("LTIS1D") || mru.get_CATEGORY().trim().equals("LTIS2D") || mru.get_CATEGORY().trim().equals("PWWD") || mru.get_CATEGORY().trim().equals("LTEV")|| mru.get_CATEGORY().trim().equals("IAS2D")) {
                reqread.addAll(Arrays.asList(new String[]{"KVAH","KWH"}));
                read_stats = "KVAH";
                flag=true;
                //reqread.add("KWH");
            }/* else if (mru.get_CATEGORY().trim().equals("LTIS1D") ||mru.get_CATEGORY().trim().equals("NDS1D") ||mru.get_CATEGORY().trim().equals("NDS2D")) {
                reqread.addAll(Arrays.asList(new String[]{"KVAH","KWH"}));
            }*/ else {
                flag=false;
                read_stats = "KWH";
                //  reqReadings[0]=read_stats;
                reqread.add(read_stats);

            }
            if (stringRecDemd.trim().equals("I")) {
                maxdemand = "";
            } else {
                maxdemand = "Max_Demand";
                //  reqReadings[2]=maxdemand;
                reqread.add(maxdemand);
            }
            if (stringPowFact.trim().equals("I")) {
                powerfactor = "";
            } else {
                powerfactor = "Power_Factor";
                // reqReadings[1]=powerfactor;
                reqread.add(powerfactor);
            }
            paramarray = reqread.stream().toArray(String[]::new);
            bundle = new Bundle();

        }

        sppurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 == 0) {
                    cpurpose = "NA";
                } else {
                    // cpurpose = getResources().getStringArray(R.array.reason)[arg2].trim();
                    if (Category.equalsIgnoreCase("LTIS1D") || Category.equalsIgnoreCase("LTIS2D") || Category.equalsIgnoreCase("NDS1D") || Category.equalsIgnoreCase("NDS2D")) {
                        cpurpose = PurposeListBycat.get(arg2).trim();
                    } else {
                        cpurpose = "NA";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                cpurpose = "NA";
            }
        });


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
        tvAcNo = findViewById(R.id.tv_ac_no);
        tvConNo = findViewById(R.id.tv_con_no);
        tvMeterNo = findViewById(R.id.tv_meter_no);
        tvConName = findViewById(R.id.tv_con_name);
        tvAddress = findViewById(R.id.tv_address);
        tvCategory = findViewById(R.id.tv_category);
        tvLoad = findViewById(R.id.tv_load);

        etDTNo = findViewById(R.id.et_dtNo);
        etMeterNo = findViewById(R.id.et_meterNo);
        scrollView = findViewById(R.id.scrollview);
        btnContinue = findViewById(R.id.btn_continue);
        radioGroup = findViewById(R.id.radio_group_meter);
        radioBtnYes = findViewById(R.id.yes);
        radioBtnNo = findViewById(R.id.no);
        rggender = findViewById(R.id.radio_group_gender);
        rbmale = findViewById(R.id.rb_male);
        rbfemale = findViewById(R.id.rb_female);
        IsmeterCorrect = findViewById(R.id.ismetercorrect);
        spreason = findViewById(R.id.et_meterNo_billing);
        radioGroupbilling = findViewById(R.id.radio_group_billing);
        radioBtnYesbilling = findViewById(R.id.yes_billing);
        radioBtnNobilling = findViewById(R.id.no_billing);
        isbillinglayout = findViewById(R.id.lin_new_billing);
        dataList = findViewById(R.id.listConsumer);
        lin_listview = findViewById(R.id.lin_listview);
        lin_meterNo = findViewById(R.id.lin_new_meter);
        btnContinue.setOnClickListener(v -> {
            // TODO Auto-generated method stub
           /* if (cgender.equalsIgnoreCase("N") || cgender.equalsIgnoreCase("NA") || cgender.equalsIgnoreCase("null")) {
                Toast.makeText(getApplicationContext(), "Please Select Gender or purpose First", Toast.LENGTH_SHORT).show();
            }*/
//            Log.d("pre_read_date",Prv_read_Date);
            //String[] tokenReadDate =Prv_read_Date.split("-");
            //LocalDate date = LocalDate.of(2025,4,1);
           //LocalDate prevDateRead = LocalDate.of(Integer.parseInt(tokenReadDate[0]),Integer.parseInt(tokenReadDate[1]),Integer.parseInt(tokenReadDate[2].trim().substring(0,2)));
//            etMobileNo.setText(mru.get_CONTACT_NUM().trim());
            if (!test()) {
                //      Toast.makeText(getApplicationContext(), "Please Select Gender or purpose First", Toast.LENGTH_SHORT).show();
            }/*else if (prevDateRead.isBefore(date) && category.contains("NDS") && !isNDSdataFound){
                mStartForResult.launch(new Intent(this, KWHtoKVAH.class));
            }*/
            else {
                if (isbilling) {
                    if (category.equalsIgnoreCase("KJ")) {
                        if (Unmeter.equals("UM")) {
                            long a = localDBHelper.saveMeterNo("N", "", tvAcNo.getText().toString());
                            if (a > 0) {
                                if (!(etMobileNo.getText().toString().equals(stringMobileNo)) || !(etDTNo.getText().toString().equals(stringDtNo))) {
                                    // This line will call due to some changes in editbox
                                    if (!Utiilties.isOnline(ConsumerActivity4A11new.this)) {
                                        long c = localDBHelper.updateMru(etMobileNo.getText().toString(), etDTNo.getText().toString(), tvAcNo.getText().toString(), "Y", cgender, cpurpose);
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
                        }
                        else if (Unmeter.equals("MS")) {
                            if (isMeterNoCorrect) {
                                long a = localDBHelper.saveMeterNo("N", "", tvAcNo.getText().toString());
                                if (a > 0) {

                                    if (!(etMobileNo.getText().toString().equals(stringMobileNo))
                                            || !(etDTNo.getText().toString().equals(stringDtNo))) {
                                        // This line will call due to some changes in editbox
                                        if (!Utiilties.isOnline(ConsumerActivity4A11new.this)) {
                                            // Offline code
                                            long c = localDBHelper.updateMru(etMobileNo.getText()
                                                            .toString(), etDTNo.getText().toString(),
                                                    tvAcNo.getText().toString(), "Y", cgender, cpurpose);
                                            if (c > 0) {
                                                localDBHelper.insertmobiledtnumber(etMobileNo.getText()
                                                                .toString(), etDTNo.getText().toString(),
                                                        tvAcNo.getText().toString());
                                                Toast.makeText(getApplicationContext(),
                                                        "Update in Local DataBase : "+ocr_agency,
                                                        Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(),
                                                        "Error in Local Database",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                            if (DATA_Engines.equalsIgnoreCase("Y")) {
                                                if (Ocr.equalsIgnoreCase("Y")) {
                                                    decideAgencyForOCR();
                                                } else {
                                                    callCameraIntent();
                                                }
                                            } else {
                                                Toast.makeText(this, "Install company's application first", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            // Online Code
                                            new updateDtNo().execute(etMobileNo.getText()
                                                            .toString(), etDTNo.getText().toString(),
                                                    tvAcNo.getText().toString());
                                        }
                                    } else {

                                        if (Ocr.equalsIgnoreCase("Y")) {
                                            decideAgencyForOCR();
                                        } else {
                                            callCameraIntent();
                                        }
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error in Inserting Meter Number", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                if (!etMeterNo.getText().toString().trim().isEmpty()) {

                                    long a = localDBHelper.saveMeterNo("Y", etMeterNo.getText().toString().trim(), tvAcNo.getText().toString());
                                    if (a > 0) {

                                        if (!(etMobileNo.getText().toString().equals(stringMobileNo))
                                                || !(etDTNo.getText().toString().equals(stringDtNo))) {
                                            // This line will call due to some changes in editbox
                                            if (!Utiilties.isOnline(ConsumerActivity4A11new.this)) {
                                                // Offline code
                                                long c = localDBHelper.updateMru(etMobileNo.getText()
                                                                .toString(), etDTNo.getText().toString(),
                                                        tvAcNo.getText().toString(), "Y", cgender, cpurpose);
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
                                                if (Ocr.equalsIgnoreCase("Y")) {
                                                    decideAgencyForOCR();
                                                } else {
                                                    callCameraIntent();
                                                }
                                            } else {
                                                // Online Code
                                                new updateDtNo().execute(etMobileNo.getText().toString(), etDTNo.getText().toString(), tvAcNo.getText().toString());
                                            }
                                        } else {

                                            if (Ocr.equalsIgnoreCase("Y")) {
                                                decideAgencyForOCR();
                                            } else {
                                                callCameraIntent();
                                            }
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
                        Toast.makeText(ConsumerActivity4A11new.this, "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                    } else if ((localDBHelper.getmobileno(etMobileNo.getText().toString()) > 3 && isEditable)) {
                        Toast.makeText(ConsumerActivity4A11new.this, "You Have Entered This Number More Than 3 Times", Toast.LENGTH_SHORT).show();
                    } else {
                        if (Unmeter.equals("UM")) {
                            long a = localDBHelper.saveMeterNo("N", "", tvAcNo.getText().toString());
                            if (a > 0) {

                                if (!(etMobileNo.getText().toString().equals(stringMobileNo)) || !(etDTNo.getText().toString().equals(stringDtNo))) {
                                    // This line will call due to some changes in editbox
                                    if (!Utiilties.isOnline(ConsumerActivity4A11new.this)) {
                                        // Offline code
                                        long c = localDBHelper.updateMru(etMobileNo.getText()
                                                        .toString(), etDTNo.getText().toString(),
                                                tvAcNo.getText().toString(), "Y", cgender, cpurpose);
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
                                        if (!Utiilties.isOnline(ConsumerActivity4A11new.this)) {
                                            // Offline code
                                            long c = localDBHelper.updateMru(etMobileNo.getText()
                                                            .toString(), etDTNo.getText().toString(),
                                                    tvAcNo.getText().toString(), "Y", cgender, cpurpose);
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
                                            if (Ocr.equalsIgnoreCase("Y")) {
                                                decideAgencyForOCR();
                                            } else {
                                                callCameraIntent();
                                            }
                                        } else {
                                            // Online Code
                                            new updateDtNo().execute(etMobileNo.getText()
                                                            .toString(), etDTNo.getText().toString(),
                                                    tvAcNo.getText().toString());
                                        }
                                    } else {
                                        // tarrif change
                                        //Ocr="N";
                                        if (Ocr.equalsIgnoreCase("Y")) {
                                            decideAgencyForOCR();
                                        } else {
                                            callCameraIntent();
                                        }
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
                                            if (!Utiilties.isOnline(ConsumerActivity4A11new.this)) {
                                                // Offline code
                                                long c = localDBHelper.updateMru(etMobileNo.getText()
                                                                .toString(), etDTNo.getText().toString(),
                                                        tvAcNo.getText().toString(), "Y", cgender, cpurpose);
                                                if (c > 0) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Update in Local DataBase",
                                                            Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Error in Local Database",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                                if (Ocr.equalsIgnoreCase("Y")) {
                                                    decideAgencyForOCR();
                                                } else {
                                                    callCameraIntent();
                                                }
                                            } else {
                                                // Online Code
                                                new updateDtNo().execute(etMobileNo.getText().toString(), etDTNo.getText().toString(), tvAcNo.getText().toString());
                                            }
                                        } else {

                                            if (Ocr.equalsIgnoreCase("Y")) {
                                                decideAgencyForOCR();
                                            } else {
                                                callCameraIntent();
                                            }
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
                        Toast.makeText(ConsumerActivity4A11new.this, "Please select Reason", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!Utiilties.isOnline(ConsumerActivity4A11new.this)) {
                            Long i = localDBHelper.saveBillreason(reason, tvAcNo.getText().toString());
                            if (i >= 0) {
                                finish();
                            } else {
                                Toast.makeText(ConsumerActivity4A11new.this, "Error in local database", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            new updatebillingreason().execute(tvConNo.getText().toString(), reason);
                        }
                    }
                }
            }
        });

        // ListView Item Click Listener
        dataList.setOnItemClickListener((parent, view, position, id) -> {
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
                    Category = mru.get_CATEGORY();
                    tvLoad.setText(mru.get_LOAD());
                    etMobileNo.setText(mru.get_CONTACT_NUM().trim());
                    etDTNo.setText(mru.get_DT_NO().trim());
                    stringMobileNo = mru.get_CONTACT_NUM().trim();
                    stringDtNo = mru.get_DT_NO().trim();
                    IMAGE_NAME = mru.get_CON_ID() + ".jpg";
                }
            }

        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
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
        });
        radioGroupbilling.setOnCheckedChangeListener((group, checkedId) -> {
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
        });

        rggender.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == rbmale.getId()) {
                cgender = "M";
            } else if (checkedId == rbfemale.getId()) {
                cgender = "F";
            } else {
                cgender = "N";
            }
        });
    }


    void createDialogForData(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConsumerActivity4A11new.this);
        // Set the message show for the Alert time
        builder.setMessage(msg);
        // Set Alert Title
        builder.setTitle("Alert !");
        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);
        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            // When the user click yes button then app will close
            dialog.cancel();
        });
        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
//        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
//            // If user click no then dialog box is canceled.
//            dialog.cancel();
//        });
        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("" + resultCode, "-----------------------Top of result--------------------------------");
        if (requestCode == 5001 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("METER_READING_STATUS")) {
                 String latitude = data.getStringExtra("LAT");
                String longitude = data.getStringExtra("LON");
                String consumerNo = data.getStringExtra("CONSUMER_NO");
                // String resVal = data.getStringExtra("KWH_READING_VALUE");
                bundle.putString("readingstats", data.getStringExtra("METER_READING_STATUS"));
                String reading="";
                if (read_stats != null) {
                    if (flag) {
                        kvah = data.getStringExtra("KVAH_READING_VALUE");
                        kwh = data.getStringExtra("KWH_READING_VALUE");
                        reading = kvah;
                    } else {
                        kwh = data.getStringExtra("KWH_READING_VALUE");
                        reading = kwh;
                    }
                }
                bundle.putString("reading", reading);
                Log.d("kvah",""+kvah);
                Log.d("kwh",""+kwh);
                bundle.putString("abnormility", data.getStringExtra("ABNORMALITY"));
                String[] returnparam = data.getStringArrayExtra("REQ_READING_VALUES");
                String resValmaxdemand = "0", resValpowerfactor = "0";
                //Power_Factor_READING_VALUE=0.88, KWH_READING_VALUE=9535, Max_Demand_READING_VALUE=00.61,
                if (Arrays.asList(returnparam).contains("Max_Demand")) {
                    resValmaxdemand = data.getStringExtra("Max_Demand_READING_VALUE");
                    //  intentdata.add(3,resValmaxdemand);
                }
                if (Arrays.asList(returnparam).contains("Power_Factor")) {
                    resValpowerfactor = data.getStringExtra("Power_Factor_READING_VALUE");
                    //  intentdata.add(4,resValpowerfactor);
                }
                bundle.putString("maxdemand", resValmaxdemand);
                bundle.putString("PF", resValpowerfactor);
                bundle.putString("ACCOUNT_NO", tvAcNo.getText()
                        .toString().trim());
                //  final String date = Utiilties.getDate(gps.getTime(), "dd/MM/yyyy");
                if (tvConNo.getText().toString().equalsIgnoreCase(consumerNo)) {
                    ClipData clipData = data.getClipData();
                    if (clipData != null && clipData.getItemCount() > 0) {
                        Uri smallImgUri = null, bigImgUri = null;
                        ClipData.Item clipItem;
                        Log.d("result", String.valueOf(clipData.getItemCount()));
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            clipItem = clipData.getItemAt(i);
                            if (clipItem.getText() != null) {
                                if ("SMALL_IMAGE".contentEquals(clipItem.getText())) {
                                    smallImgUri = clipItem.getUri();
                                } else if ("BIG_IMAGE".contentEquals(clipItem.getText())) {
                                    bigImgUri = clipItem.getUri();
                                }
                            }
                        }
                        //   Bitmap bitmap = saveFile(smallImgUri == null ? bigImgUri : smallImgUri);
                        Bitmap bitmap = null;
                        Uri uri = null;
                        try {
                            UserDetails user = CommonPref.getUserDetails(getApplicationContext());
                            bitmap = Images.Media.getBitmap(this.getContentResolver(), smallImgUri == null ? bigImgUri : smallImgUri);
                            uri = imageutils.saveImage(bitmap, this, user.get_MRUNo(), tvConNo.getText().toString().trim(), CommonPref.getmmyyyy(getApplicationContext()));
                           /* String uri1=imageutils.copyFileFromUri(ConsumerActivity4A11new.this,smallImgUri == null ? bigImgUri : smallImgUri,tvConNo.getText().toString().trim(),CommonPref.getUserDetails(getApplicationContext())
                                    .get_MRUNo());*/
                         /*   deleteuri(smallImgUri);
                            deleteuri(bigImgUri);*/
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (uri != null) {
                            // date = Utiilties.getDate(gps.getTime(), "dd/MM/yyyy");
                            long c = localDBHelper.savePhoto(String.valueOf(latitude),
                                    String.valueOf(longitude), uri.toString()
                                    , tvConNo.getText().toString()
                                            .trim(), "", cgender,cpurpose);
                            //date="";
                            if (c > 0) {
                                if (isadddressupdated.equalsIgnoreCase("Y")) {
                                    Intent intent = new Intent(getBaseContext(),
                                            MeterreadingstatusactivityOcr.class);
                                    //  intent.putExtra("FLAG", "3");
                                    bundle.putString("FLAG", "3");
                                    bundle.putString("ocr_agency", ocr_agency);
                                    bundle.putString("kvah",kvah);
                                    bundle.putString("kwh",kwh);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                    finish();
                                } else if (Integer.parseInt(CommonPref.getUserDetails(ConsumerActivity4A11new.this).get_distcode()) == 236) {
                                    Intent intent = new Intent(getBaseContext(),
                                            MeterreadingstatusactivityOcr.class);
                                    bundle.putString("FLAG", "3");
                                    bundle.putString("ocr_agency", ocr_agency);
                                    bundle.putString("kvah",kvah);
                                    bundle.putString("kwh",kwh);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                    finish();
                                } else {
                                    Intent intent = new Intent(getBaseContext(),
                                            MeterreadingstatusactivityOcr.class);
                                    bundle.putString("FLAG", "3");
                                    bundle.putString("ocr_agency", ocr_agency);
                                    bundle.putString("kvah",kvah);
                                    bundle.putString("kwh",kwh);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                    finish();
                                }
                            } else {
                                Toast.makeText(ConsumerActivity4A11new.this,
                                                "Error in Local Database", Toast.LENGTH_LONG)
                                        .show();
                            }
                        } else {
                            long c = localDBHelper.savePhoto(latitude, longitude, uri.toString(), tvConNo.getText().toString()
                                    .trim(), "", cgender,cpurpose);
                            //date="";
                            if (c > 0) {
                                if (isadddressupdated.equalsIgnoreCase("Y")) {
                                    Intent intent = new Intent(getBaseContext(), MeterreadingstatusactivityOcr.class);
                                    bundle.putString("FLAG", "3");
                                    bundle.putString("ocr_agency", ocr_agency);
                                    bundle.putString("kvah",kvah);
                                    bundle.putString("kwh",kwh);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                    finish();
                                } else {
                                    Intent intent = new Intent(getBaseContext(), User_FirstHomeActivity.class);
                                    bundle.putString("FLAG", "3");
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                    finish();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                                "Error in Local Database", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    }
                } else {
                    Toast.makeText(ConsumerActivity4A11new.this, "Consumer No Invalid", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ConsumerActivity4A11new.this, "Invalid Returns", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 5002 && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(ConsumerActivity4A11new.this, "Data Not Found from CORAL !", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == 5002 && resultCode >= 0) {
               Log.e("crystal data", data.getClipData() + "");
               if (data != null && data.hasExtra("METER_READING_STATUS")) {
                   String consumerNo = data.getStringExtra("CONSUMER_NO");
                 if (tvConNo.getText().toString().equalsIgnoreCase(consumerNo)) {
                    String latitude = data.getStringExtra("LAT");
                    String longitude = data.getStringExtra("LONG");
                    // String status = data.getStringExtra("SCAN_TYPE");
                    bundle.putString("readingstats", data.getStringExtra("METER_READING_STATUS"));
                    String reading = null;
                   if (read_stats != null) {
                       if (flag) {
                           kvah = data.getStringExtra("KVAH_READING_VALUE");
                           kwh = data.getStringExtra("KWH_READING_VALUE");
                           reading = kvah;
                       } else {
                           kwh = data.getStringExtra("KWH_READING_VALUE");
                           reading = kwh;
                       }
                   }
                     Log.d("kvah",""+kvah);
                     Log.d("kwh",""+kwh);
                    bundle.putString("reading", reading);
                    bundle.putString("abnormility", data.getStringExtra("ABNORMALITY"));
                    ArrayList reqArray = data.getStringArrayListExtra("REQ_READING_VALUES");
                    String maxDemand = null;
                    String pf = null;
                    if (reqArray.contains("KW")) {
                        maxDemand = data.getStringExtra("Max_Demand_READING_VALUE");
                    }else{
                        maxDemand = data.getStringExtra("Max_Demand_READING_VALUE");
                    }
                    if (reqArray.contains("PF")) {
                        pf = data.getStringExtra("Power_Factor_READING_VALUE");
                    }
                    bundle.putString("maxdemand", maxDemand);
                    bundle.putString("PF", pf);
                    bundle.putString("ACCOUNT_NO", tvAcNo.getText()
                            .toString().trim());
                    Log.e("crystal data", consumerNo + "," + latitude + "," + longitude + "," + read_stats + "," + reading + "," + maxDemand + "," + pf);
                    ClipData clipData = data.getClipData();
                    if (clipData != null && clipData.getItemCount() > 0) {
                        Uri smallImgUri = null, bigImgUri = null;
                        ClipData.Item clipItem;
                        // Log.d("result", String.valueOf(clipData.getItemCount()));
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            clipItem = clipData.getItemAt(i);
                            Log.e("image uri", clipData.getItemAt(i) + "");
                            smallImgUri = clipItem.getUri();
                        }
                        Bitmap bitmap = null;
                        Uri uri = null;
                        try {
                            //    Log.e("clipdata uri out side",""+smallImgUri);
                            bitmap = Images.Media.getBitmap(this.getContentResolver(), smallImgUri);
                            uri = imageutils.saveImage(bitmap, this, CommonPref.getUserDetails(getApplicationContext())
                                    .get_MRUNo(), tvConNo.getText().toString().trim(), CommonPref.getmmyyyy(getApplicationContext()));
                            /*  deleteuri(smallImgUri);*/
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (uri != null) {
                            // date = Utiilties.getDate(gps.getTime(), "dd/MM/yyyy");
                            long c = localDBHelper.savePhoto(String.valueOf(latitude),
                                    String.valueOf(longitude), uri.toString()
                                    , tvConNo.getText().toString()
                                            .trim(), "", cgender,cpurpose);
                            //date="";
                            if (c > 0) {
                                if (isadddressupdated.equalsIgnoreCase("Y")) {
                                    Intent intent = new Intent(getBaseContext(),
                                            MeterreadingstatusactivityOcr.class);
                                    //  intent.putExtra("FLAG", "3");
                                    bundle.putString("FLAG", "3");
                                    bundle.putString("ocr_agency", ocr_agency);
                                    bundle.putString("kvah",kvah);
                                    bundle.putString("kwh",kwh);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                    finish();
                                } else if (Integer.parseInt(CommonPref.getUserDetails(ConsumerActivity4A11new.this).get_distcode()) == 236) {
                                    Intent intent = new Intent(getBaseContext(),
                                            MeterreadingstatusactivityOcr.class);
                                    bundle.putString("FLAG", "3");
                                    bundle.putString("ocr_agency", ocr_agency);
                                    bundle.putString("kvah",kvah);
                                    bundle.putString("kwh",kwh);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                    finish();
                                } else {
                                    Intent intent = new Intent(getBaseContext(),
                                            MeterreadingstatusactivityOcr.class);
                                    bundle.putString("FLAG", "3");
                                    bundle.putString("ocr_agency", ocr_agency);
                                    bundle.putString("kvah",kvah);
                                    bundle.putString("kwh",kwh);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                    finish();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Error in Local Database", Toast.LENGTH_LONG)
                                        .show();
                            }
                        } else {
                            long c = localDBHelper.savePhoto(latitude, longitude, uri.toString(), tvConNo.getText().toString()
                                    .trim(), "", cgender,cpurpose);
                            //date="";
                            if (c > 0) {
                                if (isadddressupdated.equalsIgnoreCase("Y")) {
                                    Intent intent = new Intent(getBaseContext(), MeterreadingstatusactivityOcr.class);
                                    bundle.putString("FLAG", "3");
                                    bundle.putString("ocr_agency", ocr_agency);
                                    bundle.putString("kvah",kvah);
                                    bundle.putString("kwh",kwh);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                    finish();
                                } else {
                                    Intent intent = new Intent(getBaseContext(), User_FirstHomeActivity.class);
                                    bundle.putString("FLAG", "3");
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                    finish();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                                "Error in Local Database", Toast.LENGTH_LONG)
                                        .show();
                            }

                        }
                    } else {
                        Toast.makeText(ConsumerActivity4A11new.this, "image returned Invalid", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ConsumerActivity4A11new.this, "Consumer No Invalid", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ConsumerActivity4A11new.this, "Invalid Returns", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 5002 && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(ConsumerActivity4A11new.this, "Data Not Found from crystal !", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == 5003 && resultCode == RESULT_OK) {
            //Toast.makeText(ConsumerActivity4A11new.this, "value from scanner app" + resultCode + "," + data, Toast.LENGTH_SHORT).show();
            if (data != null && data.hasExtra("METER_READING_STATUS")) {
                // Data in extras
                //  Bundle[{KWH_READING_DATA_TYPE=SCAN, KWH_TIMESTAMP=1603098477, KWH_READING_VALUE=09755, Max_Demand_READING_VALUE=00.00, METER_READING_STATUS=OK, LAT=25.6101481, LON=85.1280389, READINGS_MAP={"Max_Demand":{"scan_value":"00.00","timestamp":"1603098488"},"KWH":{"scan_value":"09755","timestamp":"1603098477"}}, CONSUMER_NO=21410032848, Max_Demand_TIMESTAMP=1603098488, REQ_READING_VALUES=[KWH, Max_Demand], START_TIMESTAMP=1603098451, Max_Demand_READING_DATA_TYPE=SCAN, ABNORMALITY=Everything is Ok}]
                String latitude = data.getStringExtra("LAT");
                String longitude = data.getStringExtra("LON");
                String consumerNo = data.getStringExtra("CONSUMER_NO");
                // String resVal = data.getStringExtra("KWH_READING_VALUE");
                bundle.putString("readingstats", data.getStringExtra("METER_READING_STATUS"));
                Toast.makeText(this, "" + data.getStringExtra("METER_READING_STATUS"), Toast.LENGTH_SHORT).show();
                String reading="";
                if (flag) {
                    kvah=data.getStringExtra("KVAH_READING_VALUE");
                    kwh = data.getStringExtra("KWH_READING_VALUE");
                    reading=kvah;
                }else {
                    kwh = data.getStringExtra("KWH_READING_VALUE");
                    reading=kwh;
                }
                Log.d("kvah",""+kvah);
                Log.d("kwh",""+kwh);
                bundle.putString("reading", reading);
                //if(data.hasExtra("KWH_READING_VALUE"))kwh=data.getStringExtra("KWH_READING_VALUE");
                bundle.putString("abnormility", data.getStringExtra("ABNORMALITY"));
                ArrayList<String> returnparam = data.getStringArrayListExtra("REQ_READING_VALUES");
                String resValmaxdemand = "0", resValpowerfactor = "0";
                //Power_Factor_READING_VALUE=0.88, KWH_READING_VALUE=9535, Max_Demand_READING_VALUE=00.61,
                if (returnparam.contains("Max_Demand")) {
                    resValmaxdemand = data.getStringExtra("Max_Demand_READING_VALUE");//0.0
                }
                if (returnparam.contains("Power_Factor")) {
                    resValpowerfactor = data.getStringExtra("Power_Factor_READING_VALUE");
                }
                bundle.putString("maxdemand", resValmaxdemand);
                bundle.putString("PF", resValpowerfactor);
                bundle.putString("ACCOUNT_NO", tvAcNo.getText()
                        .toString().trim());
                //  final String date = Utiilties.getDate(gps.getTime(), "dd/MM/yyyy");
                if (tvConNo.getText().toString().equalsIgnoreCase(consumerNo)) {
                    ClipData clipData = data.getClipData();
                    if (clipData != null && clipData.getItemCount() > 0) {
                        Uri smallImgUri = null, bigImgUri = null;
                        ClipData.Item clipItem;
                        clipItem = clipData.getItemAt(0);
                        smallImgUri = clipItem.getUri();
                        Bitmap bitmap = null;
                        Uri uri = null;
                        try {
                            UserDetails user = CommonPref.getUserDetails(getApplicationContext());
                            bitmap = Images.Media.getBitmap(this.getContentResolver(), smallImgUri);
                            uri = imageutils.saveImage(bitmap, this, user.get_MRUNo(), tvConNo.getText().toString().trim(), CommonPref.getmmyyyy(getApplicationContext()));
                           /* String uri1=imageutils.copyFileFromUri(ConsumerActivity4A11new.this,smallImgUri == null ? bigImgUri : smallImgUri,tvConNo.getText().toString().trim(),CommonPref.getUserDetails(getApplicationContext())
                                    .get_MRUNo());*/
                         /*   deleteuri(smallImgUri);
                            deleteuri(bigImgUri);*/
                            Toast.makeText(this, "" + uri.toString(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (uri != null) {
                            // date = Utiilties.getDate(gps.getTime(), "dd/MM/yyyy");
                            long c = localDBHelper.savePhoto(String.valueOf(latitude),
                                    String.valueOf(longitude), uri.toString()
                                    , tvConNo.getText().toString()
                                            .trim(), "", cgender,cpurpose);
                            //date="";
                            if (c > 0) {
                                if (isadddressupdated.equalsIgnoreCase("Y")) {
                                    Intent intent = new Intent(getBaseContext(),
                                            MeterreadingstatusactivityOcr.class);
                                    bundle.putString("FLAG", "3");
                                    bundle.putString("ocr_agency", ocr_agency);
                                    bundle.putString("kvah",kvah);
                                    bundle.putString("kwh",kwh);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                    finish();
                                } else if (Integer.parseInt(CommonPref.getUserDetails(ConsumerActivity4A11new.this).get_distcode()) == 236) {
                                    Intent intent = new Intent(getBaseContext(),
                                            MeterreadingstatusactivityOcr.class);
                                    bundle.putString("FLAG", "3");
                                    bundle.putString("ocr_agency", ocr_agency);
                                    bundle.putString("kvah",kvah);
                                    bundle.putString("kwh",kwh);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                    finish();
                                } else {
                                    Intent intent = new Intent(getBaseContext(),
                                            MeterreadingstatusactivityOcr.class);
                                    bundle.putString("FLAG", "3");
                                    bundle.putString("ocr_agency", ocr_agency);
                                    bundle.putString("kvah",kvah);
                                    bundle.putString("kwh",kwh);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                    finish();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                                "Error in Local Database", Toast.LENGTH_LONG)
                                        .show();
                            }
                        } else {
                            long c = localDBHelper.savePhoto(latitude, longitude, uri.toString(), tvConNo.getText().toString()
                                    .trim(), "", cgender,cpurpose);
                            //date="";
                            if (c > 0) {
                                if (isadddressupdated.equalsIgnoreCase("Y")) {
                                    Intent intent = new Intent(getBaseContext(), MeterreadingstatusactivityOcr.class);
                                    bundle.putString("FLAG", "3");
                                    bundle.putString("ocr_agency", ocr_agency);
                                    bundle.putString("kvah",kvah);
                                    bundle.putString("kwh",kwh);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                    finish();
                                } else {
                                    Intent intent = new Intent(getBaseContext(), User_FirstHomeActivity.class);
                                    bundle.putString("FLAG", "3");
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                    finish();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                                "Error in Local Database", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    }
                } else {
                    Toast.makeText(ConsumerActivity4A11new.this, "Consumer No Invalid", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ConsumerActivity4A11new.this, "Invalid Returns", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 5003 && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(ConsumerActivity4A11new.this, "Data Not Found from Sujanix", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == 5004 && resultCode == Activity.RESULT_OK) {
            Log.e("5004", "-----------------------result--------------------------------");
            if (data != null) {
                HashMap<String, Object> hashMap = (HashMap<String, Object>) data.getSerializableExtra("result_key");
                Log.e("data", hashMap.toString());
                String latitude = (String) hashMap.get("LAT");
                String longitude = (String) hashMap.get("LON");
                String consumerNo = (String) hashMap.get("CONSUMER_NO");
                // String resVal = hashMap.get("KWH_READING_VALUE");
                bundle.putString("readingstats", (String) hashMap.get("METER_READING_STATUS"));
                /*if (read_stats.equalsIgnoreCase("KVAH")) {
                    bundle.putString("reading", (String) hashMap.get("KVAH_READING_VALUE"));
                } else {
                    bundle.putString("reading", (String) hashMap.get("KWH_READING_VALUE"));
                }*/
                String reading="";
                if (flag) {
                    kvah=data.getStringExtra("KVAH_READING_VALUE");
                    kwh = data.getStringExtra("KWH_READING_VALUE");
                    reading=kvah;
                }else {
                    kwh = data.getStringExtra("KWH_READING_VALUE");
                    reading=kwh;
                }
                Log.d("kvah",""+kvah);
                Log.d("kwh",""+kwh);
                bundle.putString("reading", reading);
                bundle.putString("abnormility", (String) hashMap.get("ABNORMALITY"));
                List<String> returnparam = (List<String>) hashMap.get("REQ_READING_VALUES");
                String resValmaxdemand = "0", resValpowerfactor = "0";
                if (returnparam.contains("Max_Demand")) {
                    resValmaxdemand = (String) hashMap.get("Max_Demand_READING_VALUE");
                }
                if (returnparam.contains("Power_Factor")) {
                    resValpowerfactor = (String) hashMap.get("Power_Factor_READING_VALUE");
                }
                bundle.putString("maxdemand", resValmaxdemand);
                bundle.putString("PF", resValpowerfactor);
                bundle.putString("ACCOUNT_NO", tvAcNo.getText()
                        .toString().trim());
                if (tvConNo.getText().toString().equalsIgnoreCase(consumerNo)) {
                    Bitmap bitmap = null;
                    Uri uri = null;
                    try {
                        UserDetails user = CommonPref.getUserDetails(getApplicationContext());
                        Log.d("log",(String) hashMap.get("smallImage"));
                        bitmap = BitmapFactory.decodeFile((String) hashMap.get("smallImage"));
                        uri = imageutils.saveImage(bitmap, this, user.get_MRUNo(), tvConNo.getText().toString().trim(), CommonPref.getmmyyyy(getApplicationContext()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (uri != null) {
                        // date = Utiilties.getDate(gps.getTime(), "dd/MM/yyyy");
                        long c = localDBHelper.savePhoto(String.valueOf(latitude),
                                String.valueOf(longitude), uri.toString()
                                , tvConNo.getText().toString()
                                        .trim(), "", cgender, cpurpose);
                        //date="";
                        if (c > 0) {
                            if (isadddressupdated.equalsIgnoreCase("Y")) {
                                Intent intent = new Intent(getBaseContext(),
                                        MeterreadingstatusactivityOcr.class);
                                //  intent.putExtra("FLAG", "3");
                                bundle.putString("FLAG", "3");
                                bundle.putString("ocr_agency", ocr_agency);
                                bundle.putString("kvah",kvah);
                                bundle.putString("kwh",kwh);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                finish();
                            } else if (Integer.parseInt(CommonPref.getUserDetails(ConsumerActivity4A11new.this).get_distcode()) == 236) {
                                Intent intent = new Intent(getBaseContext(),
                                        MeterreadingstatusactivityOcr.class);
                                bundle.putString("FLAG", "3");
                                bundle.putString("ocr_agency", ocr_agency);
                                bundle.putString("kvah",kvah);
                                bundle.putString("kwh",kwh);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                finish();
                            } else {
                                Intent intent = new Intent(getBaseContext(),
                                        MeterreadingstatusactivityOcr.class);
                                bundle.putString("FLAG", "3");
                                bundle.putString("ocr_agency", ocr_agency);
                                bundle.putString("kvah",kvah);
                                bundle.putString("kwh",kwh);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                finish();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                            "Error in Local Database", Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        long c = localDBHelper.savePhoto(latitude, longitude, uri.toString(), tvConNo.getText().toString()
                                .trim(), "", cgender, cpurpose);
                        //date="";
                        if (c > 0) {
                            if (isadddressupdated.equalsIgnoreCase("Y")) {
                                Intent intent = new Intent(getBaseContext(), MeterreadingstatusactivityOcr.class);
                                bundle.putString("FLAG", "3");
                                bundle.putString("ocr_agency", ocr_agency);
                                bundle.putString("kvah",kvah);
                                bundle.putString("kwh",kwh);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                finish();
                            } else {
                                Intent intent = new Intent(getBaseContext(), User_FirstHomeActivity.class);
                                bundle.putString("FLAG", "3");
                                intent.putExtras(bundle);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                finish();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                            "Error in Local Database", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                    //}
                } else {
                    Toast.makeText(ConsumerActivity4A11new.this, "Consumer No Invalid", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ConsumerActivity4A11new.this, "Null Data Found", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 5005 && resultCode == Activity.RESULT_OK) {
            //result code for md
            Log.e("5005", "-----------------------result value for Company EEMDE--------------------------------");
            if (data != null) {
                Bundle bandle=data.getExtras();
                Log.e("data", (bandle!=null)?"data found : "+bandle:"Null Bundle");
                String latitude = (String) bandle.getString("LAT");
                String longitude = bandle.getString("LON");
                String consumerNo = bandle.getString("CONSUMER_NO");
                // String resVal = hashMap.get("KWH_READING_VALUE");
                bundle.putString("readingstats", bandle.getString("METER_READING_STATUS"));
               /* if (read_stats.equalsIgnoreCase("KVAH")) {
                    bundle.putString("reading", bandle.getString("KVAH_READING_VALUE"));
                } else {
                    bundle.putString("reading", bandle.getString("KWH_READING_VALUE"));
                }*/
                String reading="";
                if (flag) {
                    kvah=data.getStringExtra("KVAH_READING_VALUE");
                    kwh = data.getStringExtra("KWH_READING_VALUE");
                    reading=kvah;
                }else {
                    kwh = data.getStringExtra("KWH_READING_VALUE");
                    reading=kwh;
                }
                Log.d("kvah",""+kvah);
                Log.d("kwh",""+kwh);
                bundle.putString("reading", reading);
                bundle.putString("abnormility", bandle.getString("ABNORMALITY"));
                List<String> returnparam =  data.getExtras().getStringArrayList("REQ_READING_VALUES");
                String resValmaxdemand = "0", resValpowerfactor = "0";
                if (returnparam.contains("Max_Demand")) {
                    resValmaxdemand = bandle.getString("Max_Demand_READING_VALUE");
                }
                if (returnparam.contains("Power_Factor")) {
                    resValpowerfactor = bandle.getString("Power_Factor_READING_VALUE");
                }
                bundle.putString("maxdemand", resValmaxdemand);
                bundle.putString("PF", resValpowerfactor);
                bundle.putString("ACCOUNT_NO", tvAcNo.getText()
                        .toString().trim());
                if (tvConNo.getText().toString().equalsIgnoreCase(consumerNo)) {
                    Bitmap bitmap = null;
                    Uri uri = null;
                    try {
                        UserDetails user = CommonPref.getUserDetails(getApplicationContext());
                        Log.d("log",bandle.getString("READING_IMAGE"));
                        bitmap = BitmapFactory.decodeFile(bandle.getString("READING_IMAGE"));
                        //uri = imageutils.saveImage(bitmap, this, user.get_MRUNo(), tvConNo.getText().toString().trim(), CommonPref.getmmyyyy(getApplicationContext()));
                        uri = imageutils.saveImage(Bitmap.createScaledBitmap(bitmap, 400, 300, true), this, user.get_MRUNo().trim(), tvConNo.getText().toString().trim(), CommonPref.getmmyyyy(getApplicationContext()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (uri != null) {
                        // date = Utiilties.getDate(gps.getTime(), "dd/MM/yyyy");
                        long c = localDBHelper.savePhoto(String.valueOf(latitude),
                                String.valueOf(longitude), uri.toString()
                                , tvConNo.getText().toString()
                                        .trim(), "", cgender, cpurpose);
                        //date="";
                        if (c > 0) {
                            if (isadddressupdated.equalsIgnoreCase("Y")) {
                                Intent intent = new Intent(getBaseContext(),
                                        MeterreadingstatusactivityOcr.class);
                                //  intent.putExtra("FLAG", "3");
                                bundle.putString("FLAG", "3");
                                bundle.putString("ocr_agency", ocr_agency);
                                bundle.putString("kvah",kvah);
                                bundle.putString("kwh",kwh);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                finish();
                            } else if (Integer.parseInt(CommonPref.getUserDetails(ConsumerActivity4A11new.this).get_distcode()) == 236) {
                                Intent intent = new Intent(getBaseContext(),
                                        MeterreadingstatusactivityOcr.class);
                                bundle.putString("FLAG", "3");
                                bundle.putString("ocr_agency", ocr_agency);
                                bundle.putString("kvah",kvah);
                                bundle.putString("kwh",kwh);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                finish();
                            } else {
                                Intent intent = new Intent(getBaseContext(),
                                        MeterreadingstatusactivityOcr.class);
                                bundle.putString("FLAG", "3");
                                bundle.putString("ocr_agency", ocr_agency);
                                bundle.putString("kvah",kvah);
                                bundle.putString("kwh",kwh);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                finish();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                            "Error in Local Database", Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        long c = localDBHelper.savePhoto(latitude, longitude, uri.toString(), tvConNo.getText().toString()
                                .trim(), "", cgender, cpurpose);
                        //date="";
                        if (c > 0) {
                            if (isadddressupdated.equalsIgnoreCase("Y")) {
                                Intent intent = new Intent(getBaseContext(), MeterreadingstatusactivityOcr.class);
                                bundle.putString("FLAG", "3");
                                bundle.putString("ocr_agency", ocr_agency);
                                bundle.putString("kvah",kvah);
                                bundle.putString("kwh",kwh);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                finish();
                            } else {
                                Intent intent = new Intent(getBaseContext(), User_FirstHomeActivity.class);
                                bundle.putString("FLAG", "3");
                                intent.putExtras(bundle);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                finish();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                            "Error in Local Database", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                    //}
                } else {
                    Toast.makeText(ConsumerActivity4A11new.this, "Consumer No Invalid", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ConsumerActivity4A11new.this, "Null Data Found", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 1) {
            Log.e("in onactivityresult", "");
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "canceled call", Toast.LENGTH_SHORT).show();
            }
            try {
               /* FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
                crashlytics.log(""+fileProvider);
                Log.e("hu",""+fileProvider);*/
                if (fileProvider == null && fileProvider.equals(Uri.EMPTY)) {
                    Bundle extras = data.getExtras();
                    Log.e("in onactivityresult", "" + extras);
                    Bitmap photo = null;
                    if (extras != null) {
                        photo = extras.getParcelable("data");
                    } else {
                        try {
                            photo = Images.Media.getBitmap(this.getContentResolver(), data.getData());
                            //  saveBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (photo != null) {
                        fileProvider2 = getImageUri(this, photo);
                        //   Log.e("hiii",""+fileProvider2);
                    } else {
                        fileProvider2 = data.getData();
                    }
                } else {
                    fileProvider2 = fileProvider;
                }
                Log.e("fileprovider ", fileProvider2.toString());
                photoFile = getPhotoFileUri(photoFileName);
                Uri dsturi = Uri.fromFile(new File(getApplicationContext().getCacheDir(), photoFileName));
                // Thread.sleep(100);
                UCrop.of(fileProvider2, dsturi)
                        .withAspectRatio(16, 9)
                        .withMaxResultSize(300, 200)
                        .start(ConsumerActivity4A11new.this);
            } catch (ActivityNotFoundException e) {
                Log.e("fileprovider ", e.toString());
                //display an error message if user device doesn't support
                String errorMessage = "Sorry - your device doesn't support the crop action!";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else if (requestCode == UCrop.REQUEST_CROP) {
            Uri saveduri = null;
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null && !resultUri.equals(Uri.EMPTY)) {
                try {
                    //   Thread.sleep(100);
                    Bitmap bitmap = Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    saveduri = imageutils.saveImage(bitmap, this, CommonPref.getUserDetails(getApplicationContext())
                            .get_MRUNo(), tvConNo.getText().toString().trim(), CommonPref.getmmyyyy(getApplicationContext()));
                } catch (IOException e) {
                    e.printStackTrace();
                    saveduri = fileProvider2;
                }
            } else {
                saveduri = fileProvider2;
            }
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.setCustomKey("Uri in Ucrop", "" + resultUri);
            crashlytics.setUserId(user.get_UserID());
            crashlytics.log("" + resultUri + user.get_UserID());
            SaveImage(saveduri.toString());
        }
        else if (resultCode == UCrop.RESULT_ERROR) {
            Uri saveduri = fileProvider;
            SaveImage(saveduri.toString());
            final Throwable cropError = UCrop.getError(data);
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.setCustomKey("Ucrop error", "" + UCrop.getError(data));
        }

    }

    public void AlertDialogForNoResultFound() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ConsumerActivity4A11new.this);
        alertDialog.setTitle("Result Not Found!!");
        alertDialog
                .setMessage("No Consumer Details had been found regarding your search.\n Please Try Again...");
        alertDialog.setPositiveButton("OK",
                (dialog, which) -> {
                    Intent intent = new Intent(getBaseContext(),
                            MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
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
            adapter = new ConsumerAdapter(ConsumerActivity4A11new.this,
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
            gps = new GPSTracker(ConsumerActivity4A11new.this);
            // check if GPS enabled
            if (gps.canGetLocation()) {
                Log.e("gps is", "" + gps.getLatitude());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoFile = getPhotoFileUri(photoFileName);
                fileProvider = FileProvider.getUriForFile(ConsumerActivity4A11new.this, "com.nic.app.biharelectricitybilling.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 1);
                } else {
                    startActivityForResult(intent, 1);
                }
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
            alertDialog.setPositiveButton("OK", (dialog, which) -> finish());
            // Showing Alert Message
            alertDialog.show();
        }
    }


    private void SaveImage(String data_img) {
        String success = "OK";
        if (success.matches("OK")) {
            // create class object
            gps = new GPSTracker(ConsumerActivity4A11new.this);
            latitude = 0.0;
            longitude = 0.0;
            long dateTime = 0;
            // check if GPS enabled
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                dateTime = gps.getTime();
                String date = Utiilties.getDate(dateTime, "dd/MM/yyyy");
                if (latitude > 0.0 && longitude > 0.0) {
                    long c = localDBHelper.savePhoto(String.valueOf(latitude), String.valueOf(longitude), data_img, tvConNo.getText().toString()
                            .trim(), date, cgender,cpurpose);
                    if (c > 0) {
                        if (isadddressupdated.equalsIgnoreCase("Y")) {
                            Intent intent = new Intent(getBaseContext(),
                                    MeterReadingStatusActivity.class);
                            intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
                                    .toString().trim());
                            intent.putExtra("FLAG", "0");
                            intent.putExtra("kvah",kvah);
                            intent.putExtra("kwh",kwh);


                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                            finish();
                        } else if (Integer.parseInt(CommonPref.getUserDetails(ConsumerActivity4A11new.this).get_distcode()) == 236) {
                            Intent intent = new Intent(getBaseContext(),
                                    MeterReadingStatusActivity.class);
                            intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
                                    .toString().trim());
                            intent.putExtra("FLAG", "0");
                            intent.putExtra("kvah",kvah);
                            intent.putExtra("kwh",kwh);

                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
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
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                        "Error in Local Database", Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    long c = localDBHelper.savePhoto("9999", "9999", data_img, tvConNo.getText().toString()
                            .trim(), date, cgender,cpurpose);
                    if (c > 0) {
                        if (isadddressupdated.equalsIgnoreCase("Y")) {
                            Intent intent = new Intent(getBaseContext(),
                                    MeterReadingStatusActivity.class);
                            intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
                                    .toString().trim());
                            intent.putExtra("FLAG", "0");
                            intent.putExtra("kvah",kvah);
                            intent.putExtra("kwh",kwh);

                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
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
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
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
        gps = new GPSTracker(ConsumerActivity4A11new.this);
        latitude = 0.0;
        longitude = 0.0;
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
                                .trim(), date, cgender,cpurpose);
                if (c > 0) {
                    if (isadddressupdated.equalsIgnoreCase("Y")) {
                        Intent intent = new Intent(getBaseContext(),
                                MeterReadingStatusActivity.class);
                        intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
                                .toString().trim());
                        intent.putExtra("FLAG", "0");
                        intent.putExtra("kvah",kvah);
                        intent.putExtra("kwh",kwh);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
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
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error in Local Database", Toast.LENGTH_LONG).show();
                }
            } else {
                long c = localDBHelper.savePhoto("9999",
                        "9999", "", tvConNo.getText().toString()
                                .trim(), date, cgender,cpurpose);
                if (c > 0) {
                    if (isadddressupdated.equalsIgnoreCase("Y")) {
                        Intent intent = new Intent(getBaseContext(),
                                MeterReadingStatusActivity.class);
                        intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
                                .toString().trim());
                        intent.putExtra("FLAG", "0");
                        intent.putExtra("kvah",kvah);
                        intent.putExtra("kwh",kwh);

                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
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
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
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
        return mobileno.matcher(mobno).matches();
    }

    @Override
    protected void onResume() {
        MARSHMALLOW_PERMISSION = new MarshmallowPermission(ConsumerActivity4A11new.this, Manifest.permission.CAMERA);
        MARSHMALLOW_PERMISSION = new MarshmallowPermission(ConsumerActivity4A11new.this, Manifest.permission.ACCESS_FINE_LOCATION);
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] imageInByte = stream.toByteArray();
        //this gives the size of the compressed image in kb
        long lengthbmp = imageInByte.length / 1024;
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, new FileOutputStream("/sdcard/mediaAppPhotos/compressed_new.jpg"));
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
    private void decideAgencyForOCR(){
        if (ocr_agency != null) {
            Toast.makeText(ConsumerActivity4A11new.this, ""+ocr_agency, Toast.LENGTH_SHORT).show();
            switch (ocr_agency){
                case "CORAL":
                    callCameraIntentforCORAL();
                      //callCameraIntentMagaCalibre();
                    break;
                case "CRYSTAL":
                    callCameraIntentforimagecrystal();
                    //callCameraIntentForMD();
                    break;
                case "SUJANIX":
                    callCameraIntentforsujanix();
                    break;
                case "MEGA-CALIBRE":
                    callCameraIntentMagaCalibre();
                    break;
                case "EMDEE":
                    callCameraIntentForMD();
                    break;
                default:
                    Toast.makeText(gps, "This ocr_agency not valid !", Toast.LENGTH_SHORT).show();
                    break;
            }
        }else {
            Toast.makeText(ConsumerActivity4A11new.this, "OCR Agency is NULL in User Details of MRC !", Toast.LENGTH_SHORT).show();
        }
    }
    public void callCameraIntentForMD(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setPackage("com.meterreading.ocr");
        intent.putExtra("MR_ID",user.get_UserID() );
        //intent.putExtra("AGENCY", user.get_bill_agency().trim());
        intent.putExtra("AGENCY", "EMDEE");
        intent.putExtra("CONSUMER_NUMBER",tvConNo.getText().toString().trim() );
        intent.putExtra("CONSUMER_NAME", tvConName.getText().toString().trim());
        intent.putExtra("ACCOUNT_ID", tvAcNo.getText().toString().trim());
        intent.putExtra("METER_LAT",latitude );
        intent.putExtra("METER_LON",longitude);
        intent.putExtra("PREV_READING_DATE",Prv_read_Date);
        intent.putExtra("PHASE",Phase);//
        intent.putExtra("METER_NO",Meter_no);
        intent.putExtra("CONTRACTED_LOAD", Load);
        intent.putExtra("PREV_READING",Previous_read);
        intent.putExtra("CATEGORY", category);
        intent.putExtra("METER_READER_NAME", user.get_UserName());
        intent.putExtra("METER_READER_ID", user.get_UserID());
        intent.putExtra("MRU_CONSUMER_COUNT", CommonPref.gettotalconsumer(ConsumerActivity4A11new.this));
        //intent.putExtra("AGENCY_ID", user.get_bill_agency().trim());
        intent.putExtra("AGENCY_ID","EMDEE");
        intent.putExtra("ACCESS_KEY", "Gq1fu9Vbip_sbpdcl");
        intent.putExtra("ACTIVITY_TYPE", "BILLING");
        intent.putExtra("SCAN_TYPE", read_stats);
        intent.putStringArrayListExtra("REQ_READING_VALUES",  new ArrayList<>(Arrays.asList(paramarray)));
        intent.putExtra("SUB_DIVISION_CODE", Sec_div_code.substring(0, 4));
        intent.putExtra("SECTION_CODE",Sec_div_code);
        intent.putExtra("AREA_CODE", user.get_MRUNo());
        intent.putExtra("BOARD_CODE", "SBPDCL");
        intent.setComponent(new ComponentName("com.meterreading.ocr", "com.meterreading.ocr.ReadMeterActivity"));
        Log.e("", "BOARD_CODE :" + "SBPDCL" + "SCAN_TYPE :" + read_stats + "REQ_READING_VALUES :" +reqread.toArray(paramarray).toString()
                + "PREV_READING :" + Previous_read + "CONSUMER_NO :" + tvConNo.getText().toString().trim() + "METER_NO :" + Meter_no + "PHASE :" + Phase + "," + user.get_UserName());
        Log.e("intent extras value", "Company MD : ***************************************************\n" + intent.getExtras());
        startActivityForResult(intent, 5005);
    }
    public void callCameraIntentMagaCalibre() {
        HashMap<String ,Object> hashMap = new HashMap();
        if (user.get_UserID().startsWith("2")) {
            hashMap.put("BOARD_CODE", "SBPDCL");
            hashMap.put("ACCESS_KEY", "Gq1fu9Vbip_sbpdcl");
        } else {
            hashMap.put("BOARD_CODE", "NBPDCL");
            hashMap.put("ACCESS_KEY", "Gq1fu9Vbip_nbpdcl");
        }
        hashMap.put("SCAN_TYPE", read_stats);
        Map prevReadings=new HashMap<String,String>();
        prevReadings.put(read_stats,Previous_read);
        prevReadings.put("Max_Demand",REC_DEEMAND);
        prevReadings.put("PF",POWER_FACTOR);
        hashMap.put("REQ_READING_VALUES", reqread);
        hashMap.put("PREV_READING", Previous_read);
        hashMap.put("PREV_READING_VALUES", prevReadings);
        hashMap.put("PREV_READING_DATE", Prv_read_Date);
        hashMap.put("CONSUMER_NO", tvConNo.getText().toString().trim());
        hashMap.put("METER_NO", Meter_no);
        hashMap.put("PHASE", Phase);
        hashMap.put("CATEGORY", category);
        hashMap.put("CONTRACTED_LOAD", Load);
        hashMap.put("METER_READER_ID", user.get_UserID());
        hashMap.put("AREA_CODE", user.get_MRUNo());
        hashMap.put("AGENCY_ID", user.get_bill_agency().trim());
        hashMap.put("ACCOUNT_ID", tvAcNo.getText().toString().trim());
        hashMap.put("METER_LAT", latitude);
        hashMap.put("METER_LON", longitude);//8,10,11
        hashMap.put("MRU_CONSUMER_COUNT", CommonPref.gettotalconsumer(ConsumerActivity4A11new.this));
        hashMap.put("SECTION_CODE", Sec_div_code);
        hashMap.put("SUB_DIVISION_CODE", Sec_div_code.substring(0, 4));
        hashMap.put("ACTIVITY_TYPE", "BILLING");
        hashMap.put("CONSUMER_NAME", tvConName.getText().toString().trim());
        hashMap.put("METER_READER_NAME", user.get_UserName());
        try {
            Intent sendIntent =new Intent();
            sendIntent.setComponent(new ComponentName("com.sipl_ocr_reading.sipl_ocr_reading", "com.sipl_ocr_reading.sipl_ocr_reading.MainActivity"));
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra("map", hashMap);
            sendIntent.setType("sipl/readmeter");
            Log.d("log",hashMap.toString());
            if(sendIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(sendIntent,5004);
                //finish();
            }
        }catch (Exception e){
            Toast.makeText(ConsumerActivity4A11new.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
            Log.d("sdnnsdjc",e.getMessage());

        }
    }
    public void callCameraIntentforCORAL() {
        String isPendingBill = localDBHelper.getIsPendingBill(tvAcNo.getText().toString().trim());
        if (!(isPendingBill.trim().equalsIgnoreCase("P") || isPendingBill.trim().equalsIgnoreCase("G"))) {
            gps = new GPSTracker(ConsumerActivity4A11new.this);
            // check if GPS enabled
            if (gps.canGetLocation()) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("bsmrscan/meter");
                intent.setPackage("in.coral.met");
                if (user.get_UserID().startsWith("2")) {
                    intent.putExtra("BOARD_CODE", "SBPDCL");
                    intent.putExtra("ACCESS_KEY", "Gq1fu9Vbip_sbpdcl");
                } else {
                    intent.putExtra("BOARD_CODE", "NBPDCL");
                    intent.putExtra("ACCESS_KEY", "Gq1fu9Vbip_nbpdcl");
                }
                intent.putExtra("SCAN_TYPE", read_stats);
                final JSONObject prevReadings = new JSONObject();  //import org.json.JSONObject;
                try {
                    prevReadings.put(read_stats, Previous_read);
                    prevReadings.put("Max_Demand", REC_DEEMAND);
                    prevReadings.put("PF", POWER_FACTOR);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("REQ_READING_VALUES", reqread.toArray(paramarray));
                intent.putExtra("PREV_READING", Previous_read);
                intent.putExtra("PREV_READING_VALUES", prevReadings.toString());
                intent.putExtra("PREV_READING_DATE", Prv_read_Date);
                intent.putExtra("CONSUMER_NO", tvConNo.getText().toString().trim());
                intent.putExtra("METER_NO", Meter_no);
                intent.putExtra("PHASE", Phase);
                intent.putExtra("CATEGORY", category);
                intent.putExtra("CONTRACTED_LOAD", Load);
                intent.putExtra("METER_READER_ID", user.get_UserID());
                intent.putExtra("AREA_CODE", user.get_MRUNo());
                intent.putExtra("AGENCY_ID", user.get_bill_agency().trim());
                intent.putExtra("ACCOUNT_ID", tvAcNo.getText().toString().trim());
                intent.putExtra("METER_LAT", latitude);
                intent.putExtra("METER_LON", longitude);//8,10,11
                intent.putExtra("MRU_CONSUMER_COUNT", CommonPref.gettotalconsumer(ConsumerActivity4A11new.this));
                intent.putExtra("SECTION_CODE", Sec_div_code);
                intent.putExtra("SUB_DIVISION_CODE", Sec_div_code.substring(0, 4));
                intent.putExtra("ACTIVITY_TYPE", "BILLING");
                intent.putExtra("CONSUMER_NAME", tvConName.getText().toString().trim());
                intent.putExtra("METER_READER_NAME", user.get_UserName());
                Log.e("", "BOARD_CODE :" + "SBPDCL" + "SCAN_TYPE :" + read_stats + "REQ_READING_VALUES :" + reqread.toArray(paramarray)
                        + "PREV_READING :" + Previous_read + "CONSUMER_NO :" + tvConNo.getText().toString().trim() + "METER_NO :" + Meter_no + "PHASE :" + Phase + "," + user.get_UserName());
                Log.e("inent extras value", "" + intent.getExtras());
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafe = activities.size() > 0;
                if (isIntentSafe) {
                    startActivityForResult(intent, 5001);
                } else {
                    Toast.makeText(ConsumerActivity4A11new.this, "Meter Reading App Not Found!", Toast.LENGTH_SHORT).show();
                    // Uncomment below to navigate user to install the app from playstore
                    // final String appPackageName = "in.coral.met";
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://electricity.bharatsmr.com/mrapp")));
                        // overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
                    } catch (ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://electricity.bharatsmr.com/mrapp")));
                        // overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
                    }
                }
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
            alertDialog.setPositiveButton("OK", (dialog, which) -> finish());
            // Showing Alert Message
            alertDialog.show();
        }
    }

    public void callCameraIntentforsujanix() {
        String isPendingBill = localDBHelper.getIsPendingBill(tvAcNo.getText().toString().trim());
        if (!(isPendingBill.trim().equalsIgnoreCase("P") || isPendingBill.trim().equalsIgnoreCase("G"))) {
            gps = new GPSTracker(ConsumerActivity4A11new.this);
            // check if GPS enabled
            if (gps.canGetLocation()) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("bsmrscan/meter");
                intent.setPackage("com.sujanix.truereadlink");
                if (user.get_UserID().startsWith("2")) {
                    intent.putExtra("BOARD_CODE", "SBPDCL");
                    intent.putExtra("ACCESS_KEY", "TRL_Nixlu1EW5g9");
                } else {
                    intent.putExtra("BOARD_CODE", "NBPDCL");
                    intent.putExtra("ACCESS_KEY", "TRL_Nixlu1EW5g9");
                }
                intent.putExtra("SCAN_TYPE", read_stats);
                final JSONObject prevReadings = new JSONObject();  //import org.json.JSONObject;
                try {
                    prevReadings.put(read_stats, Previous_read);
                    prevReadings.put("Max_Demand", REC_DEEMAND);
                    prevReadings.put("PF", POWER_FACTOR);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("REQ_READING_VALUES", reqread.toArray(paramarray));
                intent.putExtra("PREV_READING", Previous_read);
                intent.putExtra("PREV_READING_VALUES", prevReadings.toString());
                intent.putExtra("PREV_READING_DATE", Prv_read_Date);
                intent.putExtra("CONSUMER_NO", tvConNo.getText().toString().trim());
                intent.putExtra("METER_NO", Meter_no);
                intent.putExtra("PHASE", Phase);
                intent.putExtra("CATEGORY", category);
                intent.putExtra("METER_READER_ID", user.get_UserID());
                intent.putExtra("AREA_CODE", user.get_MRUNo());
                intent.putExtra("AGENCY_ID", user.get_bill_agency().trim());
                intent.putExtra("ACCOUNT_ID", tvAcNo.getText().toString().trim());
                intent.putExtra("METER_LAT", latitude);
                intent.putExtra("CONTRACTED_LOAD", Load);
                intent.putExtra("METER_LON", longitude);//8,10,11
                intent.putExtra("MRU_CONSUMER_COUNT", CommonPref.gettotalconsumer(ConsumerActivity4A11new.this));
                intent.putExtra("SECTION_CODE", Sec_div_code);
                intent.putExtra("SUB_DIVISION_CODE", Sec_div_code.substring(0, 4));
                intent.putExtra("ACTIVITY_TYPE", "BILLING");
                intent.putExtra("CONSUMER_NAME", tvConName.getText().toString().trim());
                intent.putExtra("METER_READER_NAME", user.get_UserName());
                Log.e("data", "BOARD_CODE :" + "SBPDCL" + "SCAN_TYPE :" + read_stats + "REQ_READING_VALUES :" + reqread.toArray(paramarray)
                        + "PREV_READING :" + Previous_read + "CONSUMER_NO :" + tvConNo.getText().toString().trim() + "METER_NO :" + Meter_no + "PHASE :" + Phase + "," + user.get_UserName());
                Log.e("inent extras value", "" + intent.getExtras());
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafe = activities.size() > 0;
                if (isIntentSafe) {
                    startActivityForResult(intent, 5003);
                } else {
                    Toast.makeText(ConsumerActivity4A11new.this, "Meter Reading App Not Found!", Toast.LENGTH_SHORT).show();
                    // Uncomment below to navigate user to install the app from playstore
                    // final String appPackageName = "in.coral.met";
                    try {
                        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://true-read.com/trueread/apk/download.php")));
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/file/d/1076VjRyHJj5OZBvPpL5BrtSwp9Eb_YXO/view?usp=sharing")));
                        // overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
                    } catch (ActivityNotFoundException anfe) {
                        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/file/d/1076VjRyHJj5OZBvPpL5BrtSwp9Eb_YXO/view?usp=sharing")));
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/file/d/1076VjRyHJj5OZBvPpL5BrtSwp9Eb_YXO/view?usp=sharing")));
                        // overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
                    }
                }
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
            alertDialog.setPositiveButton("OK", (dialog, which) -> finish());
            // Showing Alert Message
            alertDialog.show();
        }
    }

    public void callCameraIntentforimagecrystal() {
        String isPendingBill = localDBHelper.getIsPendingBill(tvAcNo.getText().toString().trim());
        if (!(isPendingBill.trim().equalsIgnoreCase("P") || isPendingBill.trim().equalsIgnoreCase("G"))) {
            gps = new GPSTracker(ConsumerActivity4A11new.this);
            // check if GPS enabled
            if (gps.canGetLocation()) {
                Intent intent = new Intent();
               // intent.setPackage("com.grid.gmr");
               intent.setComponent(new ComponentName("com.grid.gmrnew","com.grid.gmrnew.ReceiveDataActivity"));
                Bundle bundle = new Bundle();
                if (user.get_UserID().startsWith("2")) {
                    bundle.putString("BOARD_CODE", "SBPDCL");
                    bundle.putString("ACCESS_KEY", "Gq1fu9Vbip_sbpdcl");
                } else {
                    bundle.putString("BOARD_CODE", "NBPDCL");
                    bundle.putString("ACCESS_KEY", "Gq1fu9Vbip_nbpdcl");
                }
                bundle.putString("SCAN_TYPE", read_stats);
                ArrayList reqParameters = new ArrayList();
                //reqParameters.add(maxdemand);
                //reqParameters.add(powerfactor);
                if (flag){
                    reqParameters.add("KWH");
                    reqParameters.add("KVAH");
                    if (maxdemand.equals("Max_Demand")){
                        reqParameters.add("KVA");
                    }else{
                        //reqParameters.add("");
                    }
                }else{
                    reqParameters.add("KWH");
                    if (maxdemand.equals("Max_Demand")){
                        reqParameters.add("KW");
                    }else{
                        //reqParameters.add("");
                    }
                }

                if (powerfactor.equals("Power_Factor")){
                    reqParameters.add("PF");
                }else{
                    //reqParameters.add("");
                }
                bundle.putStringArrayList("REQ_READING_VALUES", reqParameters);
                bundle.putDouble("PREV_READING", Double.parseDouble(Previous_read));
                final JSONObject prevReadings = new JSONObject();  //import org.json.JSONObject;
                try {
                    // String read_stats = Previous_read; //or kVah
                    prevReadings.put("read_stats", Previous_read);
                    prevReadings.put("Max_Demand", REC_DEEMAND);
                    prevReadings.put("PF", POWER_FACTOR);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bundle.putString("PREV_READING_VALUES", prevReadings.toString());
                bundle.putString("PREV_READING_DATE", Prv_read_Date);
                bundle.putString("CONSUMER_NO", tvConNo.getText().toString().trim()); //for your testing, dummy consumers from 203410001 to 203410030 are available. In backend we have workorders for only these.
                //let us know if you need me to create workorders for few of your actual consumer numbers.
                bundle.putString("METER_NO", Meter_no);
                bundle.putString("PHASE", Phase);
                bundle.putString("CATEGORY", category);
                bundle.putString("AGENCY_ID", user.get_bill_agency().trim());
                bundle.putString("ACCOUNT_ID", tvAcNo.getText().toString().trim());
                bundle.putString("MRU_CONSUMER_COUNT", CommonPref.gettotalconsumer(ConsumerActivity4A11new.this));
                bundle.putString("METER_READER_NAME", user.get_UserName());
                bundle.putString("METER_READER_ID", user.get_UserID());
                bundle.putString("AREA_CODE", user.get_MRUNo());
                intent.putExtra("CONTRACTED_LOAD", Load);
                bundle.putString("SECTION_CODE", Sec_div_code);
                bundle.putString("SUB_DIVISION_CODE", Sec_div_code.substring(0, 4));
                bundle.putString("ACTIVITY_TYPE", "BILLING");
                bundle.putString("CONSUMER_NAME", tvConName.getText().toString().trim());
                bundle.putString("METER_READER_PHONE", user.get_MobileNo());
                intent.putExtra("sendData", bundle);
                try{
                    startActivityForResult(intent, 5002);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(ConsumerActivity4A11new.this, "Meter Reading App Not Found!", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/file/d/1zAgJAwMf6zYIAdMj1Dj1JOXyNdDjOSaq/view?usp=sharing")));
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/drive/folders/1oreo2p1IorHKFQ-7g-wT6mVyLIVXMuED?usp=drive_link")));
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/file/d/1RmkglvSiRvPqCNrWAVDiIJ-L13CRl6cy/view?usp=sharing")));
                }
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
            alertDialog.setPositiveButton("OK", (dialog, which) -> finish());
            alertDialog.show();
        }
    }

    Bitmap saveFile(Uri sourceuri) {
        ParcelFileDescriptor inputPFD;
        File root = getFilesDir();
        //  File myDir = new File(root, "/scan_images/");
        File myDir1 = new File(root, CROP_IMAGE_FILE_PATH1);
        if (!myDir1.exists()) {
            myDir1.mkdirs();
        }
        String sourceFilename = sourceuri.getPath();
        sourceFilename = tvConNo.getText().toString().trim() + ".jpg";
        myDir = new File(myDir1, sourceFilename);
        try {
            inputPFD = getContentResolver().openFileDescriptor(sourceuri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("MainActivity", "File not found.");
            return null;
        }
        // Get a regular file descriptor for the file
        FileDescriptor fd = inputPFD.getFileDescriptor();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(fd));
            bos = new BufferedOutputStream(new FileOutputStream(myDir, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getBitmapFromPath(myDir.getAbsolutePath());
    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public File getPhotoFileUri1(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG1);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "cropimage", null);
        return Uri.parse(path);
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private class updateDtNo extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(
                ConsumerActivity4A11new.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                ConsumerActivity4A11new.this).create();

        public updateDtNo() {
        }

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
            return WebServiceHelper.UpdateMRU(param[0], param[1],
                    param[2], user);
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
                                tvAcNo.getText().toString(), "N", cgender,cpurpose);
                        if (c > 0) {
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error in Local Database", Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    long c = localDBHelper.updateMru(etMobileNo.getText()
                            .toString(), etDTNo.getText().toString(), tvAcNo
                            .getText().toString(), "Y", cgender,cpurpose);
                    Toast.makeText(getApplicationContext(), "Error in Network",
                            Toast.LENGTH_LONG).show();
                }

                alertDialog.cancel();
                if (Unmeter.equals("UM")) {
                    getgps();
                } else if (Unmeter.equals("MS")) {
                    if (Ocr.equalsIgnoreCase("Y")) {
                        decideAgencyForOCR();
                    } else {
                        callCameraIntent();
                    }
                }
            }

        }
    }
    private class updatebillingreason extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(
                ConsumerActivity4A11new.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                ConsumerActivity4A11new.this).create();
        public updatebillingreason() {

        }
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
                    finish();
                }
                alertDialog.cancel();
            }

        }
    }

    public static int sizeOf(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return data.getByteCount();
        } else {
            return data.getAllocationByteCount();
        }
    }

    public Bitmap compressbitmap(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        return decoded;
    }

    /*    public void deleteuri(Uri uri){
            File fdelete = new File(uri. getPath());
            if (fdelete. exists()) {
               fdelete. delete();
            }

        }*/
    public boolean test() {
        boolean flag = false;
        String tariff = tvCategory.getText().toString().trim();
        if (cgender.equalsIgnoreCase("N") || cgender.equalsIgnoreCase("NA") || cgender.equalsIgnoreCase("null")) {
            Toast.makeText(getApplicationContext(), "Please Select Gender First", Toast.LENGTH_SHORT).show();
            flag = false;
        }else{
            flag=true;
        }
        if(flag) {
            if (tariff.equalsIgnoreCase("LTIS1D") || tariff.equalsIgnoreCase("LTIS2D") || tariff.equalsIgnoreCase("NDS1D") || tariff.equalsIgnoreCase("NDS2D")) {
                if (cpurpose.equalsIgnoreCase("N") || cpurpose.equalsIgnoreCase("NA")) {
                    Toast.makeText(this, "Please select purpose of Connection First", Toast.LENGTH_SHORT).show();
                    flag = false;
                } else {
                    flag = true;
                }

            }else{
                flag=true;
                cpurpose="";
            }
        }else{
            flag=false;

        }
        return flag;
    }
    public void setspiner(ArrayList<String> purposeList ,String purpose){
        int index=0;
        if(purpose.length()>5){
            index=purposeList.indexOf(purpose)+1;
        }
        purposeList.add(0, "Select Purpose");
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, purposeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sppurpose.setAdapter(adapter);
        sppurpose.setSelection(index);
    }

//    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//
//                if (result.getResultCode() == Activity.RESULT_OK) {
//                    isNDSdataFound=true;
//                    Intent intent = result.getData();
//                    kwh=intent.getStringExtra("kwh");
//                    kvah=intent.getStringExtra("kvah");
//                }else{
//                    isNDSdataFound=false;
//                }
//            });
}