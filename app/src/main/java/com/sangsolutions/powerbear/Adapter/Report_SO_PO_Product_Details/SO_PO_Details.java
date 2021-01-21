package com.sangsolutions.powerbear.Adapter.Report_SO_PO_Product_Details;

public class SO_PO_Details {

    String customerName,VoucherNo,VoucherDate,DeliveryDate,KNNo;
    double qty;

    public SO_PO_Details(String customerName, String voucherNo, String voucherDate, double qty, String deliveryDate, String KNNo) {
        this.customerName = customerName;
        VoucherNo = voucherNo;
        VoucherDate = voucherDate;
        this.qty = qty;
        this.KNNo=KNNo;
        this.DeliveryDate=deliveryDate;
    }

}
