package com.nic.app.biharelectricitybilling.entity;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.io.Serializable;
import java.util.Hashtable;

public class consumer_address implements KvmSerializable, Serializable {

	private static final long serialVersionUID = 1L;

	public static Class<consumer_address> BLOCK_CLASS = consumer_address.class;
	private String conid = "";
	private String DistCode = "";
	private String BlockCode = "";
	private String panchayatcode = "";
	private String villcode = "";
	private String tollcode = "";

	public consumer_address() {
		super();
	}

	public consumer_address(SoapObject obj) {
		this.BlockCode = obj.getProperty("BLOCK_ID").toString();

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

	public String getDistCode() {
		return DistCode;
	}

	public void setDistCode(String distCode) {
		DistCode = distCode;
	}

	public String getBlockCode() {
		return BlockCode;
	}

	public void setBlockCode(String blockCode) {
		BlockCode = blockCode;
	}

	public String getConid() {
		return conid;
	}

	public void setConid(String conid) {
		this.conid = conid;
	}

	public String getPanchayatcode() {
		return panchayatcode;
	}

	public void setPanchayatcode(String panchayatcode) {
		this.panchayatcode = panchayatcode;
	}

	public String getVillcode() {
		return villcode;
	}

	public void setVillcode(String villcode) {
		this.villcode = villcode;
	}

	public String getTollcode() {
		return tollcode;
	}

	public void setTollcode(String tollcode) {
		this.tollcode = tollcode;
	}
}
