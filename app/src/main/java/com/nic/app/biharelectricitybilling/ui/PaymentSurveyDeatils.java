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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.AppDatabase;
import com.nic.app.biharelectricitybilling.db.DatabaseClientSurvey;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.Payment_Data_Details;
import com.nic.app.biharelectricitybilling.entity.Survey_Data_Details;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.Utiilties;
import java.io.ByteArrayOutputStream;
public class PaymentSurveyDeatils extends Activity {
    TextView txt_actno, txt_cname;
    RadioGroup tdGroup, lowgroup, mdgroup;
    RadioButton tdYes, tdno, lowyes, lowno, mdyes, mdno;
    Spinner tdspinner, lowconsumptionsp, mdspinner;
    private ActionBar actionBar;
    String isconsumerTd = "N", islowconsumption = "N", IsmeterDefactive = "N";
    String act_no = "NA", cname = "NA", sub_div_id = "NA", Meter_no = "NA";
    String Td_spinner_value = "- Select  -",Td_spinner_value1 = "- Select  -", low_spinner_value = "- Select  -", MD_spinner_value = "- Select  -";
    Button btn_save;
    Payment_Data_Details data_details = null;
    private static final int CAMERA_REQUEST = 2000;
    Runnable r = null;
    Runnable r1 = null;
    long insertedrow = 0;
    AppDatabase db = null;
    String flag="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_remarks);
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'> Survey Data Details </font>"));
        Bundle bundle = getIntent().getExtras().getBundle("bundle");
        act_no = bundle.getString("actno");
        cname = bundle.getString("name");
        sub_div_id = bundle.getString("subdivid");

        db = DatabaseClientSurvey.getInstance(getApplicationContext()).getAppDatabase();
        /*  Toast.makeText(this, ""+sub_div_id, Toast.LENGTH_SHORT).show();*/
        txt_actno = (TextView) findViewById(R.id.txtaccountno);
        txt_cname = (TextView) findViewById(R.id.txtcname);
        txt_actno.setText("" + act_no);
        txt_cname.setText("" + cname);
        tdGroup = (RadioGroup) findViewById(R.id.rgTd);
        tdYes = (RadioButton) findViewById(R.id.tdyes);
        tdno = (RadioButton) findViewById(R.id.tdno);
        tdspinner = (Spinner) findViewById(R.id.sp_td);
        lowgroup = (RadioGroup) findViewById(R.id.rglow);
        lowyes = (RadioButton) findViewById(R.id.rblowyes);
        lowno = (RadioButton) findViewById(R.id.rblowno);
        lowconsumptionsp = (Spinner) findViewById(R.id.sp_low_consumption);
        mdgroup = (RadioGroup) findViewById(R.id.rgMd);
        mdyes = (RadioButton) findViewById(R.id.mdyes);
        mdno = (RadioButton) findViewById(R.id.mdno);
        mdspinner = (Spinner) findViewById(R.id.sp_Meter_Defactive);

        btn_save = (Button) findViewById(R.id.btn_continue);


