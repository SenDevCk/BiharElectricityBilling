package com.nic.app.biharelectricitybilling.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.Hashtable;

@Entity(tableName = "Payment_Data")
public class Payment_Data_Details implements KvmSerializable {
    public static Class<Payment_Data_Details> BILL_CLASS = Payment_Data_Details.class;
    @PrimaryKey
    @NonNull
    private String ACT_NO;
    private String Data;
    private String RESPONSE_MESSAGE;
    private String SUB_DIV_ID;
    @Ignore
    public Payment_Data_Details(SoapObject obj) {
        this.ACT_NO = obj.getProperty("ACT_NO").toString();
        this.Data = obj.getProperty("DATA").toString();
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

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
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

    public Payment_Data_Details(@NonNull String ACT_NO, String Data,String RESPONSE_MESSAGE, String SUB_DIV_ID) {
        this.ACT_NO = ACT_NO;
        this.Data = Data;

        this.RESPONSE_MESSAGE = RESPONSE_MESSAGE;
        this.SUB_DIV_ID = SUB_DIV_ID;
    }

    public Payment_Data_Details() {
        super();
    }
}


