package com.nic.app.biharelectricitybilling.ui;

import com.nic.app.biharelectricitybilling.entity.BillDetails;

public interface AsyncResponse {
    void processFinish(BillDetails billDetails);
}
