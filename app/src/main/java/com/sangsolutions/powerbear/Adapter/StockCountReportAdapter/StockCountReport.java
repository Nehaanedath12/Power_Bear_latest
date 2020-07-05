package com.sangsolutions.powerbear.Adapter.StockCountReportAdapter;

public class StockCountReport {
    String DocDate,iVoucherNo,Waehouse,Name,fQty,sUnit,sRemarks,Code;

    public StockCountReport(String docDate, String iVoucherNo,String Code, String waehouse, String name, String fQty, String sUnit, String sRemarks) {
        this. DocDate = docDate;
        this.iVoucherNo = iVoucherNo;
        this.Waehouse = waehouse;
        this. Name = name;
        this.Code = Code;
        this.fQty = fQty;
        this.sUnit = sUnit;
        this.sRemarks = sRemarks;
    }

    public String getDocDate() {
        return DocDate;
    }

    public void setDocDate(String docDate) {
        DocDate = docDate;
    }

    public String getiVoucherNo() {
        return iVoucherNo;
    }

    public void setiVoucherNo(String iVoucherNo) {
        this.iVoucherNo = iVoucherNo;
    }

    public String getWaehouse() {
        return Waehouse;
    }

    public void setWaehouse(String waehouse) {
        Waehouse = waehouse;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getfQty() {
        return fQty;
    }

    public void setfQty(String fQty) {
        this.fQty = fQty;
    }

    public String getsUnit() {
        return sUnit;
    }

    public void setsUnit(String sUnit) {
        this.sUnit = sUnit;
    }

    public String getsRemarks() {
        return sRemarks;
    }

    public void setsRemarks(String sRemarks) {
        this.sRemarks = sRemarks;
    }
}
