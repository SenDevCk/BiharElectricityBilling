package com.nic.app.biharelectricitybilling.entity;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.io.Serializable;
import java.util.Hashtable;

public class lk_md_details implements KvmSerializable, Serializable {

	private static final long serialVersionUID = 1L;

	public static Class<lk_md_details> BLOCK_CLASS = lk_md_details.class;

	private String md_count = "";
	private String lk_count = "";
	private String tot = "";
	private String md_count_current = "";
	private String lk_count_current = "";


	public lk_md_details() {
		super();
	}

	public lk_md_details(SoapObject obj) {
		this.md_count = obj.getProperty("BLOCK_ID").toString();
		this.lk_count = obj.getProperty("BLOCK_NAME").toString();
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

	public String getMd_count() {
		return md_count;
	}

	public void setMd_count(String md_count) {
		this.md_count = md_count;
	}

	public String getLk_count() {
		return lk_count;
	}

	public void setLk_count(String lk_count) {
		this.lk_count = lk_count;
	}

	public String getTot() {
		return tot;
	}

	public void setTot(String tot) {
		this.tot = tot;
	}

	public String getMd_count_current() {
		return md_count_current;
	}

	public void setMd_count_current(String md_count_current) {
		this.md_count_current = md_count_current;
	}

	public String getLk_count_current() {
		return lk_count_current;
	}

	public void setLk_count_current(String lk_count_current) {
		this.lk_count_current = lk_count_current;
	}
}
