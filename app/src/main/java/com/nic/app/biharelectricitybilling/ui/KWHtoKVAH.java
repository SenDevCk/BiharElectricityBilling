package com.nic.app.biharelectricitybilling.ui;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.nic.app.biharelectricitybilling.R;

public class KWHtoKVAH extends AppCompatActivity implements View.OnClickListener {

    EditText edit_current_kwh,edit_current_kvah;
    CheckBox check_skip;
    Button btn_skip,btn_save;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kwhto_kvah);
        edit_current_kwh=findViewById(R.id.edit_current_kwh);
        edit_current_kvah=findViewById(R.id.edit_current_kvah);
        btn_skip=findViewById(R.id.btn_skip);
        btn_save=findViewById(R.id.btn_save);
        check_skip=findViewById(R.id.check_skip);
        btn_save.setOnClickListener(this);
        check_skip.setOnClickListener(this);
        check_skip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                btn_skip.setEnabled(true);
                btn_save.setEnabled(false);
                edit_current_kwh.setText("");
                edit_current_kvah.setText("");
                edit_current_kwh.setEnabled(false);
                edit_current_kvah.setEnabled(false);
            }else{
                btn_skip.setEnabled(false);
                btn_save.setEnabled(true);
                edit_current_kwh.setEnabled(true);
                edit_current_kvah.setEnabled(true);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(KWHtoKVAH.this,ConsumerActivity4A11new.class);
        if (v.getId()==R.id.btn_skip){
            if (check_skip.isChecked()) {
                intent.putExtra("kwh",edit_current_kwh.getText().toString().trim());
                intent.putExtra("kvah",edit_current_kvah.getText().toString().trim());
            }else{
                Toast.makeText(this, "Please check the above detail", Toast.LENGTH_SHORT).show();
            }
        }else if (v.getId()==R.id.btn_save){
            if (edit_current_kwh.getText().toString().trim().isEmpty()||edit_current_kwh.getText().toString().trim().isBlank()){
                Toast.makeText(this, "Enter KWH", Toast.LENGTH_SHORT).show();
            }else  if (edit_current_kvah.getText().toString().trim().isEmpty()||edit_current_kvah.getText().toString().trim().isBlank()){
                Toast.makeText(this, "Enter KVAH", Toast.LENGTH_SHORT).show();
            }
            intent.putExtra("kwh",edit_current_kwh.getText().toString().trim());
            intent.putExtra("kvah",edit_current_kvah.getText().toString().trim());
        }
        setResult(RESULT_OK,intent);
        finish();
    }
}