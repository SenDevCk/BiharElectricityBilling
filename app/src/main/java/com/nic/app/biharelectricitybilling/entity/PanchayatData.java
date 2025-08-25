package com.nic.app.biharelectricitybilling.entity;

import org.ksoap2.serialization.SoapObject;

public class PanchayatData {
    String Pcode="";
    String Pname="";
    String area_type="";
    String total_ward="";

    public static Class<PanchayatData> PanchayatData_CLASS=PanchayatData.class;


    public PanchayatData(SoapObject sobj)
    {
        this.Pcode = sobj.getProperty("panchayatId").toString();
        this.Pname = sobj.getProperty("panchayatName").toString();
        this.area_type = sobj.getProperty("areaType").toString();
        this.total_ward = sobj.getProperty("totalWard").toString();
    }
    public PanchayatData() {
        super();
    }
    public String getPcode() {
        return Pcode;
    }

    public void setPcode(String _pcode) {
        Pcode = _pcode;
    }

    public String getPname() {
        return Pname;
    }

    public void setPname(String _pname) {
        Pname = _pname;
    }

    public String getArea_type() {
        return area_type;
    }

    public void setArea_type(String area_type) {
        this.area_type = area_type;
    }

    public String getTotal_ward() {
        return total_ward;
    }

    public void setTotal_ward(String total_ward) {
        this.total_ward = total_ward;
    }
}
