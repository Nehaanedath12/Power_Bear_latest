package com.sangsolutions.powerbear.Adapter.CustomerAdapter;

public class Customer {

    String MasterId,Name,Code;


    public Customer(String masterId, String name, String code) {
        this.MasterId = masterId;
        this. Name = name;
        this. Code = code;
    }

    public String getMasterId() {
        return MasterId;
    }

    public void setMasterId(String masterId) {
        MasterId = masterId;
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
}
