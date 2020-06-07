package com.sangsolutions.powerbear.Adapter.POAdapter;

public class PO {

    String DocNo,DocDate,Cusomer,HeaderId,SINo,Product,Qty,unit;

    public PO(String docNo, String docDate, String cusomer, String headerId, String SINo, String product, String qty, String unit) {
        this.DocNo = docNo;
        this.DocDate = docDate;
        this.Cusomer = cusomer;
        this.HeaderId = headerId;
        this.SINo = SINo;
        this.Product = product;
        this.Qty = qty;
        this.unit = unit;
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

    public String getCusomer() {
        return Cusomer;
    }

    public void setCusomer(String cusomer) {
        Cusomer = cusomer;
    }

    public String getHeaderId() {
        return HeaderId;
    }

    public void setHeaderId(String headerId) {
        HeaderId = headerId;
    }

    public String getSINo() {
        return SINo;
    }

    public void setSINo(String SINo) {
        this.SINo = SINo;
    }

    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