        tdGroup.setOnCheckedChangeListener((group, checkedId) -> {

            RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
            //  Toast.makeText(this, "" + checkedRadioButton, Toast.LENGTH_SHORT).show();
            if (checkedId == tdYes.getId()) {
                isconsumerTd = "Y";
                tdspinner.setVisibility(View.VISIBLE);
                lowno.setChecked(true);
                mdno.setChecked(true);

                mdspinner.setSelection(0);
                MD_spinner_value = "- Select  -";

                lowconsumptionsp.setSelection(0);
                low_spinner_value = "- Select  -";

            } else if (checkedId == tdno.getId()) {
                isconsumerTd = "N";
                tdspinner.setVisibility(View.GONE);
                tdspinner.setSelection(0);
                Td_spinner_value = "- Select  -";


            }
        });
        tdspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Td_spinner_value1 = parentView.getItemAtPosition(position).toString();
                Td_spinner_value="1"+position;
                if(position>0){
                    flag="1";
                }else{
                    flag="0";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                flag="2";
            }
        });
        lowgroup.setOnCheckedChangeListener((group, checkedId) -> {

            RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
            if (checkedId == lowyes.getId()) {
                islowconsumption = "Y";
                lowconsumptionsp.setVisibility(View.VISIBLE);
                tdno.setChecked(true);
                mdno.setChecked(true);
                mdspinner.setSelection(0);
                MD_spinner_value = "- Select  -";
                tdspinner.setSelection(0);
                Td_spinner_value = "- Select  -";
            } else if (checkedId == lowno.getId()) {
                islowconsumption = "N";
                lowconsumptionsp.setVisibility(View.GONE);
                lowconsumptionsp.setSelection(0);
                low_spinner_value = "- Select  -";
            }
        });
        lowconsumptionsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                low_spinner_value = parentView.getItemAtPosition(position).toString();
                Td_spinner_value="2"+position;
                if(position>0){
                    flag="1";
                }else{
                    flag="0";
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                flag="2";
            }

        });
        mdgroup.setOnCheckedChangeListener((group, checkedId) -> {

            RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);

            if (checkedId == mdyes.getId()) {
                mdspinner.setVisibility(View.VISIBLE);
                IsmeterDefactive = "Y";
                tdno.setChecked(true);
                lowno.setChecked(true);

                lowconsumptionsp.setSelection(0);
                low_spinner_value = "- Select  -";

                tdspinner.setSelection(0);
                Td_spinner_value = "- Select  -";
            } else if (checkedId == mdno.getId()) {
                IsmeterDefactive = "N";
                mdspinner.setVisibility(View.GONE);
                mdspinner.setSelection(0);
                MD_spinner_value = "- Select  -";
            }
        });
        mdspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                MD_spinner_value = parentView.getItemAtPosition(position).toString();
                Td_spinner_value="3"+position;
                if(position>0){
                    flag="1";
                }else{
                    flag="0";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                flag="2";
            }
        });

        btn_save.setOnClickListener(view -> {
            data_details = new Payment_Data_Details();
            data_details.setACT_NO(act_no);
            data_details.setSUB_DIV_ID(sub_div_id);
            data_details.setData(Td_spinner_value);
           if(flag.equalsIgnoreCase("0") || flag.equalsIgnoreCase("2")){
               Toast.makeText(this, "Please Select Reason First", Toast.LENGTH_SHORT).show();
           }else {
               if (Utiilties.isOnline(PaymentSurveyDeatils.this)) {
              //     Toast.makeText(this, act_no+","+sub_div_id+","+Td_spinner_value, Toast.LENGTH_SHORT).show();
                   new UploadDetails().execute(data_details);
               } else {
                   ThreadInsertData t = new ThreadInsertData();
                   t.start();
                   if (insertedrow > 0) {
                       Toast.makeText(this, "Data Saved Successfully" + insertedrow, Toast.LENGTH_SHORT).show();
                   }
               }
           }


        });


    }

    private class UploadDetails extends AsyncTask<Payment_Data_Details, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(
                PaymentSurveyDeatils.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                PaymentSurveyDeatils.this).create();

        @Override
        protected String doInBackground(Payment_Data_Details... data) {
            return WebServiceHelper.updatePaymentdeatils(data_details, CommonPref.getUserDetails(PaymentSurveyDeatils.this));
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(PaymentSurveyDeatils.this, result, Toast.LENGTH_SHORT).show();
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                if (result != null) {

                    if (result.contains("SUCCESS")) {
                        Threaddeleterecord t1 = new Threaddeleterecord();
                        t1.start();
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    } else if (result.contains("Update")) {
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                        Threaddeleterecord t1 = new Threaddeleterecord();
                        t1.start();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error in Network" + result, Toast.LENGTH_LONG).show();
                    ThreadInsertData t1 = new ThreadInsertData();
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

    public byte[] bitmaptobyte(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bmp.recycle();
        return byteArray;
    }

    class ThreadInsertData extends Thread {
        @Override
        public void run() {
            insertedrow = db.PaymentDao()
                    .insert(data_details);
            if (insertedrow > 0) {
                Threaddeleterecord threaddelete = new Threaddeleterecord();
                threaddelete.start();
            }
        }
    }
    class Threaddeleterecord extends Thread {
        @Override
        public void run() {
                    db.PaymentDao()
                    .deleteByact_no(data_details.getACT_NO());
            Intent intent = new Intent(PaymentSurveyDeatils.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    @Override
    public void onBackPressed() {
    //   super.onBackPressed();
    }
}

