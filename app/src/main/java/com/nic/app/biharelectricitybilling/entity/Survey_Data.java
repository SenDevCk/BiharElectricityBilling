package com.nic.app.biharelectricitybilling.entity;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.sql.Blob;
import java.util.Hashtable;
@Entity(tableName = "Survey_Data")
public class Survey_Data  implements KvmSerializable {
    public static Class<Survey_Data> BILL_CLASS = Survey_Data.class;
    @PrimaryKey
    @NonNull
    private String ACT_NO;
    private String Met_no;
    private String Name;
    private String guardian_name;
    private String Adrress;
    private String RESPONSE_MESSAGE;
    private String SUB_DIV_ID;
@Ignore
    public Survey_Data(SoapObject obj) {
        this.ACT_NO = obj.getProperty("ACT_NO").toString();
        this.Name = obj.getProperty("CNAME").toString();
        this.guardian_name = obj.getProperty("CFHNAME").toString();
        this.Adrress = obj.getProperty("BILL_ADDRESS").toString();
        this.Met_no = obj.getProperty("METER_NO").toString();
        this.SUB_DIV_ID = obj.getProperty("SUB_DIV_ID").toString();
        this.RESPONSE_MESSAGE = obj.getProperty("RESPONSE_MESSAGE").toString();
    }

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

    public String getMet_no() {
        return Met_no;
    }

    public void setMet_no(String met_no) {
        Met_no = met_no;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getGuardian_name() {
        return guardian_name;
    }

    public void setGuardian_name(String guardian_name) {
        this.guardian_name = guardian_name;
    }

    public String getAdrress() {
        return Adrress;
    }

    public void setAdrress(String adrress) {
        Adrress = adrress;
    }

    public String getRESPONSE_MESSAGE() {
        return RESPONSE_MESSAGE;
    }

    public void setRESPONSE_MESSAGE(String RESPONSE_MESSAGE) {
        this.RESPONSE_MESSAGE = RESPONSE_MESSAGE;
    }

    public String getSUB_DIV_ID() {
        return SUB_DIV_ID;
    }

    public void setSUB_DIV_ID(String SUB_DIV_ID) {
        this.SUB_DIV_ID = SUB_DIV_ID;
    }

    public Survey_Data(@NonNull String ACT_NO, String met_no, String name, String guardian_name, String adrress, String RESPONSE_MESSAGE, String SUB_DIV_ID) {
        this.ACT_NO = ACT_NO;
        Met_no = met_no;
        Name = name;
        this.guardian_name = guardian_name;
        Adrress = adrress;
        this.RESPONSE_MESSAGE = RESPONSE_MESSAGE;
        this.SUB_DIV_ID = SUB_DIV_ID;
    }

    public Survey_Data() {
        super();
    }
}


