package com.nic.app.biharelectricitybilling.entity;
import java.util.Date;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
public class BillDetails implements KvmSerializable {
    public static Class<BillDetails> BILL_CLASS = BillDetails.class;
    private String _ACT_NO = "";
    private String _AFTER_DATE = "";
    private String _AREA_TYPE;
    private String _AVG_UNIT = "";
    private String _BILLED_MONTH = "";
    private String _BILLED_UNIT = "";
    private String _BILL_ADDRESS = "";
    private String _BILL_DT = "";
    private String _BILL_NO = "";
    private String _BILL_TYPE = "";
    private String _BMONTH = "";
    private String _BOOK_NO = "";
    private String _BYEAR = "";
    private String _BY_DATE = "";
    private String _CATEGORY;
    private String _CNAME = "";
    private String _COMPANY = "";
    private String _CONC_LOAD = "";
    private String _CON_DEM = "";
    private String _CON_ID = "";
    private String _CUR_READ = "";
    private String _DEM_CURR = "";
    private String _DIV_NAME = "";
    private String _DPS_ARREAR = "";
    private String _DPS_CURR = "";
    private String _EC_ARREAR = "";
    private String _EC_CURR;
    private String _ED_CURRENT = "";
    private String _EXC_DEM_CURR = "";
    private String _FIX_CURR = "";
    private String _GROSS_AMT = "";
    private String _INCENTIVE = "";
    private String _INTR_SEC_DEP = "";
    private String _LAST_PAID_AMT = "";
    private String _LAST_PAY_DATE = "";
    private String _LAST_RCPT_NO = "";
    private String _METER_NO = "";
    private String _MET_OWNER = "";
    private String _MET_RENT;
    private String _MMC_UNIT = "";
    private String _MULTI_FACT = "";
    private String _NET_AMT = "";
    private String _OLD_CON_ID = "";
    private String _OTH_ARREAR = "";
    private String _OTH_CURR = "";
    private String _PHASE = "";
    private String _POLE_NO = "";
    private String _POW_FACT = "";
    private String _PREV_READ = "";
    private String _PRE_READ_DATE = "";
    private String _PRE_READ_STAT;
    private String _PROMPT_AMT = "";
    private String _READ_DATE = "";
    private String _READ_STAT = "";
    private String _REBATE = "";
    private String _REBATE_ON_MMC = "";
    private String _REC_DEMAND = "";
    private String _RESPONSE_MESSAGE = "";
    private String _SANC_LOAD = "";
    private String _SECTION_NAME = "";
    private String _SEC_DEP = "";
    private String _SHUN_CURR = "";
    private String _SUB_DIV_NAME;
    private String _UPTO_DATE = "";
    private String _SubTotalA = "";
    private String _SubTotalB = "";
    private String _SubTotalC;
    private String _AMBI_Flag = "";
    private String _IsAlredyPrint = "";
    private String _BILL_TIME;
    private String _UNITS_CONS = "";
    private String _EDAREAR = "";
    private String _GOV_SUBSIDY;
    private String _OLD_CONSUMPTION = "";
    private String _ONLINE_REBATE = "";
    private String _MOBILENO = "";
    private String _KEPT_AMOUNT = "";
    private String _KEPT_INST_FLAG = "";
    private String _KEPT_PAY_AMT = "";
    private String _KEPT_INST_AMT = "";
    private String _CGST_AMT = "";
    private String _SGCT_AMT = "";
    private String _Go_Green = "";
    private String _Remission_charge = "";
    private String QUARTERLY_REBATE_POSTED = "";
    private String OTH_CGST_AMT = "";
    private String OTH_SGCT_AMT = "";
    public BillDetails() {
    }

