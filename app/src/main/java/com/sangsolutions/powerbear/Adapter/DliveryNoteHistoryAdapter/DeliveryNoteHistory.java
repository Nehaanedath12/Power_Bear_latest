package com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter;

public class DeliveryNoteHistory {

    String HeaderId,Qty,iVoucherNo;

    public DeliveryNoteHistory(String headerId, String qty,String iVoucherNo) {
        this.HeaderId = headerId;
        this.Qty = qty;
        this.iVoucherNo = iVoucherNo;
    }


    public String getHeaderId() {
        return HeaderId;
    }

    public void setHeaderId(String headerId) {
        HeaderId = headerId;
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
