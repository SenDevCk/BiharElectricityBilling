package com.nic.app.biharelectricitybilling.SessionManager;

import android.hardware.usb.UsbDevice;

/**
 * Created by DELL on 1/11/2018.
 */

public class UsbDeviceObject {

    private int deviceId;
    private String devicename;
    private int vid;
    private int pid;
    private UsbDevice usbDevice;

    public UsbDeviceObject(int deviceId, String devicename, int vid, int pid, UsbDevice usbDevice) {
        this.deviceId = deviceId;
        this.devicename = devicename;
        this.vid = vid;
        this.pid = pid;
        this.usbDevice = usbDevice;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public UsbDevice getUsbDevice() {
        return usbDevice;
    }

    public void setUsbDevice(UsbDevice usbDevice) {
        this.usbDevice = usbDevice;
    }

    @Override
    public String toString() {
        return devicename;
    }
}
