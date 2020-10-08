package com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter;

@SuppressWarnings("ALL")
public class GoodsReceiptHistory {

    String DocNo,Qty, DocDate;

    public GoodsReceiptHistory(String headerId, String qty, String iVoucherNo) {
        this.DocNo = headerId;
        this.Qty = qty;
        this.DocDate = iVoucherNo;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = docNo;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getDocDate() {
        return DocDate;
    }

    public void setDocDate(String docDate) {
        this.DocDate = docDate;
    }
}
