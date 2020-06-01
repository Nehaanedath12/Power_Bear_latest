package com.sangsolutions.powerbear.Adapter.SearchProduct;

public class SearchProduct {

    String Name, Code , Barcode;

    public SearchProduct(String name, String code,String Barcode) {
        this.Name = name;
        this.Code = code;
        this.Barcode = Barcode;
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

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }
}
