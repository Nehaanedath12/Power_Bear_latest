package com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter;

@SuppressWarnings("ALL")
public class DeliveryNoteHistory {

    String DocDate,Qty,iVoucherNo;

    public DeliveryNoteHistory(String DocDate, String qty,String iVoucherNo) {
        this.DocDate = DocDate;
        this.Qty = qty;
        this.iVoucherNo = iVoucherNo;
    }


    public String getDocDate() {
        return DocDate;
    }

    public void setDocDate(String docDate) {
        DocDate = docDate;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getiVoucherNo() {
        return iVoucherNo;
    }

    public void setiVoucherNo(String iVoucherNo) {
        this.iVoucherNo = iVoucherNo;
    }
}
