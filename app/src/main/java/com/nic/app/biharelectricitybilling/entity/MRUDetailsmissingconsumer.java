package com.nic.app.biharelectricitybilling.entity;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.Hashtable;

public class MRUDetailsmissingconsumer implements KvmSerializable {
    public static Class<MRUDetailsmissingconsumer> MRU_CLASS = MRUDetailsmissingconsumer.class;
    private String _ACT_NO = "";
    private String _BILL_ADDRESS = "";
    private String _NEW_BOOK_NO = "";
    private String _Cfathername = "";
    private String _CNAME = "";
    private String _CON_ID = "";
    private String _METER_NO = "";
    private String _CONTACT_NUM = "";
    private String _RESPONSE_MESSAGE = "";
    public MRUDetailsmissingconsumer() {
    }
    @SuppressWarnings("deprecation")

    public MRUDetailsmissingconsumer(SoapObject obj) {
        this._ACT_NO = obj.getProperty("ACT_NO").toString();
        this._BILL_ADDRESS = obj.getProperty("ADDRESS").toString();
        this._CNAME = obj.getProperty("CNAME").toString();
        this._CONTACT_NUM = obj.getProperty("MOB_NO").toString();
        this._CON_ID = obj.getProperty("COND_ID").toString();
        this._Cfathername = obj.getProperty("CFH_NAME").toString();
        this._METER_NO = obj.getProperty("METER_NO").toString();
        this._RESPONSE_MESSAGE = obj.getProperty("RESP_MSG").toString();
        this._NEW_BOOK_NO = obj.getProperty("BOOK_NO").toString();
    }


    public String get_NEW_BOOK_NO() {
        return _NEW_BOOK_NO;
    }

    public void set_NEW_BOOK_NO(String _NEW_BOOK_NO) {
        this._NEW_BOOK_NO = _NEW_BOOK_NO;
    }


    public String get_ACT_NO() {
        return _ACT_NO;
    }

    public void set_ACT_NO(String _ACT_NO) {
        this._ACT_NO = _ACT_NO;
    }

    public String get_BILL_ADDRESS() {
        return _BILL_ADDRESS;
    }

    public void set_BILL_ADDRESS(String _BILL_ADDRESS) {
        this._BILL_ADDRESS = _BILL_ADDRESS;
    }


    public String get_CNAME() {
        return _CNAME;
    }

    public void set_CNAME(String _CNAME) {
        this._CNAME = _CNAME;
    }


    public String get_CONTACT_NUM() {
        return _CONTACT_NUM;
    }

    public void set_CONTACT_NUM(String _CONTACT_NUM) {
        this._CONTACT_NUM = _CONTACT_NUM;
    }

    public String get_CON_ID() {
        return _CON_ID;
    }

    public void set_CON_ID(String _CON_ID) {
        this._CON_ID = _CON_ID;
    }


    public String get_METER_NO() {
        return _METER_NO;
    }

    public void set_METER_NO(String _METER_NO) {
        this._METER_NO = _METER_NO;
    }


    public String get_RESPONSE_MESSAGE() {
        return _RESPONSE_MESSAGE;
    }

    public void set_RESPONSE_MESSAGE(String _RESPONSE_MESSAGE) {
        this._RESPONSE_MESSAGE = _RESPONSE_MESSAGE;
    }

    @Override
    public int getPropertyCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object getProperty(int index) {
        return null;
    }

    @Override
    public void getPropertyInfo(int index, Hashtable arg1,
                                PropertyInfo propertyInfo) {
    }

    @Override
    public void setProperty(int index, Object obj) {
    }


    public String get_Cfathername() {
        return _Cfathername;
    }

    public void set_Cfathername(String _Cfathername) {
        this._Cfathername = _Cfathername;
    }


}
