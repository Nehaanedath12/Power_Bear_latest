package com.sangsolutions.powerbear.Database;

public class GoodReceiptHeader {
    String DocNo,DocDate,sSupplier,sPONo,sNarration;

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
