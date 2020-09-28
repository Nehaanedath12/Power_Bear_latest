package com.sangsolutions.powerbear.Adapter.CustomerAdapter;

public class SearchCustomer {

    final String MasterId;
    String Name;
    String Code;


    public SearchCustomer(String masterId, String name, String code) {
        this.MasterId = masterId;
        this. Name = name;
        this. Code = code;
    }

    public String getMasterId() {
        return MasterId;
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
