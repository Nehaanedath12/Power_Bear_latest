package com.sangsolutions.powerbear.Adapter.Goods_Stock_Adapter;

public class Goods_Stock {
    String ProductName,ProductCode,Vendor,Qty;

    public Goods_Stock(String productName, String productCode, String vendor, String qty) {
        this.ProductName = productName;
        this.ProductCode = productCode;
        this.Vendor = vendor;
        this.Qty = qty;
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
