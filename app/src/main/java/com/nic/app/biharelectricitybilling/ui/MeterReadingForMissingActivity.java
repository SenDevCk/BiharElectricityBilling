package com.nic.app.biharelectricitybilling.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.MRUDetails;
import com.nic.app.biharelectricitybilling.entity.UserDetails;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.Utiilties;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import print.ShowMsg;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

/**
 * Created by NIC123 on 08/04/16.
 */
public class MeterReadingForMissingActivity extends Activity implements ReceiveListener {
    private ActionBar actionBar;
    LinearLayout linReadingStatus, linPowFact, linRecDemand;
    Spinner spReadingStatus;
    Boolean flag = false;
    String stringReadingStatus, stringAcNo, stringPreRead, stringPowFact, stringRecDemd, stringIsRecycle = "N";
    Button btnSubmit;
    EditText etMeterReading, etPowerFactor, etMaxdemand;
    DataBaseHelper localDBHelper;
    MRUDetails mruList;
    private Printer mPrinter = null;
    private Context mContext = null;
    String intentFlag = "";
    MRUDetails mru1;
    TextView txtreadingstaus, tvReadingLable;
    LinearLayout laymeterproperlyfixed, laymeterproperlyfixed1, maxdeemandkva;
    //BillDetails bill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_status);
        stringAcNo = getIntent().getStringExtra("ACCOUNT_NO");
        intentFlag = getIntent().getStringExtra("FLAG");
        // Database Opening
        localDBHelper = new DataBaseHelper(MeterReadingForMissingActivity.this);
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
        txtreadingstaus = (TextView) findViewById(R.id.readingStatus);
        laymeterproperlyfixed = (LinearLayout) findViewById(R.id.meterproperplyfixed);
        laymeterproperlyfixed1 = (LinearLayout) findViewById(R.id.meterproperplyfixed1);
        etMaxdemand = (EditText) findViewById(R.id.et_max_demand);
        btnSubmit = (Button) findViewById(R.id.btn_OK);
        mruList = localDBHelper.getMissingMRU("ACT_NO", stringAcNo);
        stringPreRead = mruList.get_PREVIOUS_READ();
        stringPowFact = mruList.get_POW_FACT();
        stringRecDemd = mruList.get_REC_DEM();
        mru1 = mruList;
        if (intentFlag.equals("0")) {
            spReadingStatus.setVisibility(View.GONE);
            txtreadingstaus.setVisibility(View.VISIBLE);
            linReadingStatus.setVisibility(View.GONE);
            laymeterproperlyfixed.setVisibility(View.GONE);
            laymeterproperlyfixed1.setVisibility(View.GONE);
            stringReadingStatus = "";
        } else {
            spReadingStatus.setVisibility(View.VISIBLE);
            txtreadingstaus.setVisibility(View.GONE);
            linReadingStatus.setVisibility(View.GONE);
        }
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

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!stringReadingStatus.equals("")) {
                    if (flag) {
                        if (!etMeterReading.getText().toString().trim().equals("")) {
                            if (Float.valueOf(etMeterReading.getText().toString().trim()) <= Float.valueOf(stringPreRead)) {
                                AlertDialogForRecycle();
                            } else {
                                SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), etPowerFactor.getText().toString().trim(), etMaxdemand.getText().toString().trim(), stringIsRecycle);
                                Toast.makeText(getApplicationContext(),
                                        "Success", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Enter the value of Meter Reading",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
           //please check here
                        SaveDataORSync(stringReadingStatus, "0", "", "", stringIsRecycle);
                        Toast.makeText(getApplicationContext(), "Success",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Select Reading Status", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void AlertDialogForRecycle() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MeterReadingForMissingActivity.this);
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
                        SaveDataORSync(stringReadingStatus, etMeterReading.getText().toString().trim(), etPowerFactor.getText().toString().trim(), etMaxdemand.getText().toString().trim(), stringIsRecycle);

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

    public void SaveDataORSync(String ReadingStatus, String MeterRead, String PowerFactor, String MaxDemand, String isRecycle) {
        localDBHelper.saveMeterStatusForMissing(ReadingStatus, MeterRead, PowerFactor, MaxDemand, isRecycle, stringAcNo, "P");
        if (!Utiilties.isOnline(MeterReadingForMissingActivity.this)) {
            //Offline code
            updateButtonState(false);
            if (!runPrintReceiptSequence(false)) {
                updateButtonState(true);
            }
        } else {
            //Online Code
            mruList = localDBHelper.getMissingMRU("ACT_NO", stringAcNo);
         /*   File file = new File(Environment.getExternalStorageDirectory()
                    .getPath() + "/BEB/CropImage/" + mruList.get_OLD_BOOK_NO() + "/" + mruList.get_CON_ID() + ".jpg");
            byte[] photoByte = convertFileToByteArray(file);
            mruList.set_Meter_Photo_Byte(Base64.encodeToString(photoByte, Base64.NO_WRAP));
            new requestForBill().execute(mruList);*/
            String root=null;
            File myDir=null;
            if(android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.Q){
                root= getApplicationContext().getExternalFilesDir("/BEB/CropImage/" + mruList.get_OLD_BOOK_NO() + "/").toString();
                myDir = new File(root+"/"+mruList.get_CON_ID() + ".jpg");
            }else {
                root = Environment.getExternalStorageDirectory().getPath().toString();
                myDir = new File(root+"/BEB/CropImage/" + mruList.get_OLD_BOOK_NO() + "/" + mruList.get_CON_ID() + ".jpg");
            }
            byte[] photoByte = convertFileToByteArray(myDir);
            mruList.set_Meter_Photo_Byte(Base64.encodeToString(photoByte, Base64.NO_WRAP));
            new requestForBill().execute(mruList);

        }
    }

    private class requestForBill extends AsyncTask<MRUDetails, Void, String> {

        public requestForBill() {

        }

        private final ProgressDialog dialog = new ProgressDialog(
                MeterReadingForMissingActivity.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(
                MeterReadingForMissingActivity.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog
                    .setMessage("Please wait. \n  Request for Generate bills...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected String doInBackground(MRUDetails... param) {
            UserDetails user = CommonPref.getUserDetails(getApplicationContext());
            String res1 = WebServiceHelper.BillRequestForMissing(param[0], user);

            return res1;
        }

        @Override
        protected void onPostExecute(String result) {

            if (this.dialog.isShowing()) {
                this.dialog.dismiss();

                if (result == null) {

                    alertDialog.setTitle("Failed!!");
                    alertDialog.setMessage("Some Error in Network ");
                    alertDialog.show();
                    updateButtonState(false);
                    if (!runPrintReceiptSequence(false)) {
                        updateButtonState(true);
                    }

                } else {

                    if (result.toString().trim().equalsIgnoreCase("Success")) {

                        try {
                            updateButtonState(false);
                            if (!runPrintReceiptSequence(true)) {
                                updateButtonState(true);
                            }

                        } catch (Exception ex) {
                            Utiilties.writeIntoLog(Log.getStackTraceString(ex));
                            Toast.makeText(MeterReadingForMissingActivity.this, " Error" + ex.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else if (result.toString().trim().equalsIgnoreCase("Request Accepted For MRU Change") || result.toString().trim().equalsIgnoreCase("Already Requested For MRU Change") || result.toString().trim().equalsIgnoreCase("Invalid Credentials")) {

                        alertDialog.setTitle("Success!!");
                        alertDialog.setMessage(result.toString().trim());
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
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            e.printStackTrace();
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

                dispPrinterWarnings(status);

                updateButtonState(true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
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
            textData.append("SECTION NAME  :       " + mru1.get_SECTION_NAME().trim() + "\n");
            textData.append("CON ID        :       " + mru1.get_CON_ID().trim() + "\n");
            textData.append("MRU           :       " + mru1.get_OLD_BOOK_NO().trim() + "\n");
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
            textData.append("CURR Rdg Dt   :       " + mru1.get_Read_Date().trim() + "\n");
            textData.append("CURR Rdg (Kwh):       " + meterReading + "\n");
            textData.append("PREV Rdg (Kwh):       " + mru1.get_PREVIOUS_READ().trim() + "\n");
            textData.append("The Bill could not be generated now due to different Book Number, \nPlease collect your Bill from " + header + " \nWebsite after 2 Days. \n");
            textData.append("METER READER  :       " + CommonPref.getUserDetails(getApplicationContext()).get_UserID().trim() + "\n");
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
                // Do nothing
                Utiilties.writeIntoLog(Log.getStackTraceString(ex));
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
                // Do nothing
                Utiilties.writeIntoLog(Log.getStackTraceString(ex));
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

        if (isBeginTransaction == false) {
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
            textData.append("SECTION NAME  :       " + mru1.get_SECTION_NAME().trim() + "\n");
            textData.append("CON ID        :       " + mru1.get_CON_ID().trim() + "\n");
            textData.append("MRU           :       " + mru1.get_OLD_BOOK_NO().trim() + "\n");
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
            textData.append("CURR Rdg Dt   :       " + mru1.get_Read_Date().trim() + "\n");
            textData.append("CURR Rdg (Kwh):       " + meterReading + "\n");
            textData.append("PREV Rdg (Kwh):       " + mru1.get_PREVIOUS_READ().trim() + "\n");
            textData.append("The Bill could not be generated now due to network issue. \n");
            //textData.append("CODE          :        7 \n");
            textData.append("Message       :        NETWORK ISSUE \n");
            textData.append("METER READER  :       " + CommonPref.getUserDetails(getApplicationContext()).get_UserID().trim() + "\n");
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
            //Display alert message when back button has been pressed
            Intent i = new Intent(MeterReadingForMissingActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
