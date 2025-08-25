package com.nic.app.biharelectricitybilling.entity;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.Hashtable;

public class Establishment_details implements KvmSerializable {
    public static Class<Establishment_details> MRU_CLASS = Establishment_details.class;
    private String _Tariff_Id= "";
    private String _Establishment = "";
    public Establishment_details() {
    }

    @SuppressWarnings("deprecation")

    public Establishment_details(SoapObject obj) {
        this._Tariff_Id = obj.getProperty("TARIFF_ID").toString();
        this._Establishment = obj.getProperty("ESTABLISHMENTS_TYPE").toString();

    }

    public String get_Tariff_Id() {
        return _Tariff_Id;
    }

    public void set_Tariff_Id(String _Tariff_Id) {
        this._Tariff_Id = _Tariff_Id;
    }

    public String get_Establishment() {
        return _Establishment;
    }

    public void set_Establishment(String _Establishment) {
        this._Establishment = _Establishment;
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





}
