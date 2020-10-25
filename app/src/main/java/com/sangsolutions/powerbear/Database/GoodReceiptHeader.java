package com.sangsolutions.powerbear.Database;


public class GoodReceiptHeader {
    String DocNo,DocDate,dProcessedDate,sSupplier,sPONo,sNarration;


    public GoodReceiptHeader(String docNo, String docDate,String dProcessedDate, String sSupplier, String sPONo, String sNarration) {
        this.DocNo = docNo;
        this.DocDate = docDate;
        this.dProcessedDate = dProcessedDate;
        this.sSupplier = sSupplier;
        this.sPONo = sPONo;
        this.sNarration = sNarration;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = docNo;
    }

    public String getDocDate() {
        return DocDate;
    }

    public void setDocDate(String docDate) {
        DocDate = docDate;
    }

    public String getdProcessedDate() {
        return dProcessedDate;
    }

    public void setdProcessedDate(String dProcessedDate) {
        this.dProcessedDate = dProcessedDate;
    }

    public String getsSupplier() {
        return sSupplier;
    }

    public void setsSupplier(String sSupplier) {
        this.sSupplier = sSupplier;
    }

    public String getsPONo() {
        return sPONo;
    }

    public void setsPONo(String sPONo) {
        this.sPONo = sPONo;
    }

    public String getsNarration() {
        return sNarration;
    }

    public void setsNarration(String sNarration) {
        this.sNarration = sNarration;
    }
}
