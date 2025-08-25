package com.nic.app.biharelectricitybilling.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.room.Room;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.AppDatabase;
import com.nic.app.biharelectricitybilling.db.DatabaseClientSurvey;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.Survey_Data;
import com.nic.app.biharelectricitybilling.entity.Survey_Data_Details;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.Utiilties;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SurveyDeatils extends Activity {
    TextView txt_actno, txt_cname;
    RadioGroup tdGroup, lowgroup, mdgroup, metermatchedgroup, rgunpaid, rgtraced, rgpropermru,rgmissingmru;
    RadioButton tdYes, tdno, lowyes, lowno, mdyes, mdno, matchedyes, matchedno, unpaidyes, unpaidno, tracedyes, tracedno, mruyes, mruno,propyes,prpno;
    Spinner tdspinner, lowconsumptionsp, mdspinner;
    LinearLayout laynewconid, laynewmeterno, laydconid, laylowmtrreading, laylowmtrimage, laymeterread, layoutmetermatched, laymeterno, laymobno, laytracedornot, laycano, laymtrno;
    private ActionBar actionBar;
    String isconsumerTd = "N", islowconsumption = "N", IsmeterDefactive = "N", Ismtrnomatchedwithbill = "N", IsUnpaidConsumer = "N", istraced = "N", Isconsumermissing = "N",Isconsumerpropmru = "N";
    String act_no = "NA", cname = "NA", sub_div_id = "NA",Meter_no="NA";
    String Td_spinner_value = "- Select  -", low_spinner_value = "- Select  -", MD_spinner_value = "- Select  -";
    EditText edt_td_con_id, edt_td_mtr_no, edt_duplicate_conid, edt_low_met_read, edt_md_met_read, edt_md_met_no, edt_unpaid_mobile_no, edt_proper_ca_no, edt_proper_met_no,edt_max_deemand,edt_power_factor;
    Button btn_save;
    Survey_Data_Details data_details = null;
    ImageView meterimg;
    private static final int CAMERA_REQUEST = 2000;
    Bitmap mtrbitmap=null;
    byte[] img=null;
    Runnable r=null;
    Runnable r1=null;
    long insertedrow=0;
    AppDatabase db=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_details);
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'> Survey Data Details </font>"));
        Bundle bundle = getIntent().getExtras().getBundle("bundle");
        act_no = bundle.getString("actno");
        cname = bundle.getString("name");
        sub_div_id = bundle.getString("subdivid");
        Meter_no = bundle.getString("meter_no");
        db =DatabaseClientSurvey.getInstance(getApplicationContext()).getAppDatabase();
        /*  Toast.makeText(this, ""+sub_div_id, Toast.LENGTH_SHORT).show();*/
        txt_actno = (TextView) findViewById(R.id.txtaccountno);
        txt_cname = (TextView) findViewById(R.id.txtcname);
        txt_actno.setText("" + act_no);
        txt_cname.setText("" + cname);
        tdGroup = (RadioGroup) findViewById(R.id.rgTd);
        tdYes = (RadioButton) findViewById(R.id.tdyes);
        tdno = (RadioButton) findViewById(R.id.tdno);
        tdspinner = (Spinner) findViewById(R.id.sp_td);
        laynewconid = (LinearLayout) findViewById(R.id.laynewconid);
        laynewmeterno = (LinearLayout) findViewById(R.id.laynewmeterno);
        laydconid = (LinearLayout) findViewById(R.id.laydconid);
        lowgroup = (RadioGroup) findViewById(R.id.rglow);
        mdgroup = (RadioGroup) findViewById(R.id.rgMd);
        lowyes = (RadioButton) findViewById(R.id.rblowyes);
        lowno = (RadioButton) findViewById(R.id.rblowno);
        lowconsumptionsp = (Spinner) findViewById(R.id.sp_low_consumption);
        laylowmtrreading = (LinearLayout) findViewById(R.id.laymtrreding);
        laylowmtrimage = (LinearLayout) findViewById(R.id.laymeterimg);
        mdgroup = (RadioGroup) findViewById(R.id.rgMd);
        mdyes = (RadioButton) findViewById(R.id.mdyes);
        mdno = (RadioButton) findViewById(R.id.mdno);
        mdspinner = (Spinner) findViewById(R.id.sp_Meter_Defactive);
        laymeterread = (LinearLayout) findViewById(R.id.laymeterread);
        layoutmetermatched = (LinearLayout) findViewById(R.id.laymeternomatched);
        laymeterno = (LinearLayout) findViewById(R.id.laynewmtrno);
        metermatchedgroup = (RadioGroup) findViewById(R.id.rgmetermatched);
        matchedyes = (RadioButton) findViewById(R.id.metermatchedyes);
        matchedno = (RadioButton) findViewById(R.id.metermatchedno);
        rgunpaid = (RadioGroup) findViewById(R.id.rgisconunpaid);
        unpaidyes = (RadioButton) findViewById(R.id.unpaidyes);
        unpaidno = (RadioButton) findViewById(R.id.unpaidno);
        rgtraced = (RadioGroup) findViewById(R.id.rgtraced);
        tracedyes = (RadioButton) findViewById(R.id.tracedyes);
        tracedno = (RadioButton) findViewById(R.id.tracedno);
        laymobno = (LinearLayout) findViewById(R.id.laymob);

        laytracedornot = (LinearLayout) findViewById(R.id.laytraced);

        rgmissingmru = (RadioGroup) findViewById(R.id.rgmissingmru);
        mruyes = (RadioButton) findViewById(R.id.missingyes);
        mruno = (RadioButton) findViewById(R.id.missingno);

        rgpropermru = (RadioGroup) findViewById(R.id.rgpropermru1);
        propyes = (RadioButton) findViewById(R.id.prpmruyes);
        prpno = (RadioButton) findViewById(R.id.propmruno);

        laycano = (LinearLayout) findViewById(R.id.laycano);
        laymtrno = (LinearLayout) findViewById(R.id.laymetrno);

        btn_save = (Button) findViewById(R.id.btn_continue);
        edt_td_con_id = (EditText) findViewById(R.id.edt_td_consumer_no);
        edt_td_mtr_no = (EditText) findViewById(R.id.edt_td_meter_no);
        edt_duplicate_conid = (EditText) findViewById(R.id.edt_td_duplicate_con_id);
        edt_low_met_read = (EditText) findViewById(R.id.edt_low_mtr_read);

        edt_md_met_read = (EditText) findViewById(R.id.edt_md_met_read);
        edt_md_met_no = (EditText) findViewById(R.id.edt_md_met_no);
        edt_unpaid_mobile_no = (EditText) findViewById(R.id.edt_unpaid_mobile_no);

        edt_proper_ca_no = (EditText) findViewById(R.id.edt_proper_mru_cano);
        edt_proper_met_no = (EditText) findViewById(R.id.edt_proper_met_no);

        edt_max_deemand = (EditText) findViewById(R.id.edt_maxdeemand);
        edt_power_factor = (EditText) findViewById(R.id.edt_powerfactor);

        meterimg=(ImageView)findViewById(R.id.mtr_image);

        tdGroup.setOnCheckedChangeListener((group, checkedId) -> {

            RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
            //  Toast.makeText(this, "" + checkedRadioButton, Toast.LENGTH_SHORT).show();
            if (checkedId == tdYes.getId()) {
                isconsumerTd = "Y";
                tdspinner.setVisibility(View.VISIBLE);
            } else if (checkedId == tdno.getId()) {
                isconsumerTd = "N";
                tdspinner.setVisibility(View.GONE);
                tdspinner.setSelection(0);
                Td_spinner_value = "- Select  -";
                laynewconid.setVisibility(View.GONE);
                laynewmeterno.setVisibility(View.GONE);
                laydconid.setVisibility(View.GONE);

            }
        });
        tdspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //
                // tdselectposition = position;
                Td_spinner_value = parentView.getItemAtPosition(position).toString();
            //    Toast.makeText(SurveyDeatils.this, "" + Td_spinner_value, Toast.LENGTH_LONG).show();
                if (position == 3) {
                     laynewconid.setVisibility(View.VISIBLE);
                     laynewmeterno.setVisibility(View.VISIBLE);
                     laydconid.setVisibility(View.GONE);
                }
                if (position == 6) {
                    laydconid.setVisibility(View.VISIBLE);
                    laynewconid.setVisibility(View.GONE);
                    laynewmeterno.setVisibility(View.GONE);
                }
                if (position != 3) {
                    laynewconid.setVisibility(View.GONE);
                    laynewmeterno.setVisibility(View.GONE);
                }
                if (position != 6) {
                    laydconid.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                laynewconid.setVisibility(View.GONE);
                laynewmeterno.setVisibility(View.GONE);
                laydconid.setVisibility(View.GONE);
            }
        });
        lowgroup.setOnCheckedChangeListener((group, checkedId) -> {

            RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
            //  Toast.makeText(this, "" + checkedRadioButton, Toast.LENGTH_SHORT).show();
            if (checkedId == lowyes.getId()) {
                islowconsumption = "Y";
                lowconsumptionsp.setVisibility(View.VISIBLE);
            } else if (checkedId == lowno.getId()) {
                islowconsumption = "N";

                lowconsumptionsp.setVisibility(View.GONE);
                laylowmtrreading.setVisibility(View.GONE);
                laylowmtrimage.setVisibility(View.GONE);
                lowconsumptionsp.setSelection(0);
                low_spinner_value = "- Select  -";
            }
        });
        lowconsumptionsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //  lowselectposition=position;
                low_spinner_value = parentView.getItemAtPosition(position).toString();
                if (position == 1) {
                    laylowmtrreading.setVisibility(View.VISIBLE);
                    laylowmtrimage.setVisibility(View.VISIBLE);
                } else {
                    laylowmtrreading.setVisibility(View.GONE);
                    laylowmtrimage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                laylowmtrreading.setVisibility(View.GONE);
                laylowmtrimage.setVisibility(View.GONE);
            }

        });
        mdgroup.setOnCheckedChangeListener((group, checkedId) -> {

            RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);

            if (checkedId == mdyes.getId()) {
                mdspinner.setVisibility(View.VISIBLE);
                IsmeterDefactive = "Y";
            } else if (checkedId == mdno.getId()) {
                IsmeterDefactive = "N";
                mdspinner.setVisibility(View.GONE);
                laymeterread.setVisibility(View.GONE);
                layoutmetermatched.setVisibility(View.GONE);
                laymeterno.setVisibility(View.GONE);
                mdspinner.setSelection(0);

                MD_spinner_value = "- Select  -";
            }
        });
        mdspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                MD_spinner_value = parentView.getItemAtPosition(position).toString();
                if (position == 1) {
                    laymeterread.setVisibility(View.VISIBLE);
                    layoutmetermatched.setVisibility(View.VISIBLE);
                    //  laymeterno.setVisibility(View.VISIBLE);
                } else {
                    laymeterread.setVisibility(View.GONE);
                    layoutmetermatched.setVisibility(View.GONE);
                    laymeterno.setVisibility(View.GONE);
                    matchedno.setChecked(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                laymeterread.setVisibility(View.GONE);
                layoutmetermatched.setVisibility(View.GONE);
                laymeterno.setVisibility(View.GONE);
                matchedno.setChecked(true);
            }

        });
        metermatchedgroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
            if (checkedId == matchedyes.getId()) {
                Ismtrnomatchedwithbill = "Y";
                laymeterno.setVisibility(View.VISIBLE);
            } else if (checkedId == matchedno.getId()) {
                Ismtrnomatchedwithbill = "N";
                laymeterno.setVisibility(View.GONE);
            }
        });
        rgunpaid.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
            if (checkedId == unpaidyes.getId()) {
                IsUnpaidConsumer = "Y";
                laymobno.setVisibility(View.VISIBLE);
                laytracedornot.setVisibility(View.VISIBLE);
            } else if (checkedId == unpaidno.getId()) {
                IsUnpaidConsumer = "N";
                laymobno.setVisibility(View.GONE);
                laytracedornot.setVisibility(View.GONE);
            }
        });
        rgtraced.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
            if (checkedId == tracedyes.getId()) {
                istraced = "Y";
            } else if (checkedId == tracedno.getId()) {
                istraced = "N";
            }
        });
        rgmissingmru.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
            if (checkedId == mruyes.getId()) {
                Isconsumermissing = "Y";
                laycano.setVisibility(View.VISIBLE);
                laymtrno.setVisibility(View.VISIBLE);
            } else if (checkedId == mruno.getId()) {
                Isconsumermissing = "N";
                laycano.setVisibility(View.GONE);
                laymtrno.setVisibility(View.GONE);
            }
        });

        rgpropermru.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
            if (checkedId == mruyes.getId()) {
                Isconsumerpropmru = "Y";

            } else if (checkedId == mruno.getId()) {
                Isconsumerpropmru = "N";

            }
        });
        meterimg.setOnClickListener(view -> {
           // Toast.makeText(this, "take an image", Toast.LENGTH_SHORT).show();
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });
        btn_save.setOnClickListener(view -> {
            data_details = new Survey_Data_Details();
            data_details.setACT_NO(act_no);
            data_details.setSUB_DIV_ID(sub_div_id);
            data_details.setIs_consumer_TD(isconsumerTd);
            data_details.setMeter_No(Meter_no);
            data_details.setTD_Spinner_string(Td_spinner_value.equalsIgnoreCase("- Select  -")?"NA":Td_spinner_value);
            if(!Td_spinner_value.equalsIgnoreCase("Availing Electricity through NSC")){
                edt_td_con_id.setText("");
                edt_td_mtr_no.setText("");
            }if(!Td_spinner_value.equalsIgnoreCase("TD Cases due to duplicate Energy bill")){
                edt_duplicate_conid.setText("");
            }
            data_details.setTD_New_Consumer_No((edt_td_con_id.getText().toString().trim().length()>0)?edt_td_con_id.getText().toString().trim():"NA");
            data_details.setTD_New_meter_No((edt_td_mtr_no.getText().toString().trim().length()>0)?edt_td_mtr_no.getText().toString().trim():"NA");
            data_details.setTD_Duplicate_Consumer_no(edt_duplicate_conid.getText().toString().trim().length()>0?edt_duplicate_conid.getText().toString().trim():"NA");
            data_details.setIs_Low_Consumption(islowconsumption);
            data_details.setIs_Low_Spiner_value(low_spinner_value.equalsIgnoreCase("- Select  -")?"NA":low_spinner_value);
            if(!low_spinner_value.equalsIgnoreCase("Stored readings found")){
                edt_low_met_read.setText("");
                img=null;
            }
            data_details.setIs_Low_met_read(edt_low_met_read.getText().toString().trim().length()>0?edt_low_met_read.getText().toString().trim():"NA");
            data_details.setIs_Low_met_img(img);
            Log.e("image",""+img);
            data_details.setIs_MD_Found(IsmeterDefactive);
            data_details.setMD_Spinner_value(MD_spinner_value.equalsIgnoreCase("- Select  -")?"NA":MD_spinner_value);
            if(!MD_spinner_value.equalsIgnoreCase("Is Meter found Running")){
                edt_md_met_read.setText("");
                Ismtrnomatchedwithbill="N";
            }
            if(Ismtrnomatchedwithbill.equals("N")){
                edt_md_met_no.setText("");
            }
            data_details.setMD_met_read(edt_md_met_read.getText().toString().trim().length()>0?edt_md_met_read.getText().toString():"NA");
            data_details.setIs_mtr_nomatched_with_bill(Ismtrnomatchedwithbill);
            data_details.setMd_meter_no(edt_md_met_no.getText().toString().trim().length()>0?edt_md_met_no.getText().toString().trim():"NA");
            data_details.setIs_unpaid_consumer(IsUnpaidConsumer);
            if(IsUnpaidConsumer.equals("N")){
                edt_unpaid_mobile_no.setText("");
                istraced="N";
            }
            data_details.setUnpaid_mobile_no(edt_unpaid_mobile_no.getText().toString().trim().length()>0?edt_unpaid_mobile_no.getText().toString().trim():"NA");
            data_details.setIs_unpaid_traced(istraced);
            data_details.setIs_consumer_proper_mru(Isconsumerpropmru);
            if(Isconsumermissing.equals("N")){
                edt_proper_ca_no.setText("");
                edt_proper_met_no.setText("");
            }
            data_details.setIs_consumer_missing(Isconsumermissing);
            data_details.setProper_mru_ca_no(edt_proper_ca_no.getText().toString().trim().length()>0?edt_proper_ca_no.getText().toString().trim():"NA");
            data_details.setProper_mru_met_no(edt_proper_met_no.getText().toString().trim().length()>0?edt_proper_met_no.getText().toString().trim():"NA");
            data_details.setMax_deemand(edt_max_deemand.getText().toString().trim().length()>0?edt_max_deemand.getText().toString().trim():"NA");
            data_details.setPower_factor(edt_power_factor.getText().toString().trim().length()>0?edt_power_factor.getText().toString().trim():"NA");
            Log.e("in surveydetails", data_details.toString());
            if (Utiilties.isOnline(SurveyDeatils.this)) {
                new UploadDetails().execute(data_details);
            } else {
                ThreadInsertData t=new ThreadInsertData();
                t.start();
                if (insertedrow > 0) {
                    Toast.makeText(this, "Data Saved Successfully" + insertedrow, Toast.LENGTH_SHORT).show();
                }
            }

               finish();
        });
    }


    private class UploadDetails extends AsyncTask<Survey_Data_Details, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(
                SurveyDeatils.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SurveyDeatils.this).create();
        @Override
        protected String doInBackground(Survey_Data_Details... data) {
            return WebServiceHelper.updateSurveydeatils(data_details, CommonPref.getUserDetails(SurveyDeatils.this));
        }
        @Override
        protected void onPostExecute(String result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                if (result != null) {
                    if (result.contains("SUCCESS")) {
                         Threaddeleterecord t1=new Threaddeleterecord();
                        t1.start();
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    } else if (result.contains("UPDATED")) {
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                        Threaddeleterecord t1=new Threaddeleterecord();
                        t1.start();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error in Network"+result, Toast.LENGTH_LONG).show();
                    ThreadInsertData t1=new ThreadInsertData();
                    t1.start();
                }
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Uploading data please wait");
            this.dialog.show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            mtrbitmap = (Bitmap) data.getExtras().get("data");
            meterimg.setImageBitmap(mtrbitmap);
            mtrbitmap = Bitmap.createScaledBitmap(mtrbitmap, 350, 220, true);
            img=bitmaptobyte(mtrbitmap);
            Toast.makeText(this, ""+img, Toast.LENGTH_SHORT).show();
        }
    }
    public byte[] bitmaptobyte(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bmp.recycle();
        return byteArray;
    }
    class ThreadInsertData extends Thread{
        @Override
        public void run() {
            insertedrow= db.SurveyDataDao()
                    .insert(data_details);
            if(insertedrow>0){
                Threaddeleterecord threaddelete=new Threaddeleterecord();
                threaddelete.start();
            }

        }
    }
    class Threaddeleterecord extends Thread {
        @Override
        public void run() {
                    db.SurveyDao()
                    .deleteByact_no(data_details.getACT_NO());
        }
    }

}

