package com.sangsolutions.powerbear.Adapter.ListProduct;

public class ListProduct {
    String Name,Code,Qty,PickedQty,HeaderId,Product,SiNo;

    public ListProduct(String name, String code, String qty, String pickedQty, String headerId, String product, String siNo) {
        Name = name;
        Code = code;
        Qty = qty;
        PickedQty = pickedQty;
        HeaderId = headerId;
        Product = product;
        SiNo = siNo;
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
}
