package com.nic.app.biharelectricitybilling.entity;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.ibm.icu.impl.UResource;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Arrays;
import java.util.Hashtable;
@Entity(tableName = "Survey_Data_Details")
public class Survey_Data_Details implements KvmSerializable {
    public static Class<Survey_Data_Details> BILL_CLASS = Survey_Data_Details.class;
    public String MyDefaultValuedCol = "defaultString";
    @PrimaryKey
    @NonNull
    private String ACT_NO;
    private String SUB_DIV_ID;
    private String Meter_No;
    private String Is_consumer_TD;
    private String TD_Spinner_string;
    private String TD_New_Consumer_No;
    private String TD_New_meter_No;
    private String TD_Duplicate_Consumer_no;
    private String Is_Low_Consumption;
    private String Is_Low_Spiner_value;
    private String Is_Low_met_read;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] Is_Low_met_img;
    private String Is_MD_Found;
    private String MD_Spinner_value;
    private String MD_met_read;
    private String is_mtr_nomatched_with_bill;
    private String Md_meter_no;
    private String Is_unpaid_consumer;
    private String unpaid_mobile_no;
    private String Is_unpaid_traced;
    private String Is_consumer_proper_mru;
    private String Proper_mru_ca_no;
    private String Proper_mru_met_no;
    private String Is_consumer_missing;

    private String Max_deemand;
    private String power_factor;

    private String uploaded;
    @Override
    public Object getProperty(int i) {
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 0;
    }

    @Override
    public void setProperty(int i, Object o) {

    }

    public String getMeter_No() {
        return Meter_No;
    }

    public void setMeter_No(String meter_No) {
        Meter_No = meter_No;
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {

    }

    @NonNull
    public String getACT_NO() {
        return ACT_NO;
    }

    public void setACT_NO(@NonNull String ACT_NO) {
        this.ACT_NO = ACT_NO;
    }


    public String getSUB_DIV_ID() {
        return SUB_DIV_ID;
    }

    public void setSUB_DIV_ID(String SUB_DIV_ID) {
        this.SUB_DIV_ID = SUB_DIV_ID;
    }

    public String getIs_consumer_TD() {
        return Is_consumer_TD;
    }

    public void setIs_consumer_TD(String is_consumer_TD) {
        Is_consumer_TD = is_consumer_TD;
    }

    public String getTD_Spinner_string() {
        return TD_Spinner_string;
    }

    public void setTD_Spinner_string(String TD_Spinner_string) {
        this.TD_Spinner_string = TD_Spinner_string;
    }

    public String getTD_New_Consumer_No() {
        return TD_New_Consumer_No;
    }

    public void setTD_New_Consumer_No(String TD_New_Consumer_No) {
        this.TD_New_Consumer_No = TD_New_Consumer_No;
    }

    public String getTD_New_meter_No() {
        return TD_New_meter_No;
    }

    public void setTD_New_meter_No(String TD_New_meter_No) {
        this.TD_New_meter_No = TD_New_meter_No;
    }

    public String getTD_Duplicate_Consumer_no() {
        return TD_Duplicate_Consumer_no;
    }

    public void setTD_Duplicate_Consumer_no(String TD_Duplicate_Consumer_no) {
        this.TD_Duplicate_Consumer_no = TD_Duplicate_Consumer_no;
    }

    public String getIs_Low_Consumption() {
        return Is_Low_Consumption;
    }

    public void setIs_Low_Consumption(String is_Low_Consumption) {
        Is_Low_Consumption = is_Low_Consumption;
    }

    public String getIs_Low_Spiner_value() {
        return Is_Low_Spiner_value;
    }

    public void setIs_Low_Spiner_value(String is_Low_Spiner_value) {
        Is_Low_Spiner_value = is_Low_Spiner_value;
    }

    public String getIs_Low_met_read() {
        return Is_Low_met_read;
    }

    public void setIs_Low_met_read(String is_Low_met_read) {
        Is_Low_met_read = is_Low_met_read;
    }

    public byte[] getIs_Low_met_img() {
        return Is_Low_met_img;
    }

    public void setIs_Low_met_img(byte[] is_Low_met_img) {
        Is_Low_met_img = is_Low_met_img;
    }

    public String getIs_MD_Found() {
        return Is_MD_Found;
    }

    public void setIs_MD_Found(String is_MD_Found) {
        Is_MD_Found = is_MD_Found;
    }

    public String getMD_Spinner_value() {
        return MD_Spinner_value;
    }

    public void setMD_Spinner_value(String MD_Spinner_value) {
        this.MD_Spinner_value = MD_Spinner_value;
    }

    public String getMD_met_read() {
        return MD_met_read;
    }

    public void setMD_met_read(String MD_met_read) {
        this.MD_met_read = MD_met_read;
    }

    public String getIs_mtr_nomatched_with_bill() {
        return is_mtr_nomatched_with_bill;
    }

    public void setIs_mtr_nomatched_with_bill(String is_mtr_nomatched_with_bill) {
        this.is_mtr_nomatched_with_bill = is_mtr_nomatched_with_bill;
    }

    public String getMd_meter_no() {
        return Md_meter_no;
    }

    public void setMd_meter_no(String md_meter_no) {
        Md_meter_no = md_meter_no;
    }

    public String getIs_unpaid_consumer() {
        return Is_unpaid_consumer;
    }

    public void setIs_unpaid_consumer(String is_unpaid_consumer) {
        Is_unpaid_consumer = is_unpaid_consumer;
    }

    public String getUnpaid_mobile_no() {
        return unpaid_mobile_no;
    }

    public void setUnpaid_mobile_no(String unpaid_mobile_no) {
        this.unpaid_mobile_no = unpaid_mobile_no;
    }

    public String getIs_unpaid_traced() {
        return Is_unpaid_traced;
    }

    public void setIs_unpaid_traced(String is_unpaid_traced) {
        Is_unpaid_traced = is_unpaid_traced;
    }

    public String getIs_consumer_proper_mru() {
        return Is_consumer_proper_mru;
    }

    public void setIs_consumer_proper_mru(String is_consumer_proper_mru) {
        Is_consumer_proper_mru = is_consumer_proper_mru;
    }

    public String getProper_mru_ca_no() {
        return Proper_mru_ca_no;
    }

    public void setProper_mru_ca_no(String proper_mru_ca_no) {
        Proper_mru_ca_no = proper_mru_ca_no;
    }

    public String getProper_mru_met_no() {
        return Proper_mru_met_no;
    }

    public void setProper_mru_met_no(String proper_mru_met_no) {
        Proper_mru_met_no = proper_mru_met_no;
    }

    public String getUploaded() {
        return uploaded;
    }

    public void setUploaded(String uploaded) {
        this.uploaded = uploaded;
    }

    public String getIs_consumer_missing() {
        return Is_consumer_missing;
    }

    public void setIs_consumer_missing(String is_consumer_missing) {
        Is_consumer_missing = is_consumer_missing;
    }

    public String getMax_deemand() {
        return Max_deemand;
    }

    public void setMax_deemand(String max_deemand) {
        Max_deemand = max_deemand;
    }

    public String getPower_factor() {
        return power_factor;
    }

    public void setPower_factor(String power_factor) {
        this.power_factor = power_factor;
    }

    public Survey_Data_Details(@NonNull String ACT_NO, String SUB_DIV_ID, String is_consumer_TD, String TD_Spinner_string, String TD_New_Consumer_No, String TD_New_meter_No, String TD_Duplicate_Consumer_no, String is_Low_Consumption, String is_Low_Spiner_value, String is_Low_met_read, byte[] is_Low_met_img, String is_MD_Found, String MD_Spinner_value, String MD_met_read, String is_mtr_nomatched_with_bill, String md_meter_no, String is_unpaid_consumer, String unpaid_mobile_no, String is_unpaid_traced, String is_consumer_proper_mru, String proper_mru_ca_no, String proper_mru_met_no, String is_consumer_missing, String uploaded) {
        this.ACT_NO = ACT_NO;
        this.SUB_DIV_ID = SUB_DIV_ID;
        Is_consumer_TD = is_consumer_TD;
        this.TD_Spinner_string = TD_Spinner_string;
        this.TD_New_Consumer_No = TD_New_Consumer_No;
        this.TD_New_meter_No = TD_New_meter_No;
        this.TD_Duplicate_Consumer_no = TD_Duplicate_Consumer_no;
        Is_Low_Consumption = is_Low_Consumption;
        Is_Low_Spiner_value = is_Low_Spiner_value;
        Is_Low_met_read = is_Low_met_read;
        Is_Low_met_img = is_Low_met_img;
        Is_MD_Found = is_MD_Found;
        this.MD_Spinner_value = MD_Spinner_value;
        this.MD_met_read = MD_met_read;
        this.is_mtr_nomatched_with_bill = is_mtr_nomatched_with_bill;
        Md_meter_no = md_meter_no;
        Is_unpaid_consumer = is_unpaid_consumer;
        this.unpaid_mobile_no = unpaid_mobile_no;
        Is_unpaid_traced = is_unpaid_traced;
        Is_consumer_proper_mru = is_consumer_proper_mru;
        Proper_mru_ca_no = proper_mru_ca_no;
        Proper_mru_met_no = proper_mru_met_no;
        Is_consumer_missing = is_consumer_missing;
        this.uploaded = uploaded;
    }

    @Override
    public String toString() {
        return "Survey_Data_Details{" +
                "MyDefaultValuedCol='" + MyDefaultValuedCol + '\'' +
                ", ACT_NO='" + ACT_NO + '\'' +
                ", SUB_DIV_ID='" + SUB_DIV_ID + '\'' +
                ", Meter_No='" + Meter_No + '\'' +
                ", Is_consumer_TD='" + Is_consumer_TD + '\'' +
                ", TD_Spinner_string='" + TD_Spinner_string + '\'' +
                ", TD_New_Consumer_No='" + TD_New_Consumer_No + '\'' +
                ", TD_New_meter_No='" + TD_New_meter_No + '\'' +
                ", TD_Duplicate_Consumer_no='" + TD_Duplicate_Consumer_no + '\'' +
                ", Is_Low_Consumption='" + Is_Low_Consumption + '\'' +
                ", Is_Low_Spiner_value='" + Is_Low_Spiner_value + '\'' +
                ", Is_Low_met_read='" + Is_Low_met_read + '\'' +
                ", Is_Low_met_img=" + Arrays.toString(Is_Low_met_img) +
                ", Is_MD_Found='" + Is_MD_Found + '\'' +
                ", MD_Spinner_value='" + MD_Spinner_value + '\'' +
                ", MD_met_read='" + MD_met_read + '\'' +
                ", is_mtr_nomatched_with_bill='" + is_mtr_nomatched_with_bill + '\'' +
                ", Md_meter_no='" + Md_meter_no + '\'' +
                ", Is_unpaid_consumer='" + Is_unpaid_consumer + '\'' +
                ", unpaid_mobile_no='" + unpaid_mobile_no + '\'' +
                ", Is_unpaid_traced='" + Is_unpaid_traced + '\'' +
                ", Is_consumer_proper_mru='" + Is_consumer_proper_mru + '\'' +
                ", Proper_mru_ca_no='" + Proper_mru_ca_no + '\'' +
                ", Proper_mru_met_no='" + Proper_mru_met_no + '\'' +
                ", Is_consumer_missing='" + Is_consumer_missing + '\'' +
                ", Max_deemand='" + Max_deemand + '\'' +
                ", power_factor='" + power_factor + '\'' +
                ", uploaded='" + uploaded + '\'' +
                '}';
    }

    public Survey_Data_Details() {
        super();
    }
}


