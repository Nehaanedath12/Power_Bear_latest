package com.sangsolutions.powerbear.Database;

public class PendingSO {

    private String DocNo,DocDate,HeaderId,SiNo,iCustomer,Customer,Product,Qty,unit;


    public String getiCustomer() {
        return iCustomer;
    }

    public void setiCustomer(String iCustomer) {
        this.iCustomer = iCustomer;
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

    public String getHeaderId() {
        return HeaderId;
    }

    public void setHeaderId(String headerId) {
        HeaderId = headerId;
    }

    public String getSiNo() {
        return SiNo;
    }

    public void setSiNo(String siNo) {
        SiNo = siNo;
    }

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
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
