package com.sangsolutions.powerbear.Adapter.PendingSOAndPOReportAdapter;

@SuppressWarnings("ALL")
public class PendingSoReportOP {
String Cusomer,DocDate,DocNo,HeaderId,Product,ProductCode,ProductName,Qty,SINo,unit;


    public PendingSoReportOP(String cusomer, String docDate, String docNo, String headerId, String product, String productCode, String productName, String qty, String SINo, String unit) {
        this.Cusomer = cusomer;
        this.DocDate = docDate;
        this.DocNo = docNo;
        this.HeaderId = headerId;
        this.Product = product;
        this.ProductCode = productCode;
        this.ProductName = productName;
        this.Qty = qty;
        this.SINo = SINo;
        this.unit = unit;
    }


    public String getCusomer() {
        return Cusomer;
    }

    public void setCusomer(String cusomer) {
        Cusomer = cusomer;
    }

    public String getDocDate() {
        return DocDate;
    }

    public void setDocDate(String docDate) {
        DocDate = docDate;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = docNo;
    }

    public String getHeaderId() {
        return HeaderId;
    }

    public void setHeaderId(String headerId) {
        HeaderId = headerId;
    }

    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }

    public String getProductCode() {
        return ProductCode;
    }

    public void setProductCode(String productCode) {
        ProductCode = productCode;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getSINo() {
        return SINo;
    }

    public void setSINo(String SINo) {
        this.SINo = SINo;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
