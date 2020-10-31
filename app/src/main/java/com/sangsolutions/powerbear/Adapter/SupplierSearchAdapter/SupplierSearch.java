package com.sangsolutions.powerbear.Adapter.SupplierSearchAdapter;

public class SupplierSearch {
    String sSupplierName,sSupplierId;

    public SupplierSearch(String sSupplierName, String sSupplierId) {
        this.sSupplierName = sSupplierName;
        this.sSupplierId = sSupplierId;
    }

    public String getsSupplierName() {
        return sSupplierName;
    }

    public void setsSupplierName(String sSupplierName) {
        this.sSupplierName = sSupplierName;
    }

    public String getsSupplierId() {
        return sSupplierId;
    }

    public void setsSupplierId(String sSupplierId) {
        this.sSupplierId = sSupplierId;
    }
}
