package com.nic.app.biharelectricitybilling.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.UserDetails;
import com.nic.app.biharelectricitybilling.smsRecever.SmsListener;
import com.nic.app.biharelectricitybilling.smsRecever.SmsReceiver;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.GlobalVariables;
import com.nic.app.biharelectricitybilling.util.MarshmallowPermission;
import com.nic.app.biharelectricitybilling.util.Utiilties;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class LoginActivity extends Activity {
    MarshmallowPermission MARSHMALLOW_PERMISSION = null;
    ConnectivityManager cm;
    public static String UserPhoto;
    String version;
    TelephonyManager tm;
    private static String imei;
    DataBaseHelper localDBHelper;
    private ActionBar actionBar;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 10;
    private boolean initse1;
    String serial_id = "";
    private ProgressDialog dialog = null;
    String userid = "", password = "";
    SmsReceiver smsReceiver;
    public LinearLayout layout;
    IntentFilter filter;
    private ProgressDialog dialog1;
    CountDownTimer countDownTimer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Database Opening
        localDBHelper = new DataBaseHelper(LoginActivity.this);
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
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
        // For displaying title and subtitle and change text color
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Login</font>"));
        dialog = new ProgressDialog(
                LoginActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Authenticating...");
        layout = findViewById(R.id.imei_linear);
        layout.setVisibility(View.GONE);
        dialog1 = new ProgressDialog(LoginActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Call some material design APIs here
            checkAndRequestPermissions();

        } else {
            if (!initse1) init();
        }

    }
    private boolean init(){
        readPhoneState();
        checkOnline();
        initse1 = true;
        return initse1;
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    protected void checkOnline() {
        // TODO Auto-generated method stub
        super.onResume();

        if (!Utiilties.isOnline(LoginActivity.this)) {
            AlertDialog.Builder ab = new AlertDialog.Builder(LoginActivity.this);
            ab.setMessage(Html
                    .fromHtml("<font color=#000000>Internet Connection is not avaliable..Please Turn ON Network Connection OR Continue With Off-line Mode..\nTo Turn ON Network Connection Press Yes Button else To Continue With Off-Line Mode Press No Button..</font>"));
            ab.setPositiveButton("Turn On Network Connection",
                    (dialog, whichButton) -> {
                        GlobalVariables.isOffline = false;
                        Intent I = new Intent(
                                Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(I);
                        overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);

                    });
            ab.setNegativeButton("Continue Offline",
                    (dialog, whichButton) -> GlobalVariables.isOffline = true);

            ab.create().getWindow().getAttributes().windowAnimations = R.style.alert_animation;
            ab.show();

        } else {
            GlobalVariables.isOffline = false;
            // new CheckUpdate().execute();
        }
    }

    public void Login(View view) {
        initse1 = true;
        if (!GlobalVariables.isOffline && !Utiilties.isOnline(LoginActivity.this)) {
            AlertDialog.Builder ab = new AlertDialog.Builder(LoginActivity.this);
            ab.setMessage(Html
                    .fromHtml("<font color=#000000>Internet Connection is not avaliable..Please Turn ON Network Connection OR Continue With Off-line Mode..\nTo Turn ON Network Connection Press Yes Button else To Continue With Off-Line Mode Press No Button..</font>"));
            ab.setPositiveButton("Turn On Network Connection",
                    (dialog, whichButton) -> {
                        Intent I = new Intent(
                                Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(I);
                        overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);

                    });
            ab.setNegativeButton("Continue Offline",
                    (dialog, whichButton) -> GlobalVariables.isOffline = true);
            ab.create().getWindow().getAttributes().windowAnimations = R.style.alert_animation;
            ab.show();

        } else {

            final AutoCompleteTextView userName = (AutoCompleteTextView) findViewById(R.id.email);
            final EditText userPass = (EditText) findViewById(R.id.password);
            String[] param = new String[3];
            param[0] = userName.getText().toString();
            param[1] = userPass.getText().toString();
            if (imei==null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    assert tm != null;
                    imei = tm.getDeviceId();
                } else {
                    //imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                    imei = Utiilties.getIMEI_forAndroid10(LoginActivity.this);
                }
            }
            param[2] = imei;
            userid = userName.getText().toString();
            password = userPass.getText().toString();
            if (userid.trim().equals("")) {
                Toast.makeText(this, "Enter User Id", Toast.LENGTH_SHORT).show();
            } else if (password.trim().equals("")) {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            }else {
                filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
                smsReceiver=new SmsReceiver();
                registerReceiver(smsReceiver, filter);
                SmsReceiver.bindListener(messageText -> {
                    // text_resend.setVisibility(View.GONE);
                    Log.i("activity--",""+messageText);
                   // dialog1.dismiss();
                    //if (smsVerificationService!=null && smsVerificationService.getStatus()!=SmsVerificationService.Status.RUNNING) {
                   new SmsVerificationService(LoginActivity.this,imei,serial_id).execute(messageText.split(" ")[0].trim());
                    //}
                });
                new LoginTask().execute(param);
            }
        }

    }

    private class LoginTask extends AsyncTask<String, Void, UserDetails> {
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                LoginActivity.this).create();

        @Override
        protected void onPreExecute() {
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected UserDetails doInBackground(String... param) {
            if (GlobalVariables.isOffline) {
                UserDetails userDetails = new UserDetails();
                //   userDetails= CommonPref.getUserDetails(LoginActivity.this);
                userDetails.set_isAuthenticated(true);
                return userDetails;
            } else {
                return WebServiceHelper.Login(userid, password, imei);
            }
        }

        @Override
        protected void onPostExecute(UserDetails result) {
            Log.d("user details",""+result);
         //   Toast.makeText(LoginActivity.this, ""+result.get_billmsg().toString(), Toast.LENGTH_SHORT).show();
            if (dialog.isShowing()) {
                dialog.dismiss();
                if (result == null) {
                    alertDialog.setTitle("Failed");
                    alertDialog.setMessage("Authentication Failed");
                    alertDialog.show();
                } else if (!result._isAuthenticated) {
                 if((result.get_msg().toString().contains("OTP SENT"))){
                     dialog.cancel();
//                        dialog.setCanceledOnTouchOutside(false);
//                        dialog.setMessage("Waiting For Otp...");
//                        dialog.show();
                     countDownTimer = new CountDownTimer(10000, 1000) {

                         public void onTick(long millisUntilFinished) {
                             long seconds=millisUntilFinished / 1000;
                             dialog.setCanceledOnTouchOutside(false);
                             dialog.setMessage("Waiting For Otp for 10 seconds... remaining "+(seconds)+" seconds");
                             dialog.show();
                             //here you can have your logic to set text to edittext
                         }

                         public void onFinish() {
                             dialog.dismiss();
                             AlertDialogForOTP();
                         }

                     }.start();
                    }else{
                        alertDialog.setTitle("Failed");
                        alertDialog.setMessage("Authentication Failed" + result.get_msg()+(result.get_msg().contains("IMEI")?"Your IMEI : "+imei:""));
                        alertDialog.show();
                        String msg ="ERROR : USER IS INACTIVE,IMEI NOT ALLOTED";
                        String msg1 ="ERROR :,IMEI NOT ALLOTED";
                        String msg3 = "ERROR :,IMEI MISMATCH";
                        String msg2 ="ERROR:INVALID CREDENTIAL";
//                     Toast.makeText(LoginActivity.this, result.get_msg(), Toast.LENGTH_SHORT).show();
                        if(result.get_msg().equalsIgnoreCase(msg) || result.get_msg().equalsIgnoreCase(msg1)|| result.get_msg().equalsIgnoreCase(msg3)){
                            layout.setVisibility(View.VISIBLE);
                            final TextView tvimei = findViewById(R.id.txtimei);
                            tvimei.setVisibility(View.GONE);
                            final TextView getimei = findViewById(R.id.getimei);
                            getimei.setVisibility(View.VISIBLE);
                            getimei.setText("IMEI NO :"+imei);
                        }else if(result.get_msg().equalsIgnoreCase(msg2)){
                            layout.setVisibility(View.GONE);
                        }

                 //    Toast.makeText(LoginActivity.this, "IMEI wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    final EditText userPass = (EditText) findViewById(R.id.password);
                    final AutoCompleteTextView userName = (AutoCompleteTextView) findViewById(R.id.email);
                    if (!GlobalVariables.isOffline) {
                        if (result != null && !result.get_IMEI().trim().equals("NA") && !result.get_IMEI().trim().equals(imei)) {
                            alertDialog.setTitle("Wrong Device");
                            alertDialog.setMessage("Device is not registerd with your account !");
                            alertDialog.show();
                            return;
                        }
                        try {
                            GlobalVariables.LoggedUser = result;
                            GlobalVariables.LoggedUser.set_UserID(userName
                                    .getText().toString().trim());
                            GlobalVariables.LoggedUser.set_password(userPass
                                    .getText().toString().trim());
                            CommonPref.setUserDetails(getApplicationContext(),
                                    GlobalVariables.LoggedUser);
                        //    Log.e("in logged user", GlobalVariables.LoggedUser.toString());
                            CommonPref.setUserDetails(getApplicationContext(), result.get_MRUNo());
                            SQLiteDatabase db = localDBHelper.getReadableDatabase();
                            localDBHelper.insertUserDetails(GlobalVariables.LoggedUser);
                            Intent iUserHome = new Intent(getApplicationContext(), MainActivity.class);
                            iUserHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(iUserHome);
                          //  overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
                            //  finish();
                        } catch (Exception ex) {
                            Toast.makeText(LoginActivity.this, "Login failed",
                                    Toast.LENGTH_SHORT).show();
                            Utiilties.writeIntoLog(Log.getStackTraceString(ex));

                        }
                    } else {
                        if (localDBHelper.getUserCount() > 0) {
                            GlobalVariables.LoggedUser = localDBHelper
                                    .getUserDetails(userName.getText()
                                                    .toString().trim(),
                                            userPass.getText().toString());

                            if (GlobalVariables.LoggedUser != null) {
                                CommonPref.setUserDetails(
                                        getApplicationContext(),
                                        GlobalVariables.LoggedUser);
                                GlobalVariables.Last_Visited = GlobalVariables.LoggedUser.get_LastVisitedOn();
                                Intent iUserHome = new Intent(
                                        getApplicationContext(),
                                        MainActivity.class);
                                iUserHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(iUserHome);
                             //   overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);

                                //   finish();
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "User name and password not matched !",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "please enable internet connection for first time login.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        //clearApplicationData();
        super.onDestroy();
    }
    @SuppressLint("HardwareIds")
    public void readPhoneState() {
        MARSHMALLOW_PERMISSION = new MarshmallowPermission(LoginActivity.this, Manifest.permission.RECEIVE_SMS);
        MARSHMALLOW_PERMISSION = new MarshmallowPermission(LoginActivity.this, Manifest.permission.READ_SMS);
 /*             MARSHMALLOW_PERMISSION = new MarshmallowPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE);
                MARSHMALLOW_PERMISSION = new MarshmallowPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
               MARSHMALLOW_PERMISSION = new MarshmallowPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
              MARSHMALLOW_PERMISSION = new MarshmallowPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);*/
        if (MARSHMALLOW_PERMISSION.result == -1 || MARSHMALLOW_PERMISSION.result == 0) {
            try {
                tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null)
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    assert tm != null;
                    imei = tm.getDeviceId();
                } else {
                    //imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                    imei = Utiilties.getIMEI_forAndroid10(LoginActivity.this);
                }
               //Log.e("imei",imei);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null) ;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    assert tm != null;
                    imei = tm.getDeviceId();
                } else {
                    //imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                    imei = Utiilties.getIMEI_forAndroid10(LoginActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                serial_id = Build.getSerial();
            }else{
                // serial_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                serial_id=imei;
            }
        } else {
            serial_id = Build.SERIAL;
        }
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView tv = (TextView) findViewById(R.id.txtVersion);
            String txt="App Version : (" + version+"v )" ;
            tv.setText(txt);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
  /*  public void readPhoneState() {
        initse1=true;
        MARSHMALLOW_PERMISSION = new MarshmallowPermission(LoginActivity.this, Manifest.permission.RECEIVE_SMS);
        MARSHMALLOW_PERMISSION = new MarshmallowPermission(LoginActivity.this, Manifest.permission.READ_SMS);
        MARSHMALLOW_PERMISSION = new MarshmallowPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE);
        if (MARSHMALLOW_PERMISSION.result == -1 || MARSHMALLOW_PERMISSION.result == 0) {
            try {
                tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        imei = tm.getDeviceId();
                      //  imei=tm.getImei(0);
                    } else {
                        //imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                        imei = Utiilties.getIMEI_forAndroid10(LoginActivity.this);
                    }
                    try {
                        version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                        TextView tv = (TextView) findViewById(R.id.txtVersion);
                        String txt="App Version : " + version + " ( " + imei + " )";
                        tv.setText(txt);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
              //  Utiilties.writeIntoLog(Log.getStackTraceString(e));

            }
        } else {
            try {
              //  tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                 //   imei = tm.getDeviceId();
                 //   imei = tm.getImei(0);
                    serial_id=Build.getSerial();

                } else {
                    // imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                  //  imei = Utiilties.getIMEI_forAndroid10(LoginActivity.this);
                    serial_id=imei;
                }
                try {
                    version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                    TextView tv = (TextView) findViewById(R.id.txtVersion);
                    String txt="App Version : " + version + " ( " + imei + " )";
                    tv.setText(txt);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utiilties.writeIntoLog(Log.getStackTraceString(e));
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                serial_id = Build.getSerial();
            } else {
                //  serial_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                imei = Utiilties.getIMEI_forAndroid10(LoginActivity.this);
            }
        } else {
            serial_id = Build.SERIAL;
        }
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView tv = (TextView) findViewById(R.id.txtVersion);
            tv.setText("App Version : " + version + " ( " + imei + " )");
        } catch (PackageManager.NameNotFoundException e) {

        }
    }*/
    private boolean checkAndRequestPermissions() {
        int read_media;
        int read_phone = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int write_external = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            read_media = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
        }else {
            read_media = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        int read_location_fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int read_camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int bluetooth = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        int bluetooth_scan = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        int bluetooth_Connect = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (bluetooth_scan != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN);
        }
        if (bluetooth_Connect != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        if (bluetooth != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH);
        }
        if (read_phone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (read_media != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add((android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)?Manifest.permission.READ_MEDIA_IMAGES:Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (write_external != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (read_location_fine != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (read_camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (initse1==false) {
                        init();
                    }
                } else {
                    if (initse1==false) {
                        init();
                    }
                }
                break;
        }
    }
    public class SmsVerificationService extends AsyncTask<String, Void, String> {
        private Activity activity;
        private  AlertDialog alertDialog;
        String imei,serial_id;
        public SmsVerificationService(Activity activity, String imei, String serial_id) {
            this.activity = activity;
            this.imei=imei;
            this.serial_id=serial_id;
            alertDialog = new AlertDialog.Builder( this.activity).create();
        }
        @Override
        protected void onPreExecute() {
            /*this.dialog1.setCanceledOnTouchOutside(false);
            this.dialog1.setMessage("Verifying...");
            this.dialog1.show();*/
            dialog.setMessage("Verifying...");
        }
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            if (Utiilties.isOnline(activity)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Log.d("params",strings[0]);
                    return WebServiceHelper.validateotp(userid, imei, strings[0]);
                } else {
                    Log.e("error","Your device must have atleast Kitkat or Above Version");
                }
            } else {
                Log.e("error","No Internet Connection !");
            }
            return result;

        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Log.e("res","res varification :"+result);
                if (result.contains("SUCCESS")) {
                    if (dialog.isShowing()) dialog.dismiss();
                    final EditText userPass = (EditText) findViewById(R.id.password);
                    final AutoCompleteTextView userName = (AutoCompleteTextView) findViewById(R.id.email);
                    new LoginTask().execute(userName.getText().toString(),userPass.getText().toString(),imei);
                } else {
                    if (dialog.isShowing()) dialog.dismiss();
                    Toast.makeText(activity, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (dialog.isShowing()) dialog.cancel();
                Toast.makeText(activity, "Server Problem", Toast.LENGTH_SHORT).show();
            }
        }
    }
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

    public void AlertDialogForOTP() {
        final Dialog dialogOtp = new Dialog(LoginActivity.this);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before*/
        dialogOtp.setContentView(R.layout.otp_dialog);
        dialogOtp.setTitle("OTP Dialog");
        // set the custom dialogOtp components - text, image and button
        //final EditText otpEdit = (EditText) dialogOtp.findViewById(R.id.enter_otp);
        final EditText otp_view = (EditText) dialogOtp.findViewById(R.id.otp_view);
        final Button button_submit = (Button) dialogOtp.findViewById(R.id.button_submit);
        final TextView text_timer = (TextView) dialogOtp.findViewById(R.id.text_timer);
        countDownTimer = new CountDownTimer(50000, 1000) {
            public void onTick(long millisUntilFinished) {
                long secs= millisUntilFinished / 1000;
                text_timer.setVisibility(View.VISIBLE);
                text_timer.setText(Html.fromHtml("<b style=\"color:White;\">" + (secs) + "</b> "));
                text_timer.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                text_timer.setVisibility(View.GONE);
                dialogOtp.dismiss();
            }

        }.start();
        // if button is clicked, close the custom dialogOtp
        SmsReceiver.bindListener(messageText -> {
            //Log.d("activity",""+messageText);
            otp_view.setText(messageText.split(" ")[0].trim());
        });
        button_submit.setOnClickListener(v -> {
            if (otp_view.getText().length() < 4) {
                Toast.makeText(LoginActivity.this, "Enter Valid OTP !", Toast.LENGTH_SHORT).show();
            } else {
                button_submit.setClickable(false);
                dialogOtp.dismiss();
                new SmsVerificationService(LoginActivity.this, imei, imei).execute(otp_view.getText().toString().trim());
            }

        });
        dialogOtp.setCancelable(false);
        dialogOtp.show();
    }
}
