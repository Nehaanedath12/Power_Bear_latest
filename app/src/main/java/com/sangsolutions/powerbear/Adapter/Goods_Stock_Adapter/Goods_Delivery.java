package com.sangsolutions.powerbear.Adapter.Goods_Stock_Adapter;

public class Goods_Delivery {
    String ProductName,ProductCode,Vendor,Qty,iVoucherNo;

    public Goods_Delivery(String productName, String productCode, String vendor, String qty,String iVoucherNo) {
        this.ProductName = productName;
        this.ProductCode = productCode;
        this.Vendor = vendor;
        this.Qty = qty;
        this.iVoucherNo =iVoucherNo;
    }

    public String getiVoucherNo() {
        return iVoucherNo;
    }

    public void setiVoucherNo(String iVoucherNo) {
        this.iVoucherNo = iVoucherNo;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductCode() {
        return ProductCode;
    }

    public void setProductCode(String productCode) {
        ProductCode = productCode;
    }

    public String getVendor() {
        return Vendor;
    }

    public void setVendor(String vendor) {
        Vendor = vendor;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }
}