    @SuppressWarnings("deprecation")
    public BillDetails(SoapObject obj) {
        this._ACT_NO = obj.getProperty("ACT_NO").toString();
        this._AFTER_DATE = obj.getProperty("AFTER_DATE").toString();
        this._AREA_TYPE = obj.getProperty("AREA_TYPE").toString();
        this._AVG_UNIT = obj.getProperty("AVG_UNIT").toString();
        this._BILLED_MONTH = obj.getProperty("BILLED_MONTH").toString();
        this._BILLED_UNIT = obj.getProperty("BILLED_UNIT").toString();
        this._BILL_ADDRESS = obj.getProperty("BILL_ADDRESS").toString();
        this._BILL_DT = obj.getProperty("BILL_DT").toString();
        this._BILL_NO = obj.getProperty("BILL_NO").toString();
        this._BILL_TYPE = obj.getProperty("BILL_TYPE").toString();
        this._BMONTH = obj.getProperty("BMONTH").toString();
        this._BOOK_NO = obj.getProperty("BOOK_NO").toString();
        this._BYEAR = obj.getProperty("BYEAR").toString();
        this._BY_DATE = obj.getProperty("BY_DATE").toString();
        this._CATEGORY = obj.getProperty("CATEGORY").toString();
        this._CNAME = obj.getProperty("CNAME").toString();
        this._COMPANY = obj.getProperty("COMPANY").toString();
        this._CONC_LOAD = obj.getProperty("CONC_LOAD").toString();
        this._CON_DEM = obj.getProperty("CON_DEM").toString();
        this._CON_ID = obj.getProperty("CON_ID").toString();
        this._CUR_READ = obj.getProperty("CUR_READ").toString();
        this._DEM_CURR = obj.getProperty("DEM_CURR").toString();
        this._DIV_NAME = obj.getProperty("DIV_NAME").toString();
        this._DPS_ARREAR = obj.getProperty("DPS_ARREAR").toString();
        this._DPS_CURR = obj.getProperty("DPS_CURR").toString();
        this._EC_ARREAR = obj.getProperty("EC_ARREAR").toString();
        this._EC_CURR = obj.getProperty("EC_CURR").toString();
        this._EDAREAR = obj.getProperty("ED_ARREAR").toString();
        this._ED_CURRENT = obj.getProperty("ED_CURRENT").toString();
        this._EXC_DEM_CURR = obj.getProperty("EXC_DEM_CURR").toString();
        this._FIX_CURR = obj.getProperty("FIX_CURR").toString();
        this._GROSS_AMT = obj.getProperty("GROSS_AMT").toString();
        this._INCENTIVE = obj.getProperty("INCENTIVE").toString();
        this._INTR_SEC_DEP = obj.getProperty("INTR_SEC_DEP").toString();
        this._LAST_PAID_AMT = obj.getProperty("LAST_PAID_AMT").toString();
        this._LAST_PAY_DATE = obj.getProperty("LAST_PAY_DATE").toString();
        this._LAST_RCPT_NO = obj.getProperty("LAST_RCPT_NO").toString();
        this._METER_NO = obj.getProperty("METER_NO").toString();
        this._MET_OWNER = obj.getProperty("MET_OWNER").toString();
        this._MET_RENT = obj.getProperty("MET_RENT").toString();
        this._MMC_UNIT = obj.getProperty("MMC_UNIT").toString();
        this._MULTI_FACT = obj.getProperty("MULTI_FACT").toString();
        this._NET_AMT = obj.getProperty("NET_AMT").toString();
        this._OLD_CON_ID = obj.getProperty("OLD_CON_ID").toString();
        this._OTH_CURR = obj.getProperty("OTH_CURR").toString();
        this._PHASE = obj.getProperty("PHASE").toString();
        this._POLE_NO = obj.getProperty("POLE_NO").toString();
        this._POW_FACT = obj.getProperty("POW_FACT").toString();
        this._PREV_READ = obj.getProperty("PREV_READ").toString();
        this._PRE_READ_DATE = obj.getProperty("PRE_READ_DATE").toString();
        this._PRE_READ_STAT = obj.getProperty("PRE_READ_STAT").toString();
        this._PROMPT_AMT = obj.getProperty("PROMPT_AMT").toString();
        this._READ_DATE = obj.getProperty("READ_DATE").toString();
        this._READ_STAT = obj.getProperty("READ_STAT").toString();
        this._REBATE = obj.getProperty("REBATE").toString();
        this._REBATE_ON_MMC = obj.getProperty("REBATE_ON_MMC").toString();
        this._REC_DEMAND = obj.getProperty("REC_DEMAND").toString();
        this._RESPONSE_MESSAGE = obj.getProperty("RESPONSE_MESSAGE").toString();
        this._SANC_LOAD = obj.getProperty("SANC_LOAD").toString();
        this._SECTION_NAME = obj.getProperty("SECTION_NAME").toString();
        this._SEC_DEP = obj.getProperty("SEC_DEP").toString();
        this._SHUN_CURR = obj.getProperty("SHUN_CURR").toString();
        this._SUB_DIV_NAME = obj.getProperty("SUB_DIV_NAME").toString();
        this._UPTO_DATE = obj.getProperty("UPTO_DATE").toString();
        this._BILL_TIME = obj.getProperty("BILL_TIME").toString();
        this._UNITS_CONS = obj.getProperty("UNITS_CONS").toString();
        this._SubTotalA = obj.getProperty("SUB_TOTAL_A").toString();
        this._SubTotalB = obj.getProperty("SUB_TOTAL_B").toString();
        this._SubTotalC = obj.getProperty("SUB_TOTAL_C").toString();
        this._AMBI_Flag = obj.getProperty("AMBI_FLAG").toString();
        this._MOBILENO = obj.getProperty("MOBILE_NO").toString();
        this._GOV_SUBSIDY = obj.getProperty("GOV_SUBSIDY").toString();
        this._OLD_CONSUMPTION = obj.getProperty("OLD_CONSUMPTION").toString();
        this._ONLINE_REBATE = obj.getProperty("ONLINE_REBATE").toString();
        this._KEPT_AMOUNT = obj.getProperty("KEPT_AMT").toString();
        this._KEPT_INST_FLAG = obj.getProperty("KEPT_INST_FLAG").toString();
        this._KEPT_PAY_AMT = obj.getProperty("KEPT_PAY_AMT").toString();
        this._KEPT_INST_AMT = obj.getProperty("KEPT_INST_AMT").toString();
        this._CGST_AMT = obj.getProperty("CGST_AMT").toString();
        this._SGCT_AMT = obj.getProperty("SGST_AMT").toString();
        this._Go_Green = obj.getProperty("GO_GREEN").toString();
        this._Remission_charge = obj.getProperty("REMISSION_CHARGE").toString();
        this.QUARTERLY_REBATE_POSTED = obj.getProperty("QUARTERLY_REBATE_POSTED").toString();
//use these two
        this.OTH_CGST_AMT = obj.getProperty("OTH_CGST").toString();
        this.OTH_SGCT_AMT = obj.getProperty("OTH_SGST").toString();
        this._OTH_ARREAR = obj.getProperty("OTH_ARREAR").toString();
    }
    @Override
    public int getPropertyCount() {
        // TODO Auto-generated method stub
        return 8;
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

    public String get_ACT_NO() {
        return _ACT_NO;
    }

    public void set_ACT_NO(String _ACT_NO) {
        this._ACT_NO = _ACT_NO;
    }

    public String get_AFTER_DATE() {
        return _AFTER_DATE;
    }

    public void set_AFTER_DATE(String _AFTER_DATE) {
        this._AFTER_DATE = _AFTER_DATE;
    }

    public String get_AREA_TYPE() {
        return _AREA_TYPE;
    }

    public void set_AREA_TYPE(String _AREA_TYPE) {
        this._AREA_TYPE = _AREA_TYPE;
    }

    public String get_AVG_UNIT() {
        return _AVG_UNIT;
    }

    public void set_AVG_UNIT(String _AVG_UNIT) {
        this._AVG_UNIT = _AVG_UNIT;
    }

    public String get_BILLED_MONTH() {
        return _BILLED_MONTH;
    }

    public void set_BILLED_MONTH(String _BILLED_MONTH) {
        this._BILLED_MONTH = _BILLED_MONTH;
    }

    public String get_BILLED_UNIT() {
        return _BILLED_UNIT;
    }

    public void set_BILLED_UNIT(String _BILLED_UNIT) {
        this._BILLED_UNIT = _BILLED_UNIT;
    }

    public String get_BILL_ADDRESS() {
        return _BILL_ADDRESS;
    }

    public void set_BILL_ADDRESS(String _BILL_ADDRESS) {
        this._BILL_ADDRESS = _BILL_ADDRESS;
    }

    public String get_BILL_DT() {
        return _BILL_DT;
    }

    public void set_BILL_DT(String _BILL_DT) {
        this._BILL_DT = _BILL_DT;
    }

    public String get_BILL_NO() {
        return _BILL_NO;
    }

    public void set_BILL_NO(String _BILL_NO) {
        this._BILL_NO = _BILL_NO;
    }

    public String get_BILL_TYPE() {
        return _BILL_TYPE;
    }

    public void set_BILL_TYPE(String _BILL_TYPE) {
        this._BILL_TYPE = _BILL_TYPE;
    }

    public String get_BMONTH() {
        return _BMONTH;
    }

    public void set_BMONTH(String _BMONTH) {
        this._BMONTH = _BMONTH;
    }

    public String get_BOOK_NO() {
        return _BOOK_NO;
    }

    public void set_BOOK_NO(String _BOOK_NO) {
        this._BOOK_NO = _BOOK_NO;
    }

    public String get_BYEAR() {
        return _BYEAR;
    }

    public void set_BYEAR(String _BYEAR) {
        this._BYEAR = _BYEAR;
    }

    public String get_BY_DATE() {
        return _BY_DATE;
    }

    public void set_BY_DATE(String _BY_DATE) {
        this._BY_DATE = _BY_DATE;
    }

    public String get_CATEGORY() {
        return _CATEGORY;
    }

    public void set_CATEGORY(String _CATEGORY) {
        this._CATEGORY = _CATEGORY;
    }

    public String get_CNAME() {
        return _CNAME;
    }

    public void set_CNAME(String _CNAME) {
        this._CNAME = _CNAME;
    }

    public String get_COMPANY() {
        return _COMPANY;
    }

    public void set_COMPANY(String _COMPANY) {
        this._COMPANY = _COMPANY;
    }

    public String get_CONC_LOAD() {
        return _CONC_LOAD;
    }

    public void set_CONC_LOAD(String _CONC_LOAD) {
        this._CONC_LOAD = _CONC_LOAD;
    }

    public String get_CON_DEM() {
        return _CON_DEM;
    }

    public void set_CON_DEM(String _CON_DEM) {
        this._CON_DEM = _CON_DEM;
    }

    public String get_CON_ID() {
        return _CON_ID;
    }

    public void set_CON_ID(String _CON_ID) {
        this._CON_ID = _CON_ID;
    }

    public String get_CUR_READ() {
        return _CUR_READ;
    }

    public void set_CUR_READ(String _CUR_READ) {
        this._CUR_READ = _CUR_READ;
    }

    public String get_DEM_CURR() {
        return _DEM_CURR;
    }

    public String get_Go_Green() {
        return _Go_Green;
    }

    public void set_Go_Green(String _Go_Green) {
        this._Go_Green = _Go_Green;
    }

    public String get_Remission_charge() {
        return _Remission_charge;
    }

    public void set_Remission_charge(String _Remission_charge) {
        this._Remission_charge = _Remission_charge;
    }

    public String getQUARTERLY_REBATE_POSTED() {
        return QUARTERLY_REBATE_POSTED;
    }

    public void setQUARTERLY_REBATE_POSTED(String QUARTERLY_REBATE_POSTED) {
        this.QUARTERLY_REBATE_POSTED = QUARTERLY_REBATE_POSTED;
    }

    public void set_DEM_CURR(String _DEM_CURR) {
        this._DEM_CURR = _DEM_CURR;
    }

    public String get_DIV_NAME() {
        return _DIV_NAME;
    }

    public void set_DIV_NAME(String _DIV_NAME) {
        this._DIV_NAME = _DIV_NAME;
    }

    public String get_DPS_ARREAR() {
        return _DPS_ARREAR;
    }

    public void set_DPS_ARREAR(String _DPS_ARREAR) {
        this._DPS_ARREAR = _DPS_ARREAR;
    }


    public String get_DPS_CURR() {
        return _DPS_CURR;
    }

    public void set_DPS_CURR(String _DPS_CURR) {
        this._DPS_CURR = _DPS_CURR;
    }

    public String get_EC_ARREAR() {
        return _EC_ARREAR;
    }

    public void set_EC_ARREAR(String _EC_ARREAR) {
        this._EC_ARREAR = _EC_ARREAR;
    }

    public String get_EC_CURR() {
        return _EC_CURR;
    }

    public void set_EC_CURR(String _EC_CURR) {
        this._EC_CURR = _EC_CURR;
    }

    public String get_ED_CURRENT() {
        return _ED_CURRENT;
    }

    public String getOTH_CGST_AMT() {
        return OTH_CGST_AMT;
    }

    public void setOTH_CGST_AMT(String OTH_CGST_AMT) {
        this.OTH_CGST_AMT = OTH_CGST_AMT;
    }

    public String getOTH_SGCT_AMT() {
        return OTH_SGCT_AMT;
    }

    public void setOTH_SGCT_AMT(String OTH_SGCT_AMT) {
        this.OTH_SGCT_AMT = OTH_SGCT_AMT;
    }

    public void set_ED_CURRENT(String _ED_CURRENT) {
        this._ED_CURRENT = _ED_CURRENT;
    }

    public String get_EXC_DEM_CURR() {
        return _EXC_DEM_CURR;
    }

    public void set_EXC_DEM_CURR(String _EXC_DEM_CURR) {
        this._EXC_DEM_CURR = _EXC_DEM_CURR;
    }

    public String get_FIX_CURR() {
        return _FIX_CURR;
    }

    public void set_FIX_CURR(String _FIX_CURR) {
        this._FIX_CURR = _FIX_CURR;
    }

    public String get_GROSS_AMT() {
        return _GROSS_AMT;
    }

    public void set_GROSS_AMT(String _GROSS_AMT) {
        this._GROSS_AMT = _GROSS_AMT;
    }

    public String get_INCENTIVE() {
        return _INCENTIVE;
    }

    public void set_INCENTIVE(String _INCENTIVE) {
        this._INCENTIVE = _INCENTIVE;
    }

    public String get_INTR_SEC_DEP() {
        return _INTR_SEC_DEP;
    }

    public void set_INTR_SEC_DEP(String _INTR_SEC_DEP) {
        this._INTR_SEC_DEP = _INTR_SEC_DEP;
    }

    public String get_LAST_PAID_AMT() {
        return _LAST_PAID_AMT;
    }

    public void set_LAST_PAID_AMT(String _LAST_PAID_AMT) {
        this._LAST_PAID_AMT = _LAST_PAID_AMT;
    }

    public String get_LAST_PAY_DATE() {
        return _LAST_PAY_DATE;
    }

    public void set_LAST_PAY_DATE(String _LAST_PAY_DATE) {
        this._LAST_PAY_DATE = _LAST_PAY_DATE;
    }

    public String get_LAST_RCPT_NO() {
        return _LAST_RCPT_NO;
    }

    public void set_LAST_RCPT_NO(String _LAST_RCPT_NO) {
        this._LAST_RCPT_NO = _LAST_RCPT_NO;
    }

    public String get_METER_NO() {
        return _METER_NO;
    }

    public void set_METER_NO(String _METER_NO) {
        this._METER_NO = _METER_NO;
    }

    public String get_MET_OWNER() {
        return _MET_OWNER;
    }

    public void set_MET_OWNER(String _MET_OWNER) {
        this._MET_OWNER = _MET_OWNER;
    }

    public String get_MET_RENT() {
        return _MET_RENT;
    }

    public void set_MET_RENT(String _MET_RENT) {
        this._MET_RENT = _MET_RENT;
    }

    public String get_MMC_UNIT() {
        return _MMC_UNIT;
    }

    public void set_MMC_UNIT(String _MMC_UNIT) {
        this._MMC_UNIT = _MMC_UNIT;
    }

    public String get_MULTI_FACT() {
        return _MULTI_FACT;
    }

    public void set_MULTI_FACT(String _MULTI_FACT) {
        this._MULTI_FACT = _MULTI_FACT;
    }

    public String get_NET_AMT() {
        return _NET_AMT;
    }

    public void set_NET_AMT(String _NET_AMT) {
        this._NET_AMT = _NET_AMT;
    }

    public String get_OLD_CON_ID() {
        return _OLD_CON_ID;
    }

    public void set_OLD_CON_ID(String _OLD_CON_ID) {
        this._OLD_CON_ID = _OLD_CON_ID;
    }

    public String get_OTH_ARREAR() {
        return _OTH_ARREAR;
    }
    public void set_OTH_ARREAR(String _OTH_ARREAR) {
        this._OTH_ARREAR = _OTH_ARREAR;
    }
    public String get_OTH_CURR() {
        return _OTH_CURR;
    }
    public void set_OTH_CURR(String _OTH_CURR) {
        this._OTH_CURR = _OTH_CURR;
    }
    public String get_PHASE() {
        return _PHASE;
    }
    public void set_PHASE(String _PHASE) {
        this._PHASE = _PHASE;
    }
    public String get_POLE_NO() {
        return _POLE_NO;
    }
    public void set_POLE_NO(String _POLE_NO) {
        this._POLE_NO = _POLE_NO;
    }
    public String get_POW_FACT() {
        return _POW_FACT;
    }
    public void set_POW_FACT(String _POW_FACT) {
        this._POW_FACT = _POW_FACT;
    }
    public String get_PREV_READ() {
        return _PREV_READ;
    }
    public void set_PREV_READ(String _PREV_READ) {
        this._PREV_READ = _PREV_READ;
    }
    public String get_PRE_READ_DATE() {
        return _PRE_READ_DATE;
    }

    public void set_PRE_READ_DATE(String _PRE_READ_DATE) {
        this._PRE_READ_DATE = _PRE_READ_DATE;
    }

    public String get_PRE_READ_STAT() {
        return _PRE_READ_STAT;
    }

    public void set_PRE_READ_STAT(String _PRE_READ_STAT) {
        this._PRE_READ_STAT = _PRE_READ_STAT;
    }

    public String get_PROMPT_AMT() {
        return _PROMPT_AMT;
    }

    public void set_PROMPT_AMT(String _PROMPT_AMT) {
        this._PROMPT_AMT = _PROMPT_AMT;
    }

    public String get_READ_DATE() {
        return _READ_DATE;
    }

    public void set_READ_DATE(String _READ_DATE) {
        this._READ_DATE = _READ_DATE;
    }

    public String get_READ_STAT() {
        return _READ_STAT;
    }

    public void set_READ_STAT(String _READ_STAT) {
        this._READ_STAT = _READ_STAT;
    }

    public String get_REBATE() {
        return _REBATE;
    }

    public void set_REBATE(String _REBATE) {
        this._REBATE = _REBATE;
    }

    public String get_REBATE_ON_MMC() {
        return _REBATE_ON_MMC;
    }

    public void set_REBATE_ON_MMC(String _REBATE_ON_MMC) {
        this._REBATE_ON_MMC = _REBATE_ON_MMC;
    }

    public String get_REC_DEMAND() {
        return _REC_DEMAND;
    }

    public void set_REC_DEMAND(String _REC_DEMAND) {
        this._REC_DEMAND = _REC_DEMAND;
    }

    public String get_RESPONSE_MESSAGE() {
        return _RESPONSE_MESSAGE;
    }

    public void set_RESPONSE_MESSAGE(String _RESPONSE_MESSAGE) {
        this._RESPONSE_MESSAGE = _RESPONSE_MESSAGE;
    }

    public String get_SANC_LOAD() {
        return _SANC_LOAD;
    }

    public void set_SANC_LOAD(String _SANC_LOAD) {
        this._SANC_LOAD = _SANC_LOAD;
    }

    public String get_SECTION_NAME() {
        return _SECTION_NAME;
    }

    public void set_SECTION_NAME(String _SECTION_NAME) {
        this._SECTION_NAME = _SECTION_NAME;
    }

    public String get_SEC_DEP() {
        return _SEC_DEP;
    }

    public void set_SEC_DEP(String _SEC_DEP) {
        this._SEC_DEP = _SEC_DEP;
    }

    public String get_SHUN_CURR() {
        return _SHUN_CURR;
    }

    public void set_SHUN_CURR(String _SHUN_CURR) {
        this._SHUN_CURR = _SHUN_CURR;
    }

    public String get_SUB_DIV_NAME() {
        return _SUB_DIV_NAME;
    }

    public void set_SUB_DIV_NAME(String _SUB_DIV_NAME) {
        this._SUB_DIV_NAME = _SUB_DIV_NAME;
    }

    public String get_UPTO_DATE() {
        return _UPTO_DATE;
    }

    public void set_UPTO_DATE(String _UPTO_DATE) {
        this._UPTO_DATE = _UPTO_DATE;
    }

    public String get_SubTotalA() {
        return _SubTotalA;
    }

    public void set_SubTotalA(String _SubTotalA) {
        this._SubTotalA = _SubTotalA;
    }

    public String get_SubTotalB() {
        return _SubTotalB;
    }

    public void set_SubTotalB(String _SubTotalB) {
        this._SubTotalB = _SubTotalB;
    }

    public String get_SubTotalC() {
        return _SubTotalC;
    }

    public void set_SubTotalC(String _SubTotalC) {
        this._SubTotalC = _SubTotalC;
    }

    public String get_AMBI_Flag() {
        return _AMBI_Flag;
    }

    public void set_AMBI_Flag(String _AMBI_Flag) {
        this._AMBI_Flag = _AMBI_Flag;
    }

    public String get_BILL_TIME() {
        return _BILL_TIME;
    }

    public void set_BILL_TIME(String _BILL_TIME) {
        this._BILL_TIME = _BILL_TIME;
    }

    public String get_UNITS_CONS() {
        return _UNITS_CONS;
    }

    public void set_UNITS_CONS(String _UNITS_CONS) {
        this._UNITS_CONS = _UNITS_CONS;
    }

    public String get_IsAlredyPrint() {
        return _IsAlredyPrint;
    }

    public void set_IsAlredyPrint(String _IsAlredyPrint) {
        this._IsAlredyPrint = _IsAlredyPrint;
    }
    public String get_MOBILENO() {
        return _MOBILENO;
    }

    public void set_MOBILENO(String _MOBILENO) {
        this._MOBILENO = _MOBILENO;
    }

    public String get_EDAREAR() {
        return _EDAREAR;
    }

    public void set_EDAREAR(String _EDAREAR) {
        this._EDAREAR = _EDAREAR;
    }

    public String get_GOV_SUBSIDY() {
        return _GOV_SUBSIDY;
    }

    public void set_GOV_SUBSIDY(String _GOV_SUBSIDY) {
        this._GOV_SUBSIDY = _GOV_SUBSIDY;
    }

    public String get_OLD_CONSUMPTION() {
        return _OLD_CONSUMPTION;
    }

    public void set_OLD_CONSUMPTION(String _OLD_CONSUMPTION) {
        this._OLD_CONSUMPTION = _OLD_CONSUMPTION;
    }

    public String get_KEPT_AMOUNT() {
        return _KEPT_AMOUNT;
    }

    public void set_KEPT_AMOUNT(String _KEPT_AMOUNT) {
        this._KEPT_AMOUNT = _KEPT_AMOUNT;
    }
    public String get_KEPT_INST_FLAG() {
        return _KEPT_INST_FLAG;
    }
    public void set_KEPT_INST_FLAG(String _KEPT_INST_FLAG) {
        this._KEPT_INST_FLAG = _KEPT_INST_FLAG;
    }
    public String get_KEPT_PAY_AMT() {
        return _KEPT_PAY_AMT;
    }

    public void set_KEPT_PAY_AMT(String _KEPT_PAY_AMT) {
        this._KEPT_PAY_AMT = _KEPT_PAY_AMT;
    }

    public String get_ONLINE_REBATE() {
        return _ONLINE_REBATE;
    }

    public void set_ONLINE_REBATE(String _ONLINE_REBATE) {
        this._ONLINE_REBATE = _ONLINE_REBATE;
    }

    public String get_KEPT_INST_AMT() {
        return _KEPT_INST_AMT;
    }

    public void set_KEPT_INST_AMT(String _KEPT_INST_AMT) {
        this._KEPT_INST_AMT = _KEPT_INST_AMT;
    }

    public String get_CGST_AMT() {
        return _CGST_AMT;
    }

    public void set_CGST_AMT(String _CGST_AMT) {
        this._CGST_AMT = _CGST_AMT;
    }

    public String get_SGCT_AMT() {
        return _SGCT_AMT;
    }

    public void set_SGCT_AMT(String _SGCT_AMT) {
        this._SGCT_AMT = _SGCT_AMT;
    }
}
