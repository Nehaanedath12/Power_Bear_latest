package com.sangsolutions.powerbear.Adapter.ListProduct2;

@SuppressWarnings("ALL")
public class ListProduct {
    String Name,Code,Qty,Unit,iProduct,sRemarks;

    public ListProduct(String name, String code, String qty, String unit, String iProduct,String sRemarks) {
        this.Name = name;
        this.Code = code;
        this.Qty = qty;
        this.Unit = unit;
        this.iProduct = iProduct;
        this.sRemarks = sRemarks;
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

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getiProduct() {
        return iProduct;
    }

    public void setiProduct(String iProduct) {
        this.iProduct = iProduct;
    }

    public String getsRemarks() {
        return sRemarks;
    }

    public void setsRemarks(String sRemarks) {
        this.sRemarks = sRemarks;
    }
}
