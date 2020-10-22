package com.sangsolutions.powerbear.Adapter.RemarksTypeAdapter;

public class RemarksType {
    String iId,sName;

    public RemarksType(String iId, String sName) {
        this.iId = iId;
        this.sName = sName;
    }

    public String getiId() {
        return iId;
    }

    public void setiId(String iId) {
        this.iId = iId;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }
}
