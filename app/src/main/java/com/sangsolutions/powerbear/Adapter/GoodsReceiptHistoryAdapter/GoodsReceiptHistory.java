package com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter;

public class GoodsReceiptHistory {

    String HeaderId,Qty;

    public GoodsReceiptHistory(String headerId, String qty) {
        this.HeaderId = headerId;
        this.Qty = qty;
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
}
