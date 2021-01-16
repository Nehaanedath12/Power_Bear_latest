package com.sangsolutions.powerbear.Adapter.Report_SO_PO_Product_Details;

public class SO_PO_Details {

    String customerName,VoucherNo,VoucherDate;
    double qty;

    public SO_PO_Details(String customerName, String voucherNo, String voucherDate, double qty) {
        this.customerName = customerName;
        VoucherNo = voucherNo;
        VoucherDate = voucherDate;
        this.qty = qty;
    }

}
