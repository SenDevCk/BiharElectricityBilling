package com.nic.app.biharelectricitybilling.SessionManager;

import android.util.Log;

import com.andprn.jpos.command.ESCPOS;
import com.andprn.jpos.command.ESCPOSConst;
import com.andprn.jpos.printer.ESCPOSPrinter;
import com.andprn.port.android.DeviceConnection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Sample {
    private ESCPOSPrinter posPtr;
    private final char ESC = ESCPOS.ESC;
    private final char LF = ESCPOS.LF;

    public Sample(DeviceConnection connection) {
        posPtr = new ESCPOSPrinter(connection);
    }

    public void SampleBill() throws UnsupportedEncodingException {
        posPtr.printNormal(ESC + "|cA" + ESC + "|4C" + ESC + "|bC" + "TVS ELECTRONICS" + LF);
        posPtr.printNormal(ESC + "|cA" + ESC + "|bC" + ESC + "|2C" + "BILL" + LF);
        posPtr.printNormal(ESC + "|lA" + ESC + "|bC" + "Bill.No:01       Date: 26-12-2017 09:10" + LF);

        posPtr.printNormal("----------------------------------------" + LF);
        posPtr.printNormal("ITEM               QTY   RATE      AMT  " + LF);
        posPtr.printNormal("----------------------------------------" + LF);
        posPtr.printNormal("Flashing LED Bulb  10   120.00   1200.00" + LF);
        posPtr.printNormal("Portable LH0021K    5    60.00    300.00" + LF);
        posPtr.printNormal("Film Capacitor      8    80.00    640.00" + LF);
        posPtr.printNormal("G4 LED Bulb        20     5.00    100.00" + LF);
        posPtr.printNormal("----------------------------------------" + LF);
        posPtr.printNormal("Sub.Total                        2240.00" + LF);
        posPtr.printNormal("GST (5%)                          112.00" + LF);
        posPtr.printNormal("----------------------------------------" + LF);
        posPtr.printNormal(ESC + "|cA" + ESC + "|bC" + ESC + "|2C" + "Total Rs.  2352.00" + LF);
        posPtr.printNormal(ESC + "|cA" + ESC + "|3C" + ESC + "|bC" + "** Thank you **" + LF + LF);
        posPtr.lineFeed(3);
        // POSPrinter Only.
        posPtr.cutPaper();

    }

    public void SampleBill2() throws UnsupportedEncodingException {
        posPtr.printNormal(ESC + "|3C" + "    TVS ELECTRONICS     " + LF);
        posPtr.printNormal("          BILL          " + LF);
        posPtr.printNormal("B.No:1  26-12-2017 09:10" + LF);
        posPtr.printNormal("------------------------" + LF);
        posPtr.printNormal("ITEM   QTY RATE    AMT  " + LF);
        posPtr.printNormal("------------------------" + LF);
        posPtr.printNormal("Flashing LED Bulb       " + LF);
        posPtr.printNormal("       10 120.00 1200.00" + LF);
        posPtr.printNormal("Portable LH0021K        " + LF);
        posPtr.printNormal("        5  60.00  300.00" + LF);
        posPtr.printNormal("Film Capacitor          " + LF);
        posPtr.printNormal("        8  80.00  640.00" + LF);
        posPtr.printNormal("G4 LED Bulb             " + LF);
        posPtr.printNormal("       20   5.00  100.00" + LF);
        posPtr.printNormal("------------------------" + LF);
        posPtr.printNormal("Sub.Total        2240.00" + LF);
        posPtr.printNormal("GST (5%)          112.00" + LF);
        posPtr.printNormal("------------------------" + LF);
        posPtr.printNormal("   Total Rs.  2352.00   " + LF);
        posPtr.printNormal("------------------------" + LF);
        posPtr.printNormal("        THANK YOU       " + LF);
        posPtr.printNormal("                         " + LF);
    }

    public int Sample4() throws IOException {
        int check = posPtr.printerCheck();
        if (check == ESCPOSConst.SUCCESS) {
            Log.i("Sample", "sts= " + posPtr.status());
            return posPtr.status();
        } else {
            Log.i("Sample", "Retrieve Status Failed");
            return -1;
        }
    }

    public int Sample5() throws IOException, InterruptedException {
        return posPtr.printerSts();
    }


}
