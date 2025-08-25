package com.nic.app.biharelectricitybilling.entity;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * Created by .nic on 9/10/2016.
 */
public class TOLLA implements KvmSerializable, Serializable {

    private static final long serialVersionUID = 1L;

    public static Class<TOLLA> TOLLA_CLASS = TOLLA.class;

//    PanchayatID
//    <villID>string</villID>
//    <TolaID>string</TolaID>
//    <TolaName>string</TolaName>
//CREATE TABLE "Tola" ( `PanchayatID` TEXT, `DistrictID` TEXT, `BlockID` TEXT, `VillageID` TEXT, `TolaID` TEXT, `TolaName` TEXT, `AreaType` TEXT, PRIMARY KEY(TolaID) )

    private String TOLLAID = "";
    private String TOLLAName = "";

    private  String PanchayatID="";
    private  String VillageID="";

    public TOLLA() {
        super();
    }

    public TOLLA(SoapObject obj) {
        this.TOLLAID = obj.getProperty("tolaId").toString();
        this.TOLLAName = obj.getProperty("tolaName").toString();
//        this.PanchayatID = obj.getProperty("PanchayatID").toString();
      //  this.VillageID = obj.getProperty("VillageCode").toString();
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

    public String getTOLLAID() {
        return TOLLAID;
    }

    public void setTOLLAID(String TOLLAID) {
        this.TOLLAID = TOLLAID;
    }

    public String getTOLLAName() {
        return TOLLAName;
    }

    public void setTOLLAName(String TOLLAName) {
        this.TOLLAName = TOLLAName;
    }

    public String getPanchayatID() {
        return PanchayatID;
    }

    public void setPanchayatID(String panchayatID) {
        PanchayatID = panchayatID;
    }

    public String getVillageID() {
        return VillageID;
    }

    public void setVillageID(String villageID) {
        VillageID = villageID;
    }
}


