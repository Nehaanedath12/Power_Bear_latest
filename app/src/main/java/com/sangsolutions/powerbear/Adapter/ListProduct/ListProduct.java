package com.sangsolutions.powerbear.Adapter.ListProduct;

public class ListProduct {
    String iVoucherNo,Name,Code,Qty,PickedQty,HeaderId,Product,SiNo,Unit;

    public ListProduct(String iVoucherNo,String name, String code, String qty, String pickedQty, String headerId, String product, String siNo, String unit) {
        this.iVoucherNo =iVoucherNo;
        this.Name = name;
        this.Code = code;
        this.Qty = qty;
        this.PickedQty = pickedQty;
        this.HeaderId = headerId;
        this.Product = product;
        this.SiNo = siNo;
        this.Unit = unit;
    }

    public String getiVoucherNo() {
        return iVoucherNo;
    }

    public void setiVoucherNo(String iVoucherNo) {
        this.iVoucherNo = iVoucherNo;
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

    public String getPickedQty() {
        return PickedQty;
    }

    public void setPickedQty(String pickedQty) {
        PickedQty = pickedQty;
    }

    public String getHeaderId() {
        return HeaderId;
    }

    public void setHeaderId(String headerId) {
        HeaderId = headerId;
    }

    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }

    public String getSiNo() {
        return SiNo;
    }

    public void setSiNo(String siNo) {
        SiNo = siNo;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }
}
