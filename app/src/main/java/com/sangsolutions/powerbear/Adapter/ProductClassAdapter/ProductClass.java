package com.sangsolutions.powerbear.Adapter.ProductClassAdapter;


public class ProductClass {
    String product_name, product_unit, product_masterId, code_p, barcode_P;


    public ProductClass(String product_name, String product_masterId) {
        this.product_name = product_name;
        this.product_masterId = product_masterId;
    }

    public ProductClass(String product_name, String product_unit, String product_masterId, String code_p, String barcode_P) {
        this.product_name = product_name;
        this.product_unit = product_unit;
        this.product_masterId = product_masterId;
        this.code_p = code_p;
        this.barcode_P = barcode_P;
    }

    public String getBarcode_P() {
        return barcode_P;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_masterId() {
        return product_masterId;
    }

    public String getCode_p() {
        return code_p;
    }


}