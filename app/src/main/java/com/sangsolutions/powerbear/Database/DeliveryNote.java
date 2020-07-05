package com.sangsolutions.powerbear.Database;

public class DeliveryNote {

    String HeaderId,SiNo,Product,Qty,iStatus,iVoucherNo;

    public String getiVoucherNo() {
        return iVoucherNo;
    }

    public void setiVoucherNo(String iVoucherNo) {
        this.iVoucherNo = iVoucherNo;
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

    public String getiStatus() {
        return iStatus;
    }

    public void setiStatus(String iStatus) {
        this.iStatus = iStatus;
    }
}
