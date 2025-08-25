package com.nic.app.biharelectricitybilling.SessionManager;

/**
 * Created by Administrator on 2018-07-25.
 */

public class BlueToothBean {

    private String BtName;

    private String BTMac;

    public BlueToothBean(String btName, String BTMac) {
        BtName = btName;
        this.BTMac = BTMac;
    }

    public String getBtName() {
        return BtName;
    }

    public void setBtName(String btName) {
        BtName = btName;
    }

    public String getBTMac() {
        return BTMac;
    }

    public void setBTMac(String BTMac) {
        this.BTMac = BTMac;
    }
}
