package com.sangsolutions.powerbear.Adapter.ProductAdapter;

public class Product {
    String Name,Code,MasterId;

    public Product(String name, String code, String masterId) {
        this.Name = name;
        this.Code = code;
        this.MasterId = masterId;
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

    public String getMasterId() {
        return MasterId;
    }

    public void setMasterId(String masterId) {
        MasterId = masterId;
    }
}
