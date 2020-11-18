package com.sangsolutions.powerbear.Adapter.WareHouseAdapter;

public class WareHouse {
    String MasterId, Name;

    public WareHouse(String masterId, String name) {
        MasterId = masterId;
        Name = name;
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
}
