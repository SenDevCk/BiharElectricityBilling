package com.nic.app.biharelectricitybilling.entity;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * Created by .nic on 9/10/2016.
 */
public class VILLAGE implements KvmSerializable, Serializable {

    private static final long serialVersionUID = 1L;

    public static Class<VILLAGE> VILLAGE_CLASS = VILLAGE.class;

    private String VILLAGEID = "";
    private String VILLAGEName = "";

    public VILLAGE() {
        super();
    }

    public VILLAGE(SoapObject obj) {
        this.VILLAGEID = obj.getProperty("villId").toString();
        this.VILLAGEName = obj.getProperty("villName").toString();
    }

    @Override
    public Object getProperty(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPropertyCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setProperty(int arg0, Object arg1) {
        // TODO Auto-generated method stub

    }

    public String getVILLAGEID() {
        return VILLAGEID;
    }

    public void setVILLAGEID(String VILLAGEID) {
        this.VILLAGEID = VILLAGEID;
    }

    public String getVILLAGEName() {
        return VILLAGEName;
    }

    public void setVILLAGEName(String VILLAGEName) {
        this.VILLAGEName = VILLAGEName;
    }
}


